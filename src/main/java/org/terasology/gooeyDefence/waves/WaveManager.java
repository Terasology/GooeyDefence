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
import org.terasology.gooeyDefence.StatSystem;
import org.terasology.registry.In;
import org.terasology.registry.Share;

/**
 * Handles spawning in each wave.
 * Information for each wave is stored in a special ADT.
 *
 * @see WaveInfo
 */
@RegisterSystem
@Share(WaveManager.class)
public class WaveManager extends BaseComponentSystem implements UpdateSubscriberSystem {
    private boolean isAttackUnderway = false;

    private float[] spawnDelays = {};
    private WaveInfo waveInfo = new WaveInfo();

    @In
    private EnemyManager enemyManager;
    @In
    private StatSystem statSystem;

    /**
     * Spawn in the wave according to the data
     *
     * @param wave The wave to spawn in.
     */
    public void startAttack(WaveInfo wave) {
        isAttackUnderway = true;
        waveInfo = new WaveInfo(wave);
        statSystem.incrementWave();

        int i = 0;
        spawnDelays = new float[waveInfo.getSize()];
        for (EntranceInfo info : waveInfo) {
            if (info.hasItems()) {
                spawnDelays[i] = info.popDelay();
            }
            i++;
        }
    }


    @Override
    public void update(float delta) {
        if (isAttackUnderway) {
            boolean allFinished = true;
            int entranceNum = 0;
            for (EntranceInfo info : waveInfo) {
                allFinished &= !spawnAtEntrance(info, entranceNum, delta);
                entranceNum++;
            }
            if (allFinished) {
                stopWave();
            }
        }
    }

    /**
     * Spawns in the enemy for an entrance.
     * Handles the entrance having no more enemies to spawn.
     *
     * @param spawnInfo   The information for that entrance
     * @param entranceNum The id of the entrance to spawn at
     * @param delta       The time the last frame took to execute
     * @return True if an enemy was spawned, false otherwise
     */
    private boolean spawnAtEntrance(EntranceInfo spawnInfo, int entranceNum, float delta) {
        if (spawnInfo.hasItems()) {
            spawnDelays[entranceNum] -= delta;
            if (spawnDelays[entranceNum] <= 0) {
                enemyManager.spawnEnemy(entranceNum, spawnInfo.popPrefab());
                if (spawnInfo.hasItems()) {
                    spawnDelays[entranceNum] = spawnInfo.popDelay();
                }
            }
            return true;
        } else {
            return false;
        }
    }


    /**
     * Stops a wave in progress.
     */
    public void stopWave() {
        isAttackUnderway = false;
    }
}
