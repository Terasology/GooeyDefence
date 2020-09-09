// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.ui.control;

import org.joml.Vector2i;
import org.terasology.gooeyDefence.ui.hud.DefenceHud;
import org.terasology.gooeyDefence.waves.EntranceInfo;
import org.terasology.gooeyDefence.waves.WaveInfo;
import org.terasology.nui.Canvas;
import org.terasology.nui.CoreWidget;
import org.terasology.nui.TextLineBuilder;
import org.terasology.nui.asset.font.Font;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Displays a wave.
 * <p>
 * Only shows the enemies that will be spawned, and how many. Does not show which entrance nor spawning delays/order
 *
 * @see WaveInfo
 * @see ControlScreen
 * @see DefenceHud
 */
public class UIWaveInfo extends CoreWidget {
    private final Map<String, Integer> prefabs = new HashMap<>();
    private WaveInfo waveInfo = new WaveInfo();

    public UIWaveInfo() {
        collatePrefabs();
    }

    @Override
    public void onDraw(Canvas canvas) {
        String text = prefabs.keySet()
                .stream()
                .map(prefab -> "\n" + prefab + " x " + prefabs.get(prefab))
                .reduce("", String::concat)
                .trim();

        canvas.drawText(text);
    }

    @Override
    public Vector2i getPreferredContentSize(Canvas canvas, Vector2i sizeHint) {
        Font font = canvas.getCurrentStyle().getFont();
        Vector2i size = new Vector2i();
        for (String prefab : prefabs.keySet()) {
            String line = prefab + " x " + prefabs.get(prefab);
            List<String> lines = TextLineBuilder.getLines(font, line, sizeHint.x);
            Vector2i lineSize = font.getSize(lines);
            size.x += Math.max(size.x(), lineSize.x());
            size.y += lineSize.y();
        }
        return size;
    }

    /**
     * Updates the wave info in the widget.
     *
     * @param waveInfo THe new wave info to set.
     */
    public void setWaveInfo(WaveInfo waveInfo) {
        this.waveInfo = waveInfo;
        collatePrefabs();
    }

    /**
     * Counts up the number of each type of prefab in the wave info. Stores this as a map between the prefab name
     * (string) and the count (int).
     */
    private void collatePrefabs() {
        prefabs.clear();
        for (EntranceInfo entranceInfo : waveInfo.entranceInfos) {
            for (String prefab : entranceInfo.prefabs) {
                prefabs.merge(prefab, 1, Integer::sum);
            }
        }
    }
}
