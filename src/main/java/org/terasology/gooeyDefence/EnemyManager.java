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
import org.terasology.gooeyDefence.components.GooeyComponent;
import org.terasology.gooeyDefence.events.DamageShrineEvent;
import org.terasology.gooeyDefence.events.OnFieldActivated;
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
    private DefenceWorldManager defenceWorldManager;

    /**
     * Spawns an enemy at the given entrance.
     * Also begins it travelling down the path.
     *
     * @param entranceNumber The entrance to spawn at
     */
    public void spawnEnemy(int entranceNumber) {
        EntityRef entity = entityManager.create("GooeyDefence:Gooey", DefenceField.entrancePos(entranceNumber).toVector3f());

        /* Setup movement component */
        GooeyComponent component = entity.getComponent(GooeyComponent.class);
        List<Vector3i> path = defenceWorldManager.getPath(entranceNumber);
        component.currentStep = path.size() - 1;
        component.goal = path.get(component.currentStep).toVector3f();
        component.pathId = entranceNumber;
        entity.saveComponent(component);

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
        LocationComponent locationComponent = entity.getComponent(LocationComponent.class);
        GooeyComponent gooeyComponent = entity.getComponent(GooeyComponent.class);

        float dist = locationComponent.getWorldPosition().distanceSquared(gooeyComponent.goal);
        if (dist < 0.1f) {
            updateToNextStep(entity, gooeyComponent);
        } else {
            moveEnemyTowardsGoal(entity, delta, gooeyComponent, locationComponent);
        }
    }

    /**
     * Handles the entity reaching the goal.
     * Either marks the entity as having reached the end of the path or picks the next element in the path as the goal.
     *
     * @param entity         The entity to update
     * @param gooeyComponent The GooeyComponent of the entity
     */
    private void updateToNextStep(EntityRef entity, GooeyComponent gooeyComponent) {
        List<Vector3i> path = defenceWorldManager.getPath(gooeyComponent.pathId);
        if (gooeyComponent.currentStep <= 0) {
            entity.send(new DamageShrineEvent(gooeyComponent.damage));
            destroyEnemy(entity);
        } else {
            gooeyComponent.currentStep -= 1;
            gooeyComponent.goal = path.get(gooeyComponent.currentStep).toVector3f();
            entity.saveComponent(gooeyComponent);
        }
    }

    /**
     * Moves the entity towards it's goal.
     *
     * @param entity            The entity to move
     * @param delta             The delta of this step
     * @param gooeyComponent    The GooeyComponent of the entity
     * @param locationComponent The LocationComponent of the entity
     */
    private void moveEnemyTowardsGoal(EntityRef entity, float delta, GooeyComponent gooeyComponent, LocationComponent locationComponent) {
        /* Calculate required heading */
        Vector3f target = new Vector3f(gooeyComponent.goal);
        target.sub(locationComponent.getWorldPosition());
        target.normalize();
        /* Scale to the speed */
        target.scale(gooeyComponent.speed * delta);
        /* Move the enemy */
        locationComponent.setWorldPosition(locationComponent.getWorldPosition().add(target));
        entity.saveComponent(locationComponent);
    }

}
