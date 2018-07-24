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

import com.google.common.collect.Lists;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.registry.Share;

import java.util.Collections;
import java.util.List;

/**
 * Produces {@link WaveInfo} instances that can be passed to spawn the waves.
 * Also caches the most recent wave generated, which can be used to ensure that the wave is always the same when passed around.
 *
 * @see WaveManager
 */
@RegisterSystem
@Share(WaveGenerator.class)
public class WaveGenerator extends BaseComponentSystem {
    private WaveInfo currentWave = generateWave();

    /**
     * Generates a new wave.
     *
     * @return The newly created wave.
     */
    public WaveInfo generateWave() {
        List<EntranceInfo> infos = Lists.newArrayList(
                new EntranceInfo(
                        Collections.nCopies(10, 0.5f),
                        Collections.nCopies(10, "GooeyDefence:FastEnemy")),
                new EntranceInfo(
                        Collections.nCopies(10, 0.5f),
                        Collections.nCopies(10, "GooeyDefence:BasicEnemy")),
                new EntranceInfo(
                        Collections.nCopies(10, 0.5f),
                        Collections.nCopies(10, "GooeyDefence:StrongEnemy")));
        currentWave = new WaveInfo(infos);
        return currentWave;
    }

    /**
     * @return the result of the most recently generated wave
     */
    public WaveInfo getCurrentWave() {
        return currentWave;
    }
}
