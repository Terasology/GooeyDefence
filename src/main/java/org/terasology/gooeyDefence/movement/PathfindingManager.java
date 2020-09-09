// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.movement;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.registry.In;
import org.terasology.engine.registry.Share;
import org.terasology.engine.world.OnChangedBlock;
import org.terasology.engine.world.WorldProvider;
import org.terasology.engine.world.block.entity.placement.PlaceBlocks;
import org.terasology.flexiblepathfinding.JPSConfig;
import org.terasology.flexiblepathfinding.PathfinderSystem;
import org.terasology.gooeyDefence.DefenceField;
import org.terasology.gooeyDefence.EnemyManager;
import org.terasology.gooeyDefence.events.OnEntrancePathCalculated;
import org.terasology.gooeyDefence.events.OnFieldActivated;
import org.terasology.gooeyDefence.movement.components.BlankPathComponent;
import org.terasology.gooeyDefence.movement.components.CustomPathComponent;
import org.terasology.gooeyDefence.movement.events.RepathEnemyRequest;
import org.terasology.math.geom.Vector3i;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Handles calculation and storage of paths
 * <p>
 * Does not move any entities, this is delegated to the {@link MovementSystem} & {@link EnemyManager}
 *
 * @see MovementSystem
 * @see EnemyManager
 */
@Share(PathfindingManager.class)
@RegisterSystem
public class PathfindingManager extends BaseComponentSystem {
    /**
     * How long the pathfinding system should try and find a path for before giving up. In seconds.
     */
    private static final float PATHFINDING_TIMEOUT = 10.f;
    /**
     * Any entities that require re-pathing.
     */
    private final Set<EntityRef> queuedEnemies = new HashSet<>();
    @In
    private PathfinderSystem pathfinderSystem;
    @In
    private WorldProvider worldProvider;
    /**
     * The paths from each of the entrances to the shrine
     */
    private List<List<Vector3i>> paths;

    @Override
    public void preBegin() {
        paths = new ArrayList<>(Collections.nCopies(DefenceField.entranceCount, null));
    }

    /**
     * Begins the pathfinding calculations.
     * <p>
     * Called when the field is activated
     *
     * @see OnFieldActivated
     */
    @ReceiveEvent
    public void onFieldActivated(OnFieldActivated event, EntityRef entity) {
        for (int id = 0; id < DefenceField.entranceCount; id++) {
            event.beginTask();
            calculatePath(id, event::finishTask);
        }
    }


    /**
     * Update path on a block placed.
     * <p>
     * This is only run when the field is activated to avoid the reset triggering it.
     */
    @ReceiveEvent
    public void onPlaceBlocks(PlaceBlocks event, EntityRef entity) {
        if (DefenceField.fieldActivated) {
            calculatePaths();
        }
    }

    /**
     * Update path on a block removed.
     * <p>
     * This is only run when the field is activated to avoid the reset triggering it.
     */
    @ReceiveEvent
    public void onChangedBlock(OnChangedBlock event, EntityRef entity) {
        if (DefenceField.fieldActivated) {
            calculatePaths();
        }
    }

    /**
     * Called to request an enemy be re-pathed. Prevents the path on an enemy being set multiple times
     * <p>
     * Filters on {@link LocationComponent}
     *
     * @see RepathEnemyRequest
     */
    @ReceiveEvent
    public void onRepathEnemyRequest(RepathEnemyRequest event, EntityRef entity, LocationComponent locationComponent) {
        queuedEnemies.add(entity);
        calculatePath(buildJpsConfig(new Vector3i(locationComponent.getWorldPosition())),
                path -> {
                    if (!path.isEmpty() && queuedEnemies.contains(entity)) {
                        CustomPathComponent customPathComponent = new CustomPathComponent(path);
                        entity.addComponent(customPathComponent);
                        entity.removeComponent(BlankPathComponent.class);
                        queuedEnemies.remove(entity);
                    }
                });
    }

    /**
     * Calculate the path from an entrance to the centre. This callback is not invoked with the path as an argument.
     *
     * @param id The entrance to calculate from
     * @param callback A callback to be invoked after the path calculation has finished.
     */
    private void calculatePath(int id, Runnable callback) {
        calculatePath(buildJpsConfig(DefenceField.entrancePos(id)),
                (path) -> {
                    List<Vector3i> oldPath = paths.get(id);
                    paths.set(id, path);
                    if (!path.equals(oldPath)) {
                        DefenceField.getShrineEntity().send(new OnEntrancePathCalculated(id, path));
                    }
                    if (callback != null) {
                        callback.run();
                    }
                });
    }

    /**
     * Calculate the path given the config and the callback. This callback is called with the path as a parameter
     *
     * @param config The config to use for the pathfinding.
     * @param callback The callback to be used once the path is found.
     */
    private void calculatePath(JPSConfig config, Consumer<List<Vector3i>> callback) {
        pathfinderSystem.requestPath(config, (path, end) -> {
            /* In order to make the path use zero as the end, we need to flip it. */
            Collections.reverse(path);
            callback.accept(path);
        });

    }

    /**
     * Produces a config to be used for pathfinding. Sets the path to run from the given position to the shrine.
     *
     * @param start The starting position of the path.
     * @return A new JPSConfig for the path.
     */
    private JPSConfig buildJpsConfig(Vector3i start) {
        JPSConfig result = new JPSConfig();
        result.start = start;
        result.stop = DefenceField.FIELD_CENTRE;
        result.maxDepth = DefenceField.outerRingSize * 2;
        //TODO: Replace width and height with values from enemy.
        result.plugin = new EnemyWalkingPlugin(worldProvider, 0.5f, 0.5f);
        result.maxTime = PATHFINDING_TIMEOUT;
        return result;
    }

    /**
     * Calculate paths from all the entrances to the centre.
     */
    private void calculatePaths() {
        for (int id = 0; id < DefenceField.entranceCount; id++) {
            calculatePath(id, null);
        }
    }

    /**
     * @return All paths from entrance to centre
     */
    public List<List<Vector3i>> getPaths() {
        return paths;
    }

    /**
     * Get a path. Will return null if the path has not been calculated yet.
     * <p>
     * An empty path either indicates that no path could be calculated or that the entrance and shrine are located at
     * the same position
     *
     * @param pathID Which entrance the path should come from
     * @return The given path, or null if it doesn't exist yet.
     */
    public List<Vector3i> getPath(int pathID) {
        return paths.get(pathID);
    }
}
