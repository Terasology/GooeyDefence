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

import com.google.common.collect.Range;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.registry.Share;
import org.terasology.utilities.Assets;
import org.terasology.utilities.random.FastRandom;
import org.terasology.utilities.random.Random;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Produces {@link WaveInfo} instances that can be passed to spawn the waves.
 * Also caches the most recent wave generated, which can be used to ensure that the wave is always the same when passed around.
 *
 * @see WaveManager
 */
@RegisterSystem
@Share(WaveGenerator.class)
public class WaveGenerator extends BaseComponentSystem {
    /**
     * All the valid Wave Info's, according to the most recent calculation
     */
    private List<WaveInfo> validInfos = new ArrayList<>();
    /**
     * All possible wave info's that could be valid.
     * Arranged by lower bound, using -1 if they have none.
     */
    private SortedMap<Integer, Set<WaveInfo>> waveInfos = new TreeMap<>(Integer::compareTo);

    private Random random = new FastRandom();
    private WaveInfo currentWave;

    @Override
    public void postBegin() {
        Prefab config = Assets.getPrefab("GooeyDefence:Waves").get();
        stripFromComponent(config.getComponent(WaveDefinitionComponent.class));

        buildValidInfos(0);
        generateWave();
    }

    /**
     * Updates the system to the selected wave number, then generates a new wave
     *
     * @param waveNum The wave number to update to.
     * @return The newly generated wave.
     */
    public WaveInfo generateWave(int waveNum) {
        buildValidInfos(waveNum);
        return generateWave();
    }

    /**
     * Generates a new wave.
     *
     * @return The newly created wave.
     */
    public WaveInfo generateWave() {
        currentWave = random.nextItem(validInfos);
        return currentWave;
    }

    /**
     * Collates a list of all the valid WaveInfos for the current wave number
     * This is based on the ranges specified in the WaveInfo
     * <p>
     * Note, this is a destructive action.
     * All wave values that are too low to be valid for the wave number will be removed from the master map.
     *
     * @param waveNum The wave to build for
     */
    private void buildValidInfos(int waveNum) {
        validInfos = new ArrayList<>();
        waveInfos.headMap(waveNum + 1)
                .values()
                .forEach(infoSet -> {
                    infoSet.removeIf(
                            waveInfo -> !waveInfo.getWaveRange().contains(waveNum));
                    validInfos.addAll(infoSet);
                });
    }

    /**
     * Collects all the wave ranges from the config component.
     * Handles unbounded options correctly.
     *
     * @param component The component to scrape data from
     */
    private void stripFromComponent(WaveDefinitionComponent component) {
        List<WaveInfo> waves = component.getWaves();
        for (WaveInfo wave : waves) {
            Range<Integer> waveRange = wave.getWaveRange();
            putInfoAt(waveRange.hasLowerBound() ? waveRange.lowerEndpoint() : -1, wave);
        }
    }

    /**
     * Places the wave info at the given point.
     * Handles there being no prior infos at that point correctly.
     *
     * @param pos  The position to place the info into
     * @param info The info to insert
     */
    private void putInfoAt(Integer pos, WaveInfo info) {
        Set<WaveInfo> waves = waveInfos.getOrDefault(pos, new HashSet<>());
        waves.add(info);
        waveInfos.put(pos, waves);
    }

    /**
     * @return the result of the most recently generated wave
     */
    public WaveInfo getCurrentWave() {
        return currentWave;
    }
}
