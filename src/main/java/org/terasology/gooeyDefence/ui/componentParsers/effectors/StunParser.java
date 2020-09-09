// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.ui.componentParsers.effectors;

import org.terasology.engine.entitySystem.Component;
import org.terasology.gooeyDefence.towers.effectors.StunEffectorComponent;
import org.terasology.gooeyDefence.ui.componentParsers.BaseParser;
import org.terasology.gooeyDefence.ui.towers.UIUpgrader;
import org.terasology.gooeyDefence.upgrading.UpgradingSystem;

import java.util.HashMap;
import java.util.Map;

/**
 * Converts values from the stun effector
 *
 * @see StunEffectorComponent
 * @see UIUpgrader
 * @see UpgradingSystem
 */
public class StunParser extends BaseParser {
    @Override
    public Class<? extends Component> getComponentClass() {
        return StunEffectorComponent.class;
    }

    @Override
    public Map<String, String> getFields() {
        Map<String, String> result = new HashMap<>();
        result.put("stunDuration", "Stun Duration");
        return result;
    }

    /**
     * Converts the duration from milliseconds to seconds.
     *
     * @param isUpgrade True if the value is an upgrade
     * @param value The value to convert
     * @return The value as a string.
     */
    public String stunDuration(boolean isUpgrade, int value) {
        return isUpgrade ? "+" : "" + convertDuration(value);
    }
}
