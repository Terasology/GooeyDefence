/*
 * Copyright 2017 MovingBlocks
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
import org.terasology.entitySystem.systems.RenderSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.gooeyDefence.events.combat.ApplyEffectEvent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;
import org.terasology.rendering.world.selection.BlockSelectionRenderer;
import org.terasology.utilities.Assets;

@RegisterSystem
public class FireEffectorSystem extends BaseComponentSystem implements RenderSystem, UpdateSubscriberSystem {

    private EntityRef targetEntity;

    private BlockSelectionRenderer flagRenderer;

    @Override
    public void initialise() {
        flagRenderer = new BlockSelectionRenderer(Assets.getTexture("GooeyDefence:ShrineDamaged").get());
    }

    @ReceiveEvent
    public void onApplyEffect(ApplyEffectEvent event, EntityRef entity, FireEffectorComponent effectorComponent) {
        targetEntity = event.getTarget();
    }

    @Override
    public void renderOpaque() {

    }

    @Override
    public void renderAlphaBlend() {
        flagRenderer.beginRenderOverlay();
        if (targetEntity != null) {
            Vector3f pos = targetEntity.getComponent(LocationComponent.class).getWorldPosition();
            flagRenderer.renderMark2(new Vector3i(pos).addY(1));
        }
        flagRenderer.endRenderOverlay();
    }

    @Override
    public void renderOverlay() {

    }

    @Override
    public void renderShadows() {

    }

    @Override
    public void update(float delta) {
        if (targetEntity != null && !targetEntity.exists()) {
            targetEntity = null;
        }
    }
}
