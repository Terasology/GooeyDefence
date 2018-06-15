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
package org.terasology.gooeyDefence;

import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.RenderSystem;
import org.terasology.gooeyDefence.events.DamageShrineEvent;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.In;
import org.terasology.rendering.world.selection.BlockSelectionRenderer;
import org.terasology.utilities.Assets;

import java.util.List;

@RegisterSystem
public class InWorldRenderer extends BaseComponentSystem implements RenderSystem {

    private BlockSelectionRenderer pathBlockRenderer;
    private BlockSelectionRenderer shrineDamageRenderer;


    @In
    private Time time;
    @In
    private PathfindingSystem pathfindingSystem;

    private int shrineDamaged = 0;

    @Override
    public void initialise() {
        pathBlockRenderer = new BlockSelectionRenderer(Assets.getTexture("GooeyDefence:PathBlock").get());
        shrineDamageRenderer = new BlockSelectionRenderer(Assets.getTexture("GooeyDefence:ShrineDamaged").get());
    }

    @ReceiveEvent
    public void onDamageShrine(DamageShrineEvent event, EntityRef entity) {
        shrineDamaged = 100;
    }

    @Override
    public void renderAlphaBlend() {
        pathBlockRenderer.beginRenderOverlay();
        List<List<Vector3i>> paths = pathfindingSystem.getPaths();
        for (List<Vector3i> path : paths) {
            if (path != null) {
                for (Vector3i pos : path) {
                    pathBlockRenderer.renderMark2(Vector3i.up().add(pos));
                }
            }
        }
        pathBlockRenderer.endRenderOverlay();
        shrineDamageRenderer.beginRenderOverlay();
        if (shrineDamaged > 0) {
            shrineDamaged -= time.getGameDeltaInMs();
            for (Vector3i pos : DefenceField.getShrine()) {
                shrineDamageRenderer.renderMark2(pos);
            }
        }
        shrineDamageRenderer.endRenderOverlay();
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
