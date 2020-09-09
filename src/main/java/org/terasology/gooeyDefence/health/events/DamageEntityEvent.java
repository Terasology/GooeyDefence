// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.health.events;

import org.terasology.engine.entitySystem.event.Event;
import org.terasology.gooeyDefence.health.HealthComponent;

/**
 * Sent to deal damage to a given entity. If the entities health reaches zero, then the entity will be called to be
 * destroyed.
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
