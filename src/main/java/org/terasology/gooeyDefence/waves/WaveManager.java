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
package org.terasology.gooeyDefence.waves;

import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.gooeyDefence.EnemyManager;
import org.terasology.registry.In;
import org.terasology.registry.Share;

/**
 *
 */
@RegisterSystem
@Share(WaveManager.class)
public class WaveManager extends BaseComponentSystem implements UpdateSubscriberSystem {
    private boolean isAttackUnderway = false;

    private float[] spawnDelays = {};
    private WaveInfo waveInfo = new WaveInfo();

    @In
    private EnemyManager enemyManager;

    public void startAttack(WaveInfo wave) {
        isAttackUnderway = true;
        waveInfo = new WaveInfo(wave);
        int i = 0;
        spawnDelays = new float[waveInfo.getEntranceInfos().size()];
        for (EntranceInfo info : waveInfo.getEntranceInfos()) {
            if (!info.isFinished()) {
                spawnDelays[i] = info.getDelays().remove(0);
            }
            i++;
        }
    }


    @Override
    public void update(float delta) {
        if (isAttackUnderway) {
            boolean allFinished = true;
            for (int entranceNum = 0; entranceNum < spawnDelays.length; entranceNum++) {
                EntranceInfo info = waveInfo.getEntranceInfos().get(entranceNum);
                if (!info.isFinished()) {
                    allFinished = false;
                    spawnDelays[entranceNum] -= delta;
                    if (spawnDelays[entranceNum] <= 0) {
                        spawnDelays[entranceNum] = info.getDelays().remove(0);
                        enemyManager.spawnEnemy(entranceNum);
                    }
                }
            }
            if (allFinished) {
                stopWave();
            }
        }
    }

    public void stopWave() {
        isAttackUnderway = false;
    }
}
