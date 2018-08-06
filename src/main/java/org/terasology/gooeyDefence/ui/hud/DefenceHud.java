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
package org.terasology.gooeyDefence.ui.hud;

import org.terasology.gooeyDefence.StatSystem;
import org.terasology.gooeyDefence.ui.control.UIWaveInfo;
import org.terasology.gooeyDefence.waves.WaveManager;
import org.terasology.registry.In;
import org.terasology.rendering.nui.databinding.ReadOnlyBinding;
import org.terasology.rendering.nui.layers.hud.CoreHudWidget;
import org.terasology.rendering.nui.widgets.UILabel;

/**
 * A HUD layer that displays various different hud elements.
 * This includes the next/current wave & it's duration and the amount of money
 *
 * @see DefenceHudManager
 */
public class DefenceHud extends CoreHudWidget {
    @In
    private WaveManager waveManager;
    @In
    private StatSystem statSystem;

    private UIWaveInfo waveInfo;
    private UILabel waveDuration;
    private UILabel moneyLabel;


    @Override
    public void initialise() {
        waveInfo = find("waveInfo", UIWaveInfo.class);
        waveDuration = find("waveDuration", UILabel.class);
        moneyLabel = find("moneyLabel", UILabel.class);


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
        moneyLabel.bindText(new ReadOnlyBinding<String>() {
            @Override
            public String get() {
                return "Money: " + statSystem.getPlayerMoney();
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
