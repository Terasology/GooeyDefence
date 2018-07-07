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
package org.terasology.gooeyDefence.upgrading;

import org.terasology.reflection.MappedContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a sequence of upgrades.
 *
 * @see UpgradeInfo
 * @see BlockUpgradesComponent
 */
@MappedContainer
public class UpgradeList {
    /**
     * The name of this sequence of upgrades
     */
    private String upgradeName = "";
    /**
     * The list of upgrade stages.
     * Order is important, and should be ordered such that 0 is first.
     */
    private List<UpgradeInfo> stages = new ArrayList<>();

    public List<UpgradeInfo> getStages() {
        return stages;
    }

    public String getUpgradeName() {
        return upgradeName;
    }

}
