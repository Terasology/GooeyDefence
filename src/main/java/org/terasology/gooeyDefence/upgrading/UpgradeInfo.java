// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.upgrading;

import org.terasology.nui.reflection.MappedContainer;

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
     * A mapping between the fields to set, and how much to change them by. Eg, <code>"power": 5</code> will try to
     * increase the field <code>power</code> by 5
     */
    public Map<String, Number> values = new HashMap<>();

}
