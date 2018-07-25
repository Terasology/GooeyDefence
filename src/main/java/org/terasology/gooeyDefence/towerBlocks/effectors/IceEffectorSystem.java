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
package org.terasology.gooeyDefence.towerBlocks.effectors;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.gooeyDefence.InWorldRenderer;
import org.terasology.gooeyDefence.movement.components.MovementComponent;
import org.terasology.gooeyDefence.events.combat.ApplyEffectEvent;
import org.terasology.gooeyDefence.events.combat.RemoveEffectEvent;
import org.terasology.registry.In;

/**
 * Slows the target enemy by the given amount.
 *
 * @see IceEffectorComponent
 */
@RegisterSystem
public class IceEffectorSystem extends BaseComponentSystem {


    @In
    private InWorldRenderer inWorldRenderer;
    /**
     * Applies the slow effect to the target
     * <p>
     * Filters on {@link IceEffectorComponent}
     *
     * @see ApplyEffectEvent
     */
    @ReceiveEvent
    public void onApplyEffect(ApplyEffectEvent event, EntityRef entity, IceEffectorComponent component) {
        EntityRef enemy = event.getTarget();
        MovementComponent movementComponent = enemy.getComponent(MovementComponent.class);
        double reducedSpeed = movementComponent.getSpeed() * component.getSlow();
        movementComponent.setSpeed((float) reducedSpeed);
        inWorldRenderer.addParticleEffect(enemy, "GooeyDefence:IceParticleEffect");
    }

    /**
     * Removes the slow effect from the target
     * <p>
     * Filters on {@link IceEffectorComponent}
     *
     * @see RemoveEffectEvent
     */
    @ReceiveEvent
    public void onRemoveEffect(RemoveEffectEvent event, EntityRef entity, IceEffectorComponent component) {
        EntityRef enemy = event.getTarget();
        MovementComponent movementComponent = enemy.getComponent(MovementComponent.class);
        movementComponent.setSpeed(movementComponent.getSpeed() / component.getSlow());
        inWorldRenderer.removeParticleEffect(enemy, "GooeyDefence:IceParticleEffect");
    }
}
