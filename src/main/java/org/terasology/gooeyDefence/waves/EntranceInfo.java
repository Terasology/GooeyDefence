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
import java.util.List;

@MappedContainer
public class EntranceInfo {
    private List<Float> delays = new ArrayList<>();
    private List<String> prefabs = new ArrayList<>();

    public EntranceInfo(List<Float> delayData, List<String> prefabData) {
        delays = delayData;
        prefabs = prefabData;
    }

    public EntranceInfo() {

    }

    public EntranceInfo(EntranceInfo copy) {
        delays.addAll(copy.delays);
        prefabs.addAll(copy.prefabs);
    }

    public boolean isFinished() {
        return delays.isEmpty() && prefabs.isEmpty();
    }

    public Float popDelay() {
        return delays.remove(0);
    }

    public String popPrefab() {
        return prefabs.remove(0);
    }
}
