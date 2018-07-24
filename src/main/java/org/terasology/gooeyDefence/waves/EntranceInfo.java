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

import org.terasology.reflection.MappedContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Contains information for spawning enemies at an entrance.
 *
 * @see WaveInfo
 */
@MappedContainer
public class EntranceInfo {
    private List<Float> delays = new ArrayList<>();
    private List<String> prefabs = new ArrayList<>();

    private int delayCount = 0;
    private float delay = 0f;

    private int prefabCount = 0;
    private String prefab = "";

    public EntranceInfo() {
        buildLists();
    }

    /**
     * Create a new entrance info from the given data
     *
     * @param delayData  The delay information to use
     * @param prefabData The prefab info to use
     */
    public EntranceInfo(List<Float> delayData, List<String> prefabData) {
        this();
        delays = delayData;
        prefabs = prefabData;
    }


    /**
     * Copy an entrance info into a new instance
     *
     * @param copy The entrance info to clone
     */
    public EntranceInfo(EntranceInfo copy) {
        this();
        delays.addAll(copy.delays);
        prefabs.addAll(copy.prefabs);
    }

    /**
     * @return True if the entrance info still has more enemies to spawn.
     */
    public boolean hasItems() {
        return !delays.isEmpty() || !prefabs.isEmpty();
    }

    /**
     * Get the delay for spawning the next enemy.
     * This action will remove the delay from the list.
     *
     * @return The next delay
     */
    public Float popDelay() {
        return delays.remove(0);
    }

    /**
     * Get the prefab to use to spawn the next enemy
     * This action will remove the prefab from the list.
     *
     * @return The prefab to spawn
     */
    public String popPrefab() {
        return prefabs.remove(0);
    }

    private void buildLists() {
        if (prefabCount > 0 && prefabs.isEmpty()) {
            prefabs = Collections.nCopies(prefabCount, prefab);
        }
        if (delayCount > 0 && delays.isEmpty()) {
            delays = Collections.nCopies(delayCount, delay);
        }
    }
}
