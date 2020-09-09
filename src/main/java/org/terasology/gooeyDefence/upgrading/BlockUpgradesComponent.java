// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.upgrading;

import org.terasology.engine.entitySystem.Component;

import java.util.List;

/**
 * Contains all the information related to the upgrades possible for this block
 *
 * @see UpgradeList
 * @see UpgradingSystem
 */
public class BlockUpgradesComponent implements Component {
    /**
     * The component the upgrades will apply to. Should be the name without the <code>Component</code> ending. Eg,
     * <code>"SingleTargeterComponent"</code> should be <code>"SingleTargeter"</code>
     */
    public String componentName;
    /**
     * All of the upgrade paths applicable
     */
    public List<UpgradeList> upgrades;

}
