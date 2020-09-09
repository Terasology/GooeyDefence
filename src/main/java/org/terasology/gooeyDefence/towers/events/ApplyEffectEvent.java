// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.towers.events;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.Event;
import org.terasology.gooeyDefence.towers.components.TowerEffector;
import org.terasology.gooeyDefence.towers.components.TowerTargeter;

/**
 * Event sent to apply an effect to a target Sent against the Effector blocks in the tower.
 *
 * @see TowerEffector
 */
public class ApplyEffectEvent implements Event {
    private final EntityRef target;
    private final float multiplier;

    public ApplyEffectEvent(EntityRef target, float multiplier) {
        this.target = target;
        this.multiplier = multiplier;
    }

    /**
     * @return the enemy being targeted by this event.
     */
    public EntityRef getTarget() {
        return target;
    }

    /**
     * @return The moderating damage multiplier to use for this effect
     * @see TowerTargeter#getMultiplier()
     */
    public float getDamageMultiplier() {
        return multiplier;
    }
}
