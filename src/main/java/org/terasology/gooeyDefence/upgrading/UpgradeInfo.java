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

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a single upgrade
 *
 * @see UpgradeList
 * @see BlockUpgradesComponent
 */
@MappedContainer
public class UpgradeInfo {
    /**
     * How much the upgrade will cost
     */
    public int cost;
    /**
     * A mapping between the fields to set, and how much to change them by.
     * Eg, <code>"power": 5</code> will try to increase the field <code>power</code> by 5
     */
    public Map<String, Number> values = new HashMap<>();

}
