// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.ui.componentParsers.targeters;

import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.gooeyDefence.towers.targeters.ChainTargeterComponent;
import org.terasology.gooeyDefence.ui.towers.UIUpgrader;
import org.terasology.gooeyDefence.upgrading.UpgradingSystem;

import java.util.Map;

/**
 * Converts values for the chain targeter
 *
 * @see ChainTargeterComponent
 * @see UIUpgrader
 * @see UpgradingSystem
 */
public class ChainParser extends SingleParser {
    @Override
    public Class<? extends Component> getComponentClass() {
        return ChainTargeterComponent.class;
    }

    @Override
    public Map<String, String> getFields() {
        Map<String, String> result = super.getFields();
        result.put("chainLength", "Maximum number of chains");
        result.put("chainRange", "Distance to chain over");
        return result;
    }

}
