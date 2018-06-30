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
import org.terasology.entitySystem.systems.RenderSystem;
import org.terasology.gooeyDefence.events.combat.ApplyEffectEvent;
import org.terasology.gooeyDefence.events.combat.RemoveEffectEvent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3i;
import org.terasology.rendering.world.selection.BlockSelectionRenderer;
import org.terasology.utilities.Assets;

import java.util.HashSet;
import java.util.Set;

/**
 *
 */
@RegisterSystem
public class VisualEffectorSystem extends BaseComponentSystem implements RenderSystem {

    private Set<EntityRef> targets = new HashSet<>();
    private BlockSelectionRenderer enemyRenderer;

    /**
     * Draws a visual cue above the targeted enemy
     * <p>
     * Filters on {@link VisualEffectorComponent}
     *
     * @see ApplyEffectEvent
     */
    @ReceiveEvent
    public void onApplyEffect(ApplyEffectEvent event, EntityRef entity, VisualEffectorComponent component) {
        targets.add(event.getTarget());
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
        targets.remove(event.getTarget());
    }

    @Override
    public void initialise() {
        enemyRenderer = new BlockSelectionRenderer(Assets.getTexture("GooeyDefence:ShrineDamaged").get());
    }

    @Override
    public void renderAlphaBlend() {
        enemyRenderer.beginRenderOverlay();
        for (EntityRef enemy : targets) {
            LocationComponent locationComponent = enemy.getComponent(LocationComponent.class);
            if (locationComponent != null) {
                Vector3i pos = new Vector3i(locationComponent.getWorldPosition());
                enemyRenderer.renderMark2(Vector3i.up().add(pos));
            }
        }
        enemyRenderer.endRenderOverlay();
    }

    @Override
    public void renderOpaque() {
    }

    @Override
    public void renderOverlay() {
    }

    @Override
    public void renderShadows() {

    }
}
