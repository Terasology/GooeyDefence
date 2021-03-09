// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.ui.hud;

import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.nui.layers.hud.CoreHudWidget;
import org.terasology.gooeyDefence.StatSystem;
import org.terasology.gooeyDefence.ui.control.UIWaveInfo;
import org.terasology.gooeyDefence.waves.WaveManager;
import org.terasology.nui.databinding.ReadOnlyBinding;
import org.terasology.nui.widgets.UILabel;

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


    @Override
    public void initialise() {
        waveInfo = find("waveInfo", UIWaveInfo.class);
        UILabel waveDuration = find("waveDuration", UILabel.class);
        UILabel moneyLabel = find("moneyLabel", UILabel.class);


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
