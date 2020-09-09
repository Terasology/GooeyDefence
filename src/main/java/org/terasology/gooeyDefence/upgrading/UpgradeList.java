// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
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
    public String upgradeName = "";
    /**
     * The list of upgrade stages. Order is important, and should be ordered such that 0 is first.
     */
    public List<UpgradeInfo> stages = new ArrayList<>();

}
