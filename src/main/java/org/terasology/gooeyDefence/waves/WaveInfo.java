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
import java.util.Iterator;
import java.util.List;

/**
 * Contains information on how to spawn the enemies for this wave.
 *
 * @see EntranceInfo
 */
@MappedContainer
public class WaveInfo implements Iterable<EntranceInfo> {
    /**
     * The data for spawns at each entrance.
     * No spawns should be indicated by the blank instance, not missing entries
     */
    private List<EntranceInfo> entranceInfos = new ArrayList<>();
    /**
     * The first wave this info should be valid at.
     * Used only in configuration in prefabs
     */
    private int lowerBound = -1;
    /**
     * The last wave for which this info will be valid
     * Used only in configuration in prefabs
     */
    private int upperBound = -1;

    /**
     * The wave range this info should be used in.
     */
    private Range<Integer> waveRange;

    private boolean shouldBuildRange = false;

    public WaveInfo() {
    }

    /**
     * Creates a new wave info from the given entrance data
     *
     * @param entranceData The entrance data to use.
     */
    public WaveInfo(List<EntranceInfo> entranceData) {
        this(null, entranceData);
    }

    /**
     * Creates a new wave info from the given entrance data and the wave range where this should be used.
     *
     * @param range        The range of the wave to use.
     * @param entranceData The entrance data to use.
     */
    public WaveInfo(Range<Integer> range, List<EntranceInfo> entranceData) {
        waveRange = range;
        entranceInfos = entranceData;
    }

    /**
     * Clones a given wave info.
     *
     * @param copy The wave info to clone.
     */
    public WaveInfo(WaveInfo copy) {
        for (EntranceInfo info : copy) {
            entranceInfos.add(new EntranceInfo(info));
        }
        waveRange = copy.waveRange;
        lowerBound = copy.lowerBound;
        upperBound = copy.upperBound;
    }

    /**
     * @return How many entrances this WaveInfo contains data for
     */
    public int getSize() {
        return entranceInfos.size();
    }

    /**
     * Gets the range this info applies over.
     * Generates this based on the `upperBound` and `lowerBound` info, caching the value after the first call.
     *
     * @return The range the wave info should be used in.
     * @see Range
     */
    public Range<Integer> getWaveRange() {
        if (waveRange == null) {
            convertToRange();
        }
        return waveRange;
    }

    /**
     * Converts the `upperBound` and `lowerBound` fields into a {@link Range}
     */
    private void convertToRange() {
        if (lowerBound >= 0) {
            if (upperBound >= 0) {
                /* Lower and upper */
                waveRange = Range.closed(lowerBound, upperBound);
            } else {
                /* Just lower */
                waveRange = Range.atLeast(lowerBound);
            }
        } else {
            if (upperBound >= 0) {
                /* Just upper */
                waveRange = Range.atMost(upperBound);
            } else {
                /* Neither */
                waveRange = Range.all();
            }
        }
    }

    /**
     * @return Iterator for easy iterating over the data
     */
    @Override
    public Iterator<EntranceInfo> iterator() {
        return entranceInfos.iterator();
    }
}
