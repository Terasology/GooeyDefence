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

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.gooeyDefence.towers.TowerManager;
import org.terasology.gooeyDefence.towers.events.ApplyEffectEvent;
import org.terasology.gooeyDefence.towers.events.RemoveEffectEvent;
import org.terasology.rendering.logic.SkeletalMeshComponent;

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
        targetMesh.scale.scale(2f);

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
        targetMesh.scale.scale(0.5f);
        event.getTarget().saveComponent(targetMesh);
    }
}
