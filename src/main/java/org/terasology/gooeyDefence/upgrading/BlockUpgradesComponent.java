// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.upgrading;

import com.google.common.collect.Lists;
import org.terasology.gestalt.entitysystem.component.Component;

import java.util.List;

/**
 * Contains all the information related to the upgrades possible for this block
 *
 * @see UpgradeList
 * @see UpgradingSystem
 */
public class BlockUpgradesComponent implements Component<BlockUpgradesComponent> {
    /**
     * The component the upgrades will apply to.
     * Should be the name without the <code>Component</code> ending.
     * Eg, <code>"SingleTargeterComponent"</code> should be <code>"SingleTargeter"</code>
     */
    public String componentName;
    /**
     * All of the upgrade paths applicable
     */
    public List<UpgradeList> upgrades = Lists.newArrayList();

    @Override
    public void copy(BlockUpgradesComponent other) {
        this.componentName = other.componentName;
        this.upgrades = Lists.newArrayList(other.upgrades);
    }
}
