// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.movement.events;

import org.terasology.engine.entitySystem.event.ConsumableEvent;
import org.terasology.gooeyDefence.movement.MovementSystem;

/**
 * Event used to notify a system of when an entity has reached it's goal.
 * <p>
 * Sent when an entity has been moved to it's goal. Sent against the entity that reached the goal.
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
