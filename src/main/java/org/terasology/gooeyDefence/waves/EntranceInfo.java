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
    public List<Float> delays = new ArrayList<>();
    public List<String> prefabs = new ArrayList<>();

    private int delayCount = 0;
    private float delay = 0f;

    private int prefabCount = 0;
    private String prefab = "";

    public EntranceInfo() {
        buildLists();
    }

    /**
     * Copy an entrance info into a new instance
     *
     * @param copy The entrance info to clone
     */
    public EntranceInfo(EntranceInfo copy) {
        this();
        copy.buildLists();
        delays.addAll(copy.delays);
        prefabs.addAll(copy.prefabs);
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
