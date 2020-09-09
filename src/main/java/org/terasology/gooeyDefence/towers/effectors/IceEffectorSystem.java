// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
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
