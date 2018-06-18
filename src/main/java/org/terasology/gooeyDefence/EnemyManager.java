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
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.gooeyDefence.components.enemies.CustomPathComponent;
import org.terasology.gooeyDefence.components.enemies.EntrancePathComponent;
import org.terasology.gooeyDefence.components.enemies.GooeyComponent;
import org.terasology.gooeyDefence.components.enemies.MovementComponent;
import org.terasology.gooeyDefence.components.enemies.PathComponent;
import org.terasology.gooeyDefence.events.DamageShrineEvent;
import org.terasology.gooeyDefence.events.OnFieldActivated;
import org.terasology.gooeyDefence.events.OnPathChanged;
import org.terasology.logic.common.ActivateEvent;
import org.terasology.logic.delay.DelayManager;
import org.terasology.logic.delay.PeriodicActionTriggeredEvent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3f;
import org.terasology.registry.In;
import org.terasology.registry.Share;

import java.util.HashSet;
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

    public static PathComponent getPathComponent(EntityRef entity) {
        //TODO: Make this less ugly. Dynamically obtain path components via reflection?
        if (entity.hasComponent(EntrancePathComponent.class)) {
            return entity.getComponent(EntrancePathComponent.class);
        } else if (entity.hasComponent(CustomPathComponent.class)) {
            return entity.getComponent(CustomPathComponent.class);
        } else {
            throw new Error("Enemy with no Path Component Requested.");
        }
    }

    /**
     * Called when the field is activated.
     * Clears the enemy store and re-scans for any enemies.
     */
    @ReceiveEvent
    public void onFieldActivated(OnFieldActivated event, EntityRef entity) {
        enemies.clear();
        entityManager.getEntitiesWith(GooeyComponent.class).forEach(enemies::add);
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
     * @see OnPathChanged
     */
    @ReceiveEvent
    public void onPathChanged(OnPathChanged event, EntityRef entity) {
        logger.info(event.getPathId() + "");
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

        /* Setup movement component */
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
            enemies.forEach(entity -> processEnemy(entity, delta));
            enemiesToRemove.forEach(enemies::remove);
            enemiesToRemove.clear();
        }
    }

    /**
     * Moves an enemy along it's path.
     *
     * @param entity the enemy to move
     */
    private void processEnemy(EntityRef entity, float delta) {
        PathComponent pathComponent = getPathComponent(entity);
        LocationComponent locationComponent = entity.getComponent(LocationComponent.class);

        float dist = locationComponent.getWorldPosition().distanceSquared(pathComponent.getGoal());
        if (dist < 0.1f) {
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
     * @param delta             The delta of this step
     */
    private void moveEnemyTowardsGoal(EntityRef entity, PathComponent pathComponent, LocationComponent locationComponent, float delta) {
        MovementComponent movementComponent = entity.getComponent(MovementComponent.class);
        /* Calculate required heading */
        Vector3f target = new Vector3f(pathComponent.getGoal());
        target.sub(locationComponent.getWorldPosition());
        target.normalize();
        /* Scale to the speed */
        target.scale(movementComponent.getSpeed() * delta);
        /* Move the enemy */
        locationComponent.setWorldPosition(locationComponent.getWorldPosition().add(target));
        entity.saveComponent(locationComponent);
    }

}
