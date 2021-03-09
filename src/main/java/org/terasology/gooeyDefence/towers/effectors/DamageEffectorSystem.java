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
package org.terasology.gooeyDefence.towers.effectors;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.gooeyDefence.health.events.DamageEntityEvent;
import org.terasology.gooeyDefence.towers.TowerManager;
import org.terasology.gooeyDefence.towers.events.ApplyEffectEvent;

/**
 * Deals plain damage to the target
 *
 * @see DamageEffectorComponent
 * @see TowerManager
 */
@RegisterSystem
public class DamageEffectorSystem extends BaseComponentSystem {

    /**
     * Called to apply the effect to the target of the event.
     * <p>
     * Filters on DamageEffectorComponent
     *
     * @see ApplyEffectEvent
     */
    @ReceiveEvent
    public void onApplyEffect(ApplyEffectEvent event, EntityRef entity, DamageEffectorComponent component) {
        event.getTarget().send(new DamageEntityEvent(component.damage));
    }
}
