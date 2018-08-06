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
package org.terasology.gooeyDefence.health;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.EventPriority;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
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
     * Deals damage to an entity.
     * If the entity's health reaches zero it sends a destruction event to be handled
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
     * Sent when the field is activated.
     * Filters on {@link HealthComponent}
     * Has priority {@link EventPriority#PRIORITY_LOW}
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
     * @see OnFieldReset
     */
    @ReceiveEvent
    public void onFieldReset(OnFieldReset event, EntityRef entity) {
        HealthComponent healthComponent = entity.getParentPrefab().getComponent(HealthComponent.class);
        entity.addOrSaveComponent(healthComponent);
    }
}
