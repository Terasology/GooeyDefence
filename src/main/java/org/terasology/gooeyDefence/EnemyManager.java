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
import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.gooeyDefence.components.enemies.EntrancePathComponent;
import org.terasology.gooeyDefence.components.enemies.GooeyComponent;
import org.terasology.gooeyDefence.components.enemies.MovementComponent;
import org.terasology.gooeyDefence.components.enemies.PathComponent;
import org.terasology.gooeyDefence.events.DamageShrineEvent;
import org.terasology.gooeyDefence.events.OnFieldActivated;
import org.terasology.gooeyDefence.events.OnEntrancePathChanged;
import org.terasology.gooeyDefence.events.RepathEnemyRequest;
import org.terasology.logic.common.ActivateEvent;
import org.terasology.logic.delay.DelayManager;
import org.terasology.logic.delay.PeriodicActionTriggeredEvent;
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
public class EnemyManager extends BaseComponentSystem implements UpdateSubscriberSystem {
    private static final Logger logger = LoggerFactory.getLogger(EnemyManager.class);

    private Set<EntityRef> enemies = new HashSet<>();
    private Set<EntityRef> enemiesToRemove = new HashSet<>();

    @In
    private EntityManager entityManager;
    @In
    private PathfindingSystem pathfindingSystem;
    @In
    private DelayManager delayManager;

    /**
     * Get the PathComponent used on the entity.
     * <p>
     * An entity should only have one implementation of the path component.
     * As such, this method makes no guarantee about which component will be returned if the
     * entity has multiple implementations.
     *
     * @param entity The entity to look on
     * @return The first path component found on the entity.
     * @throws IllegalArgumentException If the entity lacks a PathComponent
     */
    public static PathComponent getPathComponent(EntityRef entity) {
        for (Component component : entity.iterateComponents()) {
            if (component instanceof PathComponent) {
                return (PathComponent) component;
            }
        }
        throw new IllegalArgumentException("Path Component requested on entity that lacks one.");
    }

    /**
     * Called when the field is activated.
     * Clears the enemy store and re-scans for any enemies.
     */
    @ReceiveEvent
    public void onFieldActivated(OnFieldActivated event, EntityRef entity) {
        enemies.clear();
        entityManager.getEntitiesWith(GooeyComponent.class).forEach(enemies::add);
        enemies.stream().filter(enemy -> enemy.hasComponent(EntrancePathComponent.class))
                .forEach(enemy -> enemy.getComponent(EntrancePathComponent.class).setPathManager(pathfindingSystem));

        //delayManager.addPeriodicAction(DefenceField.getShrineEntity(), "SpawnEnemyEvent", 500, 500);
    }

    @ReceiveEvent
    public void onActivate(ActivateEvent event, EntityRef entity) {
        for (int i = 0; i < DefenceField.entranceCount(); i++) {
            spawnEnemy(i);
        }
    }

    @ReceiveEvent
    public void onPeriodicActionTriggered(PeriodicActionTriggeredEvent event, EntityRef entity) {
        if (event.getActionId().equals("SpawnEnemyEvent")) {
            for (int i = 0; i < DefenceField.entranceCount(); i++) {
                spawnEnemy(i);
            }
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
                    return;
                }
            }

            /* Check if the goal is on the new path */
            PathComponent pathComponent = getPathComponent(enemy);
            Vector3i goal = pathComponent.getGoal();
            List<Vector3i> newPath = event.getNewPath();
            if (newPath.contains(goal)) {
                /* Add a entrance component starting at the given position */
                enemy.removeComponent(pathComponent.getClass());
                EntrancePathComponent entranceComponent = new EntrancePathComponent(
                        event.getPathId(),
                        pathfindingSystem,
                        newPath.indexOf(goal));
                enemy.addComponent(entranceComponent);
            } else {
                /* It's had its path change and it isn't on the new path */
                enemy.send(new RepathEnemyRequest());
            }
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

        EntityRef entity = entityManager.create("GooeyDefence:Gooey", DefenceField.entrancePos(entranceNumber).toVector3f());

        /* Setup pathfinding component */
        EntrancePathComponent component = new EntrancePathComponent(entranceNumber, pathfindingSystem);
        entity.addComponent(component);

        enemies.add(entity);
    }

    /**
     * Destroys an enemy, ensuring that all references to it in the system are handled.
     *
     * @param enemy The enemy to destroy
     */
    public void destroyEnemy(EntityRef enemy) {
        enemiesToRemove.add(enemy);
        enemy.destroy();
    }

    /**
     * Obtain all the enemies that are within range of the given position.
     *
     * @param pos   The position to look for
     * @param range The range to search in.
     * @return A set of all enemies found within this range.
     */
    public Set<EntityRef> getEnemiesInRange(Vector3f pos, int range) {
        int rangeSqr = range * range;
        Set<EntityRef> result = new HashSet<>();
        for (EntityRef enemy : enemies) {
            Vector3f enemyPos = enemy.getComponent(LocationComponent.class).getWorldPosition();
            if (enemyPos.distanceSquared(pos) <= rangeSqr) {
                result.add(enemy);
            }
        }
        return result;
    }

    @Override
    public void update(float delta) {
        if (DefenceField.isFieldActivated()) {
            enemies.forEach(entity -> moveEnemyAlongPath(entity, delta));
            enemiesToRemove.forEach(enemies::remove);
            enemiesToRemove.clear();
        }
    }

    /**
     * Moves an enemy one step along it's path.
     * Also handles the enemy reaching the end of the path.
     *
     * @param entity the enemy to move
     */
    private void moveEnemyAlongPath(EntityRef entity, float delta) {
        PathComponent pathComponent = getPathComponent(entity);
        LocationComponent locationComponent = entity.getComponent(LocationComponent.class);

        float distSqr = locationComponent.getWorldPosition().distanceSquared(pathComponent.getGoal().toVector3f());
        if (distSqr < 0.1f) {
            updateToNextStep(entity, pathComponent);
        } else {
            moveEnemyTowardsGoal(entity, pathComponent, locationComponent, delta);
        }
    }

    /**
     * Handles the entity reaching the goal.
     * Either marks the entity as having reached the end of the path or picks the next element in the path as the goal.
     *
     * @param entity        The entity to update
     * @param pathComponent The GooeyComponent of the entity
     */
    private void updateToNextStep(EntityRef entity, PathComponent pathComponent) {
        if (pathComponent.atEnd()) {
            GooeyComponent gooeyComponent = entity.getComponent(GooeyComponent.class);
            entity.send(new DamageShrineEvent(gooeyComponent.damage));
            destroyEnemy(entity);
        } else {
            pathComponent.nextStep();
        }
    }


    /**
     * Moves the entity towards it's goal.
     *
     * @param entity            The entity to move
     * @param pathComponent     The GooeyComponent of the entity
     * @param locationComponent The LocationComponent of the entity
     * @param delta             The time elapsed since the last call (in ms)
     */
    private void moveEnemyTowardsGoal(EntityRef entity, PathComponent pathComponent, LocationComponent locationComponent, float delta) {
        MovementComponent movementComponent = entity.getComponent(MovementComponent.class);
        /* Calculate required heading */
        Vector3f target = pathComponent.getGoal().toVector3f();
        target.sub(locationComponent.getWorldPosition());
        target.normalize();
        /* Scale to the speed */
        target.scale(movementComponent.getSpeed() * delta);
        /* Move the enemy */
        locationComponent.setWorldPosition(locationComponent.getWorldPosition().add(target));
        entity.saveComponent(locationComponent);
    }

}
