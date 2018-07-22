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
import org.terasology.gooeyDefence.DefenceField;
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

    private int waveSize = 10;
    private float spawnRate = 0.2f;

    private float spawnDelay = 0;
    private int enemiesRemaining = 0;

    @In
    private EnemyManager enemyManager;

    public void startAttack() {
        isAttackUnderway = true;
        spawnDelay = 0;
        enemiesRemaining = waveSize;
    }


    @Override
    public void update(float delta) {
        if (isAttackUnderway) {
            spawnDelay -= delta;
            if (spawnDelay <= 0) {
                spawnDelay = spawnRate;
                enemiesRemaining--;
                for (int i = 0; i < DefenceField.entranceCount(); i++) {
                    enemyManager.spawnEnemy(i);
                }
            }
            if (enemiesRemaining <= 0) {
                stopWave();
            }
        }
    }

    public void stopWave() {
        isAttackUnderway = false;
    }
}
