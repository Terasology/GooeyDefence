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
import org.terasology.engine.registry.In;
import org.terasology.gooeyDefence.DefenceUris;
import org.terasology.gooeyDefence.movement.components.MovementComponent;
import org.terasology.gooeyDefence.towers.TowerManager;
import org.terasology.gooeyDefence.towers.events.ApplyEffectEvent;
import org.terasology.gooeyDefence.towers.events.RemoveEffectEvent;
import org.terasology.gooeyDefence.visuals.InWorldRenderer;

/**
 * Slows the target enemy by the given amount.
 *
 * @see IceEffectorComponent
 * @see TowerManager
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
        double reducedSpeed = movementComponent.speed * component.slow;
        movementComponent.speed = (float) reducedSpeed;
        inWorldRenderer.addParticleEffect(enemy, DefenceUris.ICE_PARTICLES);
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
        movementComponent.speed = movementComponent.speed / component.slow;
        inWorldRenderer.removeParticleEffect(enemy, DefenceUris.ICE_PARTICLES);
    }
}
