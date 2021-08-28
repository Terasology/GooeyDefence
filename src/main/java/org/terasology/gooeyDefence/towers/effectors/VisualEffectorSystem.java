// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.towers.effectors;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.rendering.logic.SkeletalMeshComponent;
import org.terasology.gooeyDefence.towers.TowerManager;
import org.terasology.gooeyDefence.towers.events.ApplyEffectEvent;
import org.terasology.gooeyDefence.towers.events.RemoveEffectEvent;

/**
 * Enlarges an enemy
 * Used to help identify the enemy.
 *
 * @see VisualEffectorComponent
 * @see TowerManager
 */
@RegisterSystem
public class VisualEffectorSystem extends BaseComponentSystem {

    /**
     * Applies the increased scale to the enemy.
     * <p>
     * Filters on {@link VisualEffectorComponent}
     *
     * @see ApplyEffectEvent
     */
    @ReceiveEvent
    public void onApplyEffect(ApplyEffectEvent event, EntityRef entity, VisualEffectorComponent component) {
        SkeletalMeshComponent targetMesh = event.getTarget().getComponent(SkeletalMeshComponent.class);
        targetMesh.scale.mul(2f);

        event.getTarget().saveComponent(targetMesh);
    }

    /**
     * Reverts the size change of the enemy back to original.
     * <p>
     * Filters on {@link VisualEffectorComponent}
     *
     * @see RemoveEffectEvent
     */
    @ReceiveEvent
    public void onRemoveEffect(RemoveEffectEvent event, EntityRef entity, VisualEffectorComponent component) {
        SkeletalMeshComponent targetMesh = event.getTarget().getComponent(SkeletalMeshComponent.class);
        targetMesh.scale.mul(0.5f);
        event.getTarget().saveComponent(targetMesh);
    }
}
