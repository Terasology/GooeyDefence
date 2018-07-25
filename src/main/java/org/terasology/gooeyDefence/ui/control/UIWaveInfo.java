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
package org.terasology.gooeyDefence.ui.control;

import org.terasology.gooeyDefence.waves.EntranceInfo;
import org.terasology.gooeyDefence.waves.WaveInfo;
import org.terasology.math.geom.Vector2i;
import org.terasology.rendering.assets.font.Font;
import org.terasology.rendering.nui.Canvas;
import org.terasology.rendering.nui.CoreWidget;
import org.terasology.rendering.nui.TextLineBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UIWaveInfo extends CoreWidget {
    private WaveInfo waveInfo = new WaveInfo();
    private Map<String, Integer> prefabs = new HashMap<>();

    public UIWaveInfo() {
        collatePrefabs(waveInfo);
    }

    @Override
    public void onDraw(Canvas canvas) {
        String text = prefabs.keySet()
                .stream()
                .map(prefab -> "\n" + prefab + " x " + prefabs.get(prefab))
                .reduce("", String::concat);

        canvas.drawText(text);
    }

    @Override
    public Vector2i getPreferredContentSize(Canvas canvas, Vector2i sizeHint) {
        Font font = canvas.getCurrentStyle().getFont();
        Vector2i size = Vector2i.zero();
        for (String prefab : prefabs.keySet()) {
            String line = prefab + " x " + prefabs.get(prefab);
            List<String> lines = TextLineBuilder.getLines(font, line, sizeHint.x);
            Vector2i lineSize = font.getSize(lines);
            size.addY(lineSize.y());
            size.setX(Math.max(size.x(), lineSize.x()));
        }
        return size;
    }

    public void setWaveInfo(WaveInfo waveInfo) {
        this.waveInfo = waveInfo;
        collatePrefabs(waveInfo);
    }

    private void collatePrefabs(WaveInfo waveInfo) {
        prefabs.clear();
        for (EntranceInfo entranceInfo : waveInfo) {
            for (String prefab : entranceInfo.getPrefabs()) {
                prefabs.merge(prefab, 1, Integer::sum);
            }
        }
    }
}
