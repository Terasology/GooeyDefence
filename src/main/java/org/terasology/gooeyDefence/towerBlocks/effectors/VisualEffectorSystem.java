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
import org.terasology.gooeyDefence.events.combat.ApplyEffectEvent;
import org.terasology.gooeyDefence.events.combat.RemoveEffectEvent;
import org.terasology.math.geom.Vector3f;
import org.terasology.rendering.logic.SkeletalMeshComponent;

/**
 *
 */
@RegisterSystem
public class VisualEffectorSystem extends BaseComponentSystem {

    /**
     * Draws a visual cue above the targeted enemy
     * <p>
     * Filters on {@link VisualEffectorComponent}
     *
     * @see ApplyEffectEvent
     */
    @ReceiveEvent
    public void onApplyEffect(ApplyEffectEvent event, EntityRef entity, VisualEffectorComponent component) {
        SkeletalMeshComponent targetMesh = event.getTarget().getComponent(SkeletalMeshComponent.class);
        targetMesh.scale = Vector3f.one().scale(0.4f);

        event.getTarget().saveComponent(targetMesh);
    }

    /**
     * Removes an enemy from being drawn
     * <p>
     * Filters on {@link VisualEffectorComponent}
     *
     * @see RemoveEffectEvent
     */
    @ReceiveEvent
    public void onRemoveEffect(RemoveEffectEvent event, EntityRef entity, VisualEffectorComponent component) {
        SkeletalMeshComponent targetMesh = event.getTarget().getComponent(SkeletalMeshComponent.class);
        targetMesh.scale = Vector3f.one().scale(0.25f);
        event.getTarget().saveComponent(targetMesh);
    }
}
