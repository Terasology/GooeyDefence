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

import org.terasology.gooeyDefence.waves.WaveManager;
import org.terasology.registry.In;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.rendering.nui.UIWidget;
import org.terasology.rendering.nui.WidgetUtil;
import org.terasology.rendering.nui.databinding.ReadOnlyBinding;
import org.terasology.rendering.nui.widgets.UILabel;

/**
 * Allows the player to control information about the field.
 * This includes for instance, starting a wave.
 *
 * @see ControlScreenSystem
 */
public class ControlScreen extends CoreScreenLayer {

    @In
    private WaveManager waveManager;

    private UIWaveInfo waveInfo;

    @Override
    public void initialise() {
        waveInfo = find("waveInfo", UIWaveInfo.class);
        UILabel waveDuration = find("waveDuration", UILabel.class);

        WidgetUtil.trySubscribe(this, "startButton", this::startButtonPressed);
        waveInfo.setWaveInfo(waveManager.getCurrentWave());
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

    @Override
    public void onOpened() {
        reloadWaveInfo();
    }

    /**
     * Used to ensure that `E` will still close the screen.
     * Does allow for movement when screen is open.
     * TODO: Replace this method of closing with another more robust one.
     *
     * @return True if keypresses are allowed
     */
    @Override
    public boolean isModal() {
        return false;
    }

    /**
     * Method handler for when the start button is pressed.
     */
    @SuppressWarnings("unused")
    private void startButtonPressed(UIWidget ignored) {
        waveManager.startAttack();
    }

    /**
     * Reloads the WaveInfo from the WaveManager.
     */
    public void reloadWaveInfo() {
        this.waveInfo.setWaveInfo(waveManager.getCurrentWave());
    }
}
