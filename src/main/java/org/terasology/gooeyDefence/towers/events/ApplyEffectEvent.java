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
package org.terasology.gooeyDefence.towers.events;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.event.Event;
import org.terasology.gooeyDefence.towers.components.TowerEffector;
import org.terasology.gooeyDefence.towers.components.TowerTargeter;

/**
 * Event sent to apply an effect to a target
 * Sent against the Effector blocks in the tower.
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
