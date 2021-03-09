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
package org.terasology.gooeyDefence.health.events;

import org.terasology.engine.entitySystem.event.Event;
import org.terasology.gooeyDefence.health.HealthComponent;

/**
 * Sent to deal damage to a given entity.
 * If the entities health reaches zero, then the entity will be called to be destroyed.
 * <p>
 * Sent against the entity taking the damage
 *
 * @see HealthComponent
 * @see EntityDeathEvent
 */
public class DamageEntityEvent implements Event {
    private final int damage;

    public DamageEntityEvent(int damage) {
        this.damage = damage;
    }

    /**
     * @return the damage being dealt by this attack.
     */
    public int getDamage() {
        return damage;
    }
}
