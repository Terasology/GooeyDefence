/*
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.gooeyDefence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.flexiblepathfinding.JPSConfig;
import org.terasology.flexiblepathfinding.PathfinderSystem;
import org.terasology.gooeyDefence.components.enemies.BlankPathComponent;
import org.terasology.gooeyDefence.components.enemies.CustomPathComponent;
import org.terasology.gooeyDefence.components.enemies.PathComponent;
import org.terasology.gooeyDefence.events.OnEntrancePathChanged;
import org.terasology.gooeyDefence.events.OnFieldActivated;
import org.terasology.gooeyDefence.events.RepathEnemyRequest;
import org.terasology.gooeyDefence.pathfinding.EnemyWalkingPlugin;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.In;
import org.terasology.registry.Share;
import org.terasology.world.OnChangedBlock;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.entity.placement.PlaceBlocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@Share(PathfindingManager.class)
@RegisterSystem
public class PathfindingManager extends BaseComponentSystem {
    private static final Logger logger = LoggerFactory.getLogger(PathfindingManager.class);
    /**
     * How long the pathfinding system should try and find a path for before giving up.
     * In seconds.
     */
    private static final float PATHFINDING_TIMEOUT = 10.f;

    @In
    private PathfinderSystem pathfinderSystem;
    private List<List<Vector3i>> paths = new ArrayList<>(Collections.nCopies(DefenceField.entranceCount(), null));
    @In
    private WorldProvider worldProvider;


    /**
     * Called to initialise the field.
     */
    @ReceiveEvent
    public void onFieldActivated(OnFieldActivated event, EntityRef entity) {
        for (int id = 0; id < DefenceField.entranceCount(); id++) {
            event.beginTask();
            calculatePath(id, event::finishTask);
        }
    }


    /**
     * Update path on a block placed
     */
    @ReceiveEvent
    public void onPlaceBlocks(PlaceBlocks event, EntityRef entity) {
        if (DefenceField.isFieldActivated()) {
            calculatePaths();
        }
    }

    /**
     * Update path on a block removed
     */
    @ReceiveEvent
    public void onChangedBlock(OnChangedBlock event, EntityRef entity) {
        if (DefenceField.isFieldActivated()) {
            calculatePaths();
        }
    }

    /**
     * Called to request an enemy be re-pathed.
     *
     * @see RepathEnemyRequest
     */
    @ReceiveEvent
    public void onRepathEnemyRequest(RepathEnemyRequest event, EntityRef entity, LocationComponent locationComponent) {
        /* Pause the enemy */
        entity.removeComponent(DefenceField.getComponentExtending(entity, PathComponent.class).getClass());
        entity.addComponent(new BlankPathComponent(new Vector3i(locationComponent.getWorldPosition())));

        /* Process its path */
        calculatePath(buildJpsConfig(new Vector3i(locationComponent.getWorldPosition())),
                path -> {
                    if (!path.isEmpty()) {
                        CustomPathComponent customPathComponent = new CustomPathComponent(path);
                        entity.addComponent(customPathComponent);
                        entity.removeComponent(BlankPathComponent.class);
                    }
                });
    }

    /**
     * Calculate the path from an entrance to the centre
     *
     * @param id       The entrance to calculate from
     * @param callback A callback to be invoked after the path calculation has finished.
     */
    private void calculatePath(int id, Runnable callback) {
        calculatePath(buildJpsConfig(DefenceField.entrancePos(id)),
                (path) -> {
                    List<Vector3i> oldPath = paths.get(id);
                    paths.set(id, path);
                    if (oldPath != null && !oldPath.equals(path)) {
                        DefenceField.getShrineEntity().send(new OnEntrancePathChanged(id, path));
                    }
                    if (callback != null) {
                        callback.run();
                    }
                });
    }

    /**
     * Calculate the path given the config and the callback.
     *
     * @param config   The config to use for the pathfinding.
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
     * Produces a config to be used for pathfinding.
     * Sets the path to run from the given position to the shrine.
     *
     * @param start The starting position of the path.
     * @return A new JPSConfig for the path.
     */
    private JPSConfig buildJpsConfig(Vector3i start) {
        JPSConfig result = new JPSConfig();
        result.start = start;
        result.stop = DefenceField.fieldCentre();
        result.maxDepth = DefenceField.outerRingSize() * 2;
        //TODO: Replace width and height with values from enemy.
        result.plugin = new EnemyWalkingPlugin(worldProvider, 0.5f, 0.5f);
        result.maxTime = PATHFINDING_TIMEOUT;
        return result;
    }

    /**
     * Calculate paths from all the entrances to the centre.
     */
    private void calculatePaths() {
        for (int id = 0; id < DefenceField.entranceCount(); id++) {
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
     *
     * @param pathID Which entrance the path should come from
     * @return The given path, or null if it doesn't exist yet.
     */
    public List<Vector3i> getPath(int pathID) {
        return paths.get(pathID);
    }
}
