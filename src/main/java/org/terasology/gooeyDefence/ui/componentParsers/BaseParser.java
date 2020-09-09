// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.ui.componentParsers;

import org.terasology.engine.entitySystem.Component;
import org.terasology.gooeyDefence.ui.towers.UIComponentFields;
import org.terasology.gooeyDefence.upgrading.UpgradingSystem;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides information on how to convert data from a component or upgrade into human readable strings.
 *
 * @see UIComponentFields
 * @see UpgradingSystem
 */
public abstract class BaseParser {
    /**
     * @return The class that this parser applies to.
     */
    public abstract Class<? extends Component> getComponentClass();

    /**
     * The fields that should be displayed as well as the display name.
     *
     * @return A mapping between field and display name
     */
    public Map<String, String> getFields() {
        Map<String, String> result = new HashMap<>();
        result.put("drain", "Drain");
        return result;
    }

    /**
     * Converts a raw value from a field into a human readable version.
     *
     * @param field The name of the field being converted.
     * @param rawValue The value of the field being converted.
     * @return The human readable string to display.
     */
    public String handleField(String field, Object rawValue) {
        return rawValue.toString();
    }

    /**
     * Converts a raw upgrade value into a human readable version
     *
     * @param field The name of the field being converted
     * @param rawValue The value of the upgrade field being converted
     * @return The human readable variant of the field.
     */
    public String handleUpgrade(String field, Object rawValue) {
        return "+" + handleField(field, rawValue);
    }

    /**
     * Helper method to convert a duration from milliseconds into seconds, to 1dp
     *
     * @param duration The duration to convert
     * @return The duration as a string.
     */
    protected String convertDuration(Number duration) {
        return String.format("%.1fs", duration.floatValue() / 1000);
    }
}
