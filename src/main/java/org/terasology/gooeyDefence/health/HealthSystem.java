// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.health;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.EventPriority;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.gooeyDefence.events.OnFieldActivated;
import org.terasology.gooeyDefence.events.OnFieldReset;
import org.terasology.gooeyDefence.health.events.DamageEntityEvent;
import org.terasology.gooeyDefence.health.events.EntityDeathEvent;

/**
 * Handles operations involving health on entities.
 *
 * @see HealthComponent
 */
@RegisterSystem
public class HealthSystem extends BaseComponentSystem {

    /**
     * Deals damage to an entity. If the entity's health reaches zero it sends a destruction event to be handled
     * <p>
     * Filters on {@link HealthComponent}
     *
     * @see DamageEntityEvent
     */
    @ReceiveEvent
    public void onDamageEntity(DamageEntityEvent event, EntityRef entity, HealthComponent component) {
        component.health = Math.max(component.health - event.getDamage(), 0);
        if (component.health == 0) {
            entity.send(new EntityDeathEvent());
        }
    }


    /**
     * Handles the case where a game is loaded from a save where the shrine has been killed.
     * <p>
     * Sent when the field is activated. Filters on {@link HealthComponent} Has priority {@link
     * EventPriority#PRIORITY_LOW}
     *
     * @see OnFieldActivated
     */
    @ReceiveEvent(priority = EventPriority.PRIORITY_LOW)
    public void onFieldActivated(OnFieldActivated event, EntityRef entity, HealthComponent component) {
        if (component.health <= 0) {
            entity.send(new EntityDeathEvent());
        }
    }

    /**
     * Resets the shrines health to the default specified in it's prefab.
     * <p>
     * Called when a field is reset.
     *
     * @see OnFieldReset
     */
    @ReceiveEvent
    public void onFieldReset(OnFieldReset event, EntityRef entity) {
        HealthComponent healthComponent = entity.getParentPrefab().getComponent(HealthComponent.class);
        entity.addOrSaveComponent(healthComponent);
    }
}
