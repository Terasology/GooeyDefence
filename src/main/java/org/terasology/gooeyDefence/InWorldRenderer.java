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

import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.RenderSystem;
import org.terasology.gooeyDefence.components.PathBlockComponent;
import org.terasology.gooeyDefence.components.ShrineComponent;
import org.terasology.gooeyDefence.components.TargeterBulletComponent;
import org.terasology.gooeyDefence.events.OnEntrancePathChanged;
import org.terasology.gooeyDefence.events.health.DamageEntityEvent;
import org.terasology.gooeyDefence.movement.PathfindingManager;
import org.terasology.gooeyDefence.movement.components.MovementComponent;
import org.terasology.gooeyDefence.movement.events.ReachedGoalEvent;
import org.terasology.gooeyDefence.towerBlocks.base.TowerTargeter;
import org.terasology.logic.location.LocationComponent;
import org.terasology.logic.players.PlayerTargetChangedEvent;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;
import org.terasology.particles.components.generators.VelocityRangeGeneratorComponent;
import org.terasology.registry.In;
import org.terasology.registry.Share;
import org.terasology.rendering.logic.MeshComponent;
import org.terasology.rendering.world.selection.BlockSelectionRenderer;
import org.terasology.utilities.Assets;

import java.util.List;

/**
 * Handles misc rendering duties for the module.
 * These involve rendering stuff in world for systems that provide functionality.
 */
@RegisterSystem
@Share(InWorldRenderer.class)
public class InWorldRenderer extends BaseComponentSystem implements RenderSystem {

    private static final Vector3f outOfSightPos = new Vector3f(0, -3, 0);
    private BlockSelectionRenderer shrineDamageRenderer;
    @In
    private Time time;
    @In
    private PathfindingManager pathfindingManager;
    @In
    private EntityManager entityManager;

    private int shrineDamaged = 0;
    private EntityRef sphere;

    @Override
    public void initialise() {
        shrineDamageRenderer = new BlockSelectionRenderer(Assets.getTexture("GooeyDefence:ShrineDamaged").get());
    }

    @Override
    public void postBegin() {
        sphere = entityManager.create("GooeyDefence:Sphere");
        LocationComponent sphereLoc = sphere.getComponent(LocationComponent.class);

        sphereLoc.setWorldPosition(outOfSightPos);
        sphereLoc.setLocalScale(0.1f);
        clearPathParticles();
    }

    /**
     * Called when the shrine is damaged
     * Filters on {@link ShrineComponent}
     *
     * @see DamageEntityEvent
     */
    @ReceiveEvent(components = ShrineComponent.class)
    public void onDamageShrine(DamageEntityEvent event, EntityRef entity) {
        shrineDamaged = 100;
    }

    /**
     * Called whenever an entrance path is changed.
     * Used to re-create the path display entities.
     *
     * @see OnEntrancePathChanged
     */
    @ReceiveEvent
    public void onEntrancePathChanged(OnEntrancePathChanged event, EntityRef entity) {
        clearPathParticles();
        List<List<Vector3i>> paths = pathfindingManager.getPaths();
        for (List<Vector3i> path : paths) {
            if (path != null) {
                for (int i = path.size() - 1; i >= 2; i--) {
                    EntityRef pathBlock = entityManager.create("GooeyDefence:PathDisplay", path.get(i).toVector3f());

                    VelocityRangeGeneratorComponent component = pathBlock.getComponent(VelocityRangeGeneratorComponent.class);

                    component.minVelocity = Vector3f.one().scale(-0.5f);
                    component.maxVelocity = Vector3f.one().scale(0.5f);

                    Vector3f delta = path.get(i - 1).toVector3f().sub(path.get(i).toVector3f());
                    component.minVelocity.add(delta);
                    component.maxVelocity.add(delta);

                }
            }
        }
    }

    /**
     * Called when the block or entity being targeted by the player changes.
     * Used to render the range visuals for targeters
     *
     * @see PlayerTargetChangedEvent
     */
    @ReceiveEvent
    public void onPlayerTargetChanged(PlayerTargetChangedEvent event, EntityRef entity) {
        if (DefenceField.hasComponentExtending(event.getNewTarget(), TowerTargeter.class)) {
            LocationComponent sphereLoc = sphere.getComponent(LocationComponent.class);
            LocationComponent targeterLoc = event.getNewTarget().getComponent(LocationComponent.class);
            TowerTargeter targeter = DefenceField.getComponentExtending(event.getNewTarget(), TowerTargeter.class);

            sphereLoc.setWorldPosition(targeterLoc.getWorldPosition());
            sphereLoc.setLocalScale(targeter.getRange() * 2 + 1);

        } else {
            LocationComponent sphereLoc = sphere.getComponent(LocationComponent.class);

            sphereLoc.setWorldPosition(outOfSightPos);
            sphereLoc.setLocalScale(0.1f);
        }
    }

    /**
     * Removes all entities used to display the paths.
     */
    private void clearPathParticles() {
        for (EntityRef blockEntity : entityManager.getEntitiesWith(PathBlockComponent.class)) {
            blockEntity.destroy();
        }
    }

    @Override
    public void renderAlphaBlend() {
        shrineDamageRenderer.beginRenderOverlay();
        if (shrineDamaged > 0) {
            shrineDamaged -= time.getGameDeltaInMs();
            for (Vector3i pos : DefenceField.getShrine()) {
                shrineDamageRenderer.renderMark2(pos);
            }
        }
        shrineDamageRenderer.endRenderOverlay();
    }

    public void shootBulletTowards(Vector3f goal, Vector3f start) {
        EntityRef bullet = entityManager.create("GooeyDefence:Bullet");
        MovementComponent movementComponent = new MovementComponent();
        movementComponent.setGoal(goal);
        movementComponent.setSpeed(30);
        movementComponent.setReachedDistance(0.5f);
        bullet.addOrSaveComponent(movementComponent);

        LocationComponent locationComponent = bullet.getComponent(LocationComponent.class);
        locationComponent.setWorldPosition(start);
    }

    /**
     * Filters on {@link TargeterBulletComponent}
     *
     * @see ReachedGoalEvent
     */
    @ReceiveEvent(components = TargeterBulletComponent.class)
    public void onReachedGoal(ReachedGoalEvent event, EntityRef entity) {
        entity.removeComponent(MeshComponent.class);
    }

    @Override
    public void renderOpaque() {
    }

    @Override
    public void renderOverlay() {

    }

    @Override
    public void renderShadows() {

    }
}
