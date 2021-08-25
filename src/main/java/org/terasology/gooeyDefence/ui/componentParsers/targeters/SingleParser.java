// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.ui.componentParsers.targeters;

import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.gooeyDefence.towers.targeters.SingleTargeterComponent;
import org.terasology.gooeyDefence.ui.componentParsers.BaseParser;
import org.terasology.gooeyDefence.ui.towers.UIUpgrader;
import org.terasology.gooeyDefence.upgrading.UpgradingSystem;

import java.util.Map;

/**
 * Converts values for the Aoe targeter
 *
 * @see SingleTargeterComponent
 * @see UIUpgrader
 * @see UpgradingSystem
 */
public class SingleParser extends BaseParser {
    @Override
    public Class<? extends Component> getComponentClass() {
        return SingleTargeterComponent.class;
    }

    @Override
    public Map<String, String> getFields() {
        Map<String, String> result = super.getFields();
        result.put("range", "Range");
        result.put("attackSpeed", "Attack Speed");
        result.put("selectionMethod", "Selection Method");
        return result;
    }

    /**
     * Converts the attack speed from milliseconds to seconds (1dp)
     *
     * @param isUpgrade True if the value is an upgrade
     * @param value     The value to convert
     * @return The string version of the value
     */
    public String attackSpeed(boolean isUpgrade, int value) {
        return convertDuration(value);
    }
}
