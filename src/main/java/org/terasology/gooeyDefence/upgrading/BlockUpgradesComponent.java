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

import org.terasology.entitySystem.Component;

import java.util.List;

/**
 * Contains all the information related to the upgrades possible for this block
 *
 * @see UpgradeList
 * @see UpgradingSystem
 */
public class BlockUpgradesComponent implements Component {
    /**
     * The component the upgrades will apply to.
     * Should be the name without the <code>Component</code> ending.
     * Eg, <code>"SingleTargeterComponent"</code> should be <code>"SingleTargeter"</code>
     */
    public String componentName;
    /**
     * All of the upgrade paths applicable
     */
    public List<UpgradeList> upgrades;

}
