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
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.gooeyDefence.components.GooeyComponent;
import org.terasology.gooeyDefence.events.DamageShrineEvent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.In;
import org.terasology.registry.Share;

import java.util.List;

/**
 * Handles all enemy based actions. Is controlled by {@link DefenceWorldManager}.
 */
@Share(EnemyManager.class)
@RegisterSystem
public class EnemyManager extends BaseComponentSystem implements UpdateSubscriberSystem {
    private static final Logger logger = LoggerFactory.getLogger(EnemyManager.class);

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
        component.goal = defenceWorldManager.getPath(entranceNumber).get(0).toVector3f();
        component.currentStep = 0;
        component.pathId = entranceNumber;
        entity.saveComponent(component);
    }

    @Override
    public void update(float delta) {
        if (defenceWorldManager.isFieldActivated()) {
            for (EntityRef entity : entityManager.getEntitiesWith(GooeyComponent.class)) {
                moveEnemy(entity, delta);
            }
        }
    }

    /**
     * Moves an enemy along it's path.
     *
     * @param entity the enemy to move
     */
    private void moveEnemy(EntityRef entity, float delta) {
        LocationComponent locationComponent = entity.getComponent(LocationComponent.class);
        GooeyComponent gooeyComponent = entity.getComponent(GooeyComponent.class);

        float dist = locationComponent.getWorldPosition().distanceSquared(gooeyComponent.goal);
        if (dist < 0.1f) {
            List<Vector3i> path = defenceWorldManager.getPath(gooeyComponent.pathId);
            if (gooeyComponent.currentStep == path.size() - 1) {
                entity.send(new DamageShrineEvent(gooeyComponent.damage));
                entity.destroy();
            } else {
                gooeyComponent.currentStep = Math.min(gooeyComponent.currentStep + 1, path.size() - 1);
                gooeyComponent.goal = path.get(gooeyComponent.currentStep).toVector3f();
            }
        } else {
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
}
