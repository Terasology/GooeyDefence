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
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.gooeyDefence.components.enemies.GooeyComponent;
import org.terasology.gooeyDefence.events.OnEntrancePathChanged;
import org.terasology.gooeyDefence.events.OnFieldActivated;
import org.terasology.gooeyDefence.events.health.DamageEntityEvent;
import org.terasology.gooeyDefence.events.health.EntityDeathEvent;
import org.terasology.gooeyDefence.movement.PathfindingManager;
import org.terasology.gooeyDefence.movement.components.BlankPathComponent;
import org.terasology.gooeyDefence.movement.components.EntrancePathComponent;
import org.terasology.gooeyDefence.movement.components.MovementComponent;
import org.terasology.gooeyDefence.movement.components.PathComponent;
import org.terasology.gooeyDefence.movement.events.ReachedGoalEvent;
import org.terasology.gooeyDefence.movement.events.RepathEnemyRequest;
import org.terasology.logic.common.ActivateEvent;
import org.terasology.logic.delay.DelayManager;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.In;
import org.terasology.registry.Share;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Handles all enemy based actions. Is controlled by {@link DefenceWorldManager}.
 */
@Share(EnemyManager.class)
@RegisterSystem
public class EnemyManager extends BaseComponentSystem {
    private static final Logger logger = LoggerFactory.getLogger(EnemyManager.class);

    private Set<EntityRef> enemies = new HashSet<>();

    @In
    private EntityManager entityManager;
    @In
    private PathfindingManager pathfindingManager;
    @In
    private DelayManager delayManager;

    /**
     * Called when the field is activated.
     * Clears the enemy store and re-scans for any enemies.
     */
    @ReceiveEvent
    public void onFieldActivated(OnFieldActivated event, EntityRef entity) {
        enemies.clear();
        entityManager.getEntitiesWith(GooeyComponent.class).forEach(enemies::add);

        /* Reset path components to blank ones which will freeze the enemy in place until paths are calculated */
        enemies.forEach(enemy -> enemy.removeComponent(DefenceField.getComponentExtending(enemy, PathComponent.class).getClass()));
        enemies.forEach(enemy -> enemy.addComponent(new BlankPathComponent(enemy.getComponent(MovementComponent.class).getGoal())));

    }

    /*
     * Test handler to allow easy enemy spawning
     */
    @ReceiveEvent
    public void onActivate(ActivateEvent event, EntityRef entity) {
        for (int i = 0; i < DefenceField.entranceCount(); i++) {
            spawnEnemy(i);
        }
    }

    /**
     * Called when the a path is changed.
     *
     * @see OnEntrancePathChanged
     */
    @ReceiveEvent
    public void onPathChanged(OnEntrancePathChanged event, EntityRef shrineEntity) {
        for (EntityRef enemy : enemies) {
            /* Firstly check if the enemy is on an unchanged path */
            if (enemy.hasComponent(EntrancePathComponent.class)) {
                if (enemy.getComponent(EntrancePathComponent.class).getEntranceId() != event.getPathId()) {
                    continue;
                }
            }

            /* Check if the goal is on the new path */
            MovementComponent movementComponent = enemy.getComponent(MovementComponent.class);
            Vector3f goal = movementComponent.getGoal();
            List<Vector3i> newPath = event.getNewPath();

            enemy.removeComponent(DefenceField.getComponentExtending(enemy, PathComponent.class).getClass());

            if (newPath.contains(goal)) {
                /* Add a entrance component starting at the given position */
                EntrancePathComponent entranceComponent = new EntrancePathComponent(
                        event.getPathId(),
                        pathfindingManager,
                        newPath.indexOf(goal));
                enemy.addComponent(entranceComponent);
            } else {
                /* Enemy isn't on the new path, so we have to calculate it's own path. */
                enemy.addComponent(new BlankPathComponent(goal));
                enemy.send(new RepathEnemyRequest());
            }
        }
    }

    /**
     * Called when an entity reaches zero health.
     * Filters on {@link GooeyComponent}
     *
     * @see EntityDeathEvent
     */
    @ReceiveEvent
    public void onEntityDeath(EntityDeathEvent event, EntityRef entity, GooeyComponent component) {
        destroyEnemy(entity);
    }

    /**
     * Called when an enemy reaches it's movement goal. Set the next goal and consumes the event.
     * <p>
     * Filters on {@link GooeyComponent}
     *
     * @see ReachedGoalEvent
     */
    @ReceiveEvent
    public void onReachedGoal(ReachedGoalEvent event, EntityRef entity, GooeyComponent gooeyComponent) {
        event.consume();
        PathComponent pathComponent = DefenceField.getComponentExtending(entity, PathComponent.class);
        if (pathComponent.atEnd()) {
            entity.send(new DamageEntityEvent(gooeyComponent.damage));
            destroyEnemy(entity);
        } else {
            pathComponent.nextStep();
            MovementComponent component = entity.getComponent(MovementComponent.class);
            component.setGoal(pathComponent.getGoal());
        }
    }

    /**
     * Spawns an enemy at the given entrance.
     * Also begins it travelling down the path.
     *
     * @param entranceNumber The entrance to spawn at
     */
    public void spawnEnemy(int entranceNumber) {
        if (!DefenceField.isFieldActivated()) {
            return;
        }

        EntityRef entity = entityManager.create("GooeyDefence:BasicEnemy", DefenceField.entrancePos(entranceNumber).toVector3f());

        /* Setup pathfinding component */
        EntrancePathComponent component = new EntrancePathComponent(entranceNumber, pathfindingManager);
        entity.addComponent(component);
        /* Setup movement component */
        MovementComponent movementComponent = entity.getComponent(MovementComponent.class);
        movementComponent.setGoal(component.getGoal());

        enemies.add(entity);
    }

    /**
     * Destroys an enemy, ensuring that all references to it in the system are handled.
     *
     * @param enemy The enemy to destroy
     */
    public void destroyEnemy(EntityRef enemy) {
        enemies.remove(enemy);
        enemy.destroy();
    }

    /**
     * Obtain all the enemies that are within range of the given position.
     *
     * @param pos   The position to look for
     * @param range The range to search in.
     * @return A set of all enemies found within this range.
     */
    public Set<EntityRef> getEnemiesInRange(Vector3f pos, float range) {
        float rangeSqr = range * range;
        Set<EntityRef> result = new HashSet<>();
        for (EntityRef enemy : enemies) {
            Vector3f enemyPos = enemy.getComponent(LocationComponent.class).getWorldPosition();
            if (enemyPos.distanceSquared(pos) <= rangeSqr) {
                result.add(enemy);
            }
        }
        return result;
    }

}
