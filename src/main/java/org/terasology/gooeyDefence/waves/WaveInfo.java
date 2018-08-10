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
import org.terasology.reflection.MappedContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains information on how to spawn the enemies for this wave.
 *
 * @see EntranceInfo
 */
@MappedContainer
public class WaveInfo {
    /**
     * The data for spawns at each entrance.
     * No spawns should be indicated by the blank instance, not missing entries
     */
    public List<EntranceInfo> entranceInfos = new ArrayList<>();
    /**
     * The first wave this info should be valid at.
     * Used only in configuration in prefabs
     */
    public int lowerBound = -1;
    /**
     * The last wave for which this info will be valid
     * Used only in configuration in prefabs
     */
    public int upperBound = -1;

    /**
     * The wave range this info should be used in.
     */
    public Range<Integer> waveRange;

    /**
     * Plain constructor for serialisation
     */
    public WaveInfo() {

    }

    /**
     * Clones a given wave info.
     *
     * @param copy The wave info to clone.
     */
    public WaveInfo(WaveInfo copy) {
        for (EntranceInfo info : copy.entranceInfos) {
            entranceInfos.add(new EntranceInfo(info));
        }
        waveRange = copy.waveRange;
        lowerBound = copy.lowerBound;
        upperBound = copy.upperBound;
    }


}
