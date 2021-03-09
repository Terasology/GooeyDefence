/*
 * Copyright 2018 MovingBlocks
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
package org.terasology.gooeyDefence.movement;

import org.joml.Vector3f;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.registry.In;
import org.terasology.gooeyDefence.DefenceField;
import org.terasology.gooeyDefence.movement.components.MovementComponent;
import org.terasology.gooeyDefence.movement.events.ReachedGoalEvent;

/**
 * Handles moving enemies towards a goal, as dictated by their movement component.
 *
 * @see MovementComponent
 * @see ReachedGoalEvent
 */
@RegisterSystem
public class MovementSystem extends BaseComponentSystem implements UpdateSubscriberSystem {
    @In
    private EntityManager entityManager;

    @Override
    public void update(float delta) {
        if (DefenceField.fieldActivated) {
            for (EntityRef entity : entityManager.getEntitiesWith(MovementComponent.class, LocationComponent.class)) {
                moveEntity(entity, delta);
            }
        }
    }

    /**
     * Moves an entity towards the goal as set out in the movement component.
     * Also sends an event when the goal is reached.
     *
     * @param entity The entity to move
     * @param delta  The time the last frame took in seconds.
     */
    private void moveEntity(EntityRef entity, float delta) {
        MovementComponent movementComponent = entity.getComponent(MovementComponent.class);
        LocationComponent locationComponent = entity.getComponent(LocationComponent.class);

        float distSqr = locationComponent.getWorldPosition(new Vector3f()).distanceSquared(movementComponent.goal);
        if (distSqr < movementComponent.reachedDistance) {
            entityReachedGoal(entity);
        } else {
            moveEntityTowardsGoal(entity, delta);
        }
    }

    /**
     * Handles an entity reaching the goal.
     * Sends out an event for other systems to consume and deal with.
     * If the event is not consumed, it simply removes the movement component.
     *
     * @param entity The entity that's reached the goal.
     */
    private void entityReachedGoal(EntityRef entity) {
        ReachedGoalEvent event = new ReachedGoalEvent();
        entity.send(event);
        if (!event.isConsumed()) {
            entity.removeComponent(MovementComponent.class);
        }
    }

    /**
     * Moves an entity towards the goal.
     * Does this by updating the location component on the entity.
     *
     * @param entity The entity to move
     * @param delta  The time the last frame took in seconds.
     */
    private void moveEntityTowardsGoal(EntityRef entity, float delta) {
        MovementComponent movementComponent = entity.getComponent(MovementComponent.class);
        LocationComponent locationComponent = entity.getComponent(LocationComponent.class);

        Vector3f target = new Vector3f(movementComponent.goal)
                /* Calculate required heading */
                .sub(locationComponent.getWorldPosition(new Vector3f()))
                .normalize()
                /* Scale to the speed */
                .mul(movementComponent.speed * delta);
        /* Move the entity */
        locationComponent.setWorldPosition(locationComponent.getWorldPosition(new Vector3f()).add(target));
        entity.saveComponent(locationComponent);
    }

}
