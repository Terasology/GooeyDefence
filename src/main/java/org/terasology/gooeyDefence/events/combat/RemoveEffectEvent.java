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
package org.terasology.gooeyDefence.events.combat;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.Event;
import org.terasology.gooeyDefence.towerBlocks.base.TowerEffector;

/**
 * Event sent to remove an effect from the target
 * Sent against the Effector that applied the effect.
 *
 * @see TowerEffector
 */
public class RemoveEffectEvent implements Event {
    private EntityRef target;
    private float multiplier;

    public RemoveEffectEvent(EntityRef target, float multiplier) {
        this.target = target;
        this.multiplier = multiplier;
    }

    public EntityRef getTarget() {
        return target;
    }

    public float getMultiplier() {
        return multiplier;
    }
}
