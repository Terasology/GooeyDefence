// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.ui.componentParsers.effectors;

import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.gooeyDefence.towers.effectors.IceEffectorComponent;
import org.terasology.gooeyDefence.ui.componentParsers.BaseParser;
import org.terasology.gooeyDefence.ui.towers.UIUpgrader;
import org.terasology.gooeyDefence.upgrading.UpgradingSystem;

import java.util.HashMap;
import java.util.Map;

/**
 * Converts the ice effector values
 *
 * @see IceEffectorComponent
 * @see UIUpgrader
 * @see UpgradingSystem
 */
public class IceParser extends BaseParser {
    @Override
    public Class<? extends Component> getComponentClass() {
        return IceEffectorComponent.class;
    }

    @Override
    public Map<String, String> getFields() {
        Map<String, String> result = new HashMap<>();
        result.put("slow", "Slow");
        return result;
    }

    /**
     * Converts the slow multiplier into a percentage
     *
     * @param isUpgrade If the value is actually an upgrade value
     * @param value     The value to convert
     * @return A string version of the value
     */
    public String slow(boolean isUpgrade, float value) {
        if (!isUpgrade) {
            return (int) ((1 - value) * 100) + "%";
        } else {
            return "+" + (int) (Math.abs(value) * 100) + "%";
        }
    }
}
