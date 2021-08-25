// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.ui.componentParsers.effectors;

import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.gooeyDefence.towers.effectors.FireEffectorComponent;
import org.terasology.gooeyDefence.ui.towers.UIUpgrader;
import org.terasology.gooeyDefence.upgrading.UpgradingSystem;

import java.util.Map;

/**
 * Handles converting fields for the fire effector
 *
 * @see FireEffectorComponent
 * @see UIUpgrader
 * @see UpgradingSystem
 */
public class FireParser extends DamageParser {
    @Override
    public Class<? extends Component> getComponentClass() {
        return FireEffectorComponent.class;
    }

    @Override
    public Map<String, String> getFields() {
        Map<String, String> result = super.getFields();
        result.put("fireDuration", "Burn Duration");
        return result;
    }

    /**
     * Converts the duration field from milliseconds into seconds.
     *
     * @param isUpgrade True if the value is actually an upgrade value
     * @param value     The value to convert
     * @return The string version of the value.
     */
    public String fireDuration(boolean isUpgrade, int value) {
        return (isUpgrade ? "+" : "") + convertDuration(value);
    }
}
