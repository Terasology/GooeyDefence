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

import org.terasology.gooeyDefence.DefenceField;
import org.terasology.gooeyDefence.waves.EntranceInfo;
import org.terasology.gooeyDefence.waves.WaveInfo;
import org.terasology.gooeyDefence.waves.WaveManager;
import org.terasology.registry.In;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.rendering.nui.widgets.UIButton;

import java.util.Collections;
import java.util.List;

/**
 * Allows the player to control information about the field.
 * This includes for instance, starting a wave.
 */
public class ControlScreen extends CoreScreenLayer {

    @In
    private WaveManager waveManager;

    private UIButton startButton;

    @Override
    public void initialise() {
        startButton = find("startButton", UIButton.class);

        List<EntranceInfo> infos = Collections.nCopies(DefenceField.entranceCount(),
                new EntranceInfo(
                        Collections.nCopies(10, 0.5f),
                        Collections.nCopies(10, "GooeyDefence:BasicEnemy")));

        startButton.subscribe(widget ->
                waveManager.startAttack(new WaveInfo(infos)));
    }

    @Override
    public boolean isModal() {
        return false;
    }
}
