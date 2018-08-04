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
import com.google.common.collect.Streams;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.gooeyDefence.DefenceField;
import org.terasology.gooeyDefence.EnemyManager;
import org.terasology.gooeyDefence.StatSystem;
import org.terasology.registry.In;
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
 * Handles spawning in each wave.
 * Information for each wave is stored in a special ADT.
 *
 * @see WaveInfo
 */
@RegisterSystem
@Share(WaveManager.class)
public class WaveManager extends BaseComponentSystem implements UpdateSubscriberSystem {
    /**
     * Flag used to indicate that an attack is underway
     */
    private boolean isAttackUnderway = false;

    /**
     * A list of the time until another enemy will be spawned at each entrance
     */
    private float[] spawnDelays = {};

    /**
     * All the valid Wave Info's, according to the most recent calculation
     */
    private List<WaveInfo> validInfos = new ArrayList<>();

    /**
     * All possible wave info's that could be valid.
     * Arranged by lower bound, using -1 if they have none.
     */
    private SortedMap<Integer, Set<WaveInfo>> waveInfos = new TreeMap<>(Integer::compareTo);

    /**
     * The current wave that is being spawned, or is about to be spawned.
     */
    private WaveInfo currentWave;

    private float remainingDuration = 0f;

    @In
    private EnemyManager enemyManager;
    @In
    private StatSystem statSystem;
    private Random random = new FastRandom();

    @Override
    public void preBegin() {
        Prefab config = Assets.getPrefab("GooeyDefence:Waves").get();
        stripFromComponent(config.getComponent(WaveDefinitionComponent.class));

        generateWave(statSystem.getWaveNumber());
    }

    @Override
    public void update(float delta) {
        if (isAttackUnderway) {
            boolean allFinished = true;
            int entranceNum = 0;
            for (EntranceInfo info : currentWave.entranceInfos) {
                allFinished &= !spawnAtEntrance(info, entranceNum, delta);
                entranceNum++;
            }
            if (allFinished) {
                stopWave();
            }
            remainingDuration -= delta;
        }
    }

    /**
     * Begin spawning in the current wave.
     * Once the wave ends, a new wave will be generated.
     */
    public void startAttack() {
        if (!isAttackUnderway) {
            isAttackUnderway = true;

            remainingDuration = Streams.stream(currentWave.entranceInfos)
                    .map(entranceInfo -> entranceInfo.delays
                            .stream()
                            .reduce(0f, Float::sum))
                    .max(Float::compareTo)
                    .orElse(0f);

            int i = 0;
            spawnDelays = new float[currentWave.entranceInfos.size()];
            for (EntranceInfo info : currentWave.entranceInfos) {
                if (!info.delays.isEmpty() || !info.prefabs.isEmpty()) {
                    spawnDelays[i] = info.delays.remove(0);
                }
                i++;
            }


        }
    }

    /**
     * @return How much longer the wave is expected to run for.
     */
    public float getRemainingDuration() {
        return remainingDuration;
    }

    /**
     * @return True if an attack is currently happening. False otherwise
     */
    public boolean isAttackUnderway() {
        return isAttackUnderway;
    }

    /**
     * Stops a wave in progress and generates a new wave.
     * Sends out an event when the wave has fully ended
     *
     * @see OnWaveEnd
     */
    private void stopWave() {
        isAttackUnderway = false;
        statSystem.incrementWave();
        generateWave(statSystem.getWaveNumber());
        DefenceField.getShrineEntity().send(new OnWaveEnd());
    }

    /**
     * Updates the system to the selected wave number, then generates a new wave
     *
     * @param waveNum The wave number to update to.
     */
    private void generateWave(int waveNum) {
        buildValidInfos(waveNum);
        generateWave();
    }

    /**
     * Generates a new wave.
     */
    private void generateWave() {
        currentWave = new WaveInfo(random.nextItem(validInfos));
    }

    /**
     * @return The wave currently being spawned, or about to be spawned.
     */
    public WaveInfo getCurrentWave() {
        return currentWave;
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
        if (!spawnInfo.delays.isEmpty() || !spawnInfo.prefabs.isEmpty()) {
            spawnDelays[entranceNum] -= delta;
            if (spawnDelays[entranceNum] <= 0) {
                enemyManager.spawnEnemy(entranceNum, spawnInfo.prefabs.remove(0));
                if (!spawnInfo.delays.isEmpty() || !spawnInfo.prefabs.isEmpty()) {
                    spawnDelays[entranceNum] = spawnInfo.delays.remove(0);
                }
            }
            return true;
        } else {
            return false;
        }
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
                            waveInfo -> !getWaveRange(waveInfo).contains(waveNum));
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
        List<WaveInfo> waves = component.waves;
        for (WaveInfo wave : waves) {
            Range<Integer> waveRange = getWaveRange(wave);
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

    private Range<Integer> getWaveRange(WaveInfo info) {
        if (info.waveRange == null) {
            convertToRange(info);
        }
        return info.waveRange;
    }

    private void convertToRange(WaveInfo info) {
        if (info.lowerBound >= 0) {
            if (info.upperBound >= 0) {
                /* Lower and upper */
                info.waveRange = Range.closed(info.lowerBound, info.upperBound);
            } else {
                /* Just lower */
                info.waveRange = Range.atLeast(info.lowerBound);
            }
        } else {
            if (info.upperBound >= 0) {
                /* Just upper */
                info.waveRange = Range.atMost(info.upperBound);
            } else {
                /* Neither */
                info.waveRange = Range.all();
            }
        }
    }


}
