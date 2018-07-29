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
package org.terasology.gooeyDefence.ui.hud.waveDisplay;

import org.terasology.gooeyDefence.ui.control.UIWaveInfo;
import org.terasology.gooeyDefence.waves.WaveManager;
import org.terasology.registry.In;
import org.terasology.rendering.nui.databinding.ReadOnlyBinding;
import org.terasology.rendering.nui.layers.hud.CoreHudWidget;
import org.terasology.rendering.nui.widgets.UILabel;

/**
 * A HUD element that displays the current wave, as well as the duration remaining.
 */
public class WaveHud extends CoreHudWidget {
    @In
    private WaveManager waveManager;

    private UIWaveInfo waveInfo;
    private UILabel waveDuration;

    @Override
    public void initialise() {
        waveInfo = find("waveInfo", UIWaveInfo.class);
        waveDuration = find("waveDuration", UILabel.class);

        waveDuration.bindText(new ReadOnlyBinding<String>() {
            @Override
            public String get() {
                return String.format("%.1fs", Math.max(0f, waveManager.getRemainingDuration()));

            }
        });
        waveDuration.bindVisible(new ReadOnlyBinding<Boolean>() {
            @Override
            public Boolean get() {
                return waveManager.isAttackUnderway();
            }
        });
    }

    /**
     * Gets the screen to update the displayed wave to whatever the current wave is.
     */
    public void updateCurrentWave() {
        waveInfo.setWaveInfo(waveManager.getCurrentWave());
    }
}
