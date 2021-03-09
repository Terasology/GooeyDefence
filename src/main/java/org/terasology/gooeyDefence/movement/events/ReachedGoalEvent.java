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
package org.terasology.gooeyDefence.movement.events;

import org.terasology.engine.entitySystem.event.ConsumableEvent;
import org.terasology.gooeyDefence.movement.MovementSystem;

/**
 * Event used to notify a system of when an entity has reached it's goal.
 * <p>
 * Sent when an entity has been moved to it's goal.
 * Sent against the entity that reached the goal.
 *
 * @see MovementSystem
 */
public class ReachedGoalEvent implements ConsumableEvent {
    private boolean isConsumed;

    @Override
    public boolean isConsumed() {
        return isConsumed;
    }

    @Override
    public void consume() {
        isConsumed = true;
    }
}
