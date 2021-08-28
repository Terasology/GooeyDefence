// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.ui.componentParsers.effectors;

import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.gooeyDefence.towers.effectors.DamageEffectorComponent;
import org.terasology.gooeyDefence.ui.componentParsers.BaseParser;
import org.terasology.gooeyDefence.ui.towers.UIUpgrader;
import org.terasology.gooeyDefence.upgrading.UpgradingSystem;

import java.util.Map;

/**
 * Handles conversion of fields for the damage effector.
 *
 * @see DamageEffectorComponent
 * @see UIUpgrader
 * @see UpgradingSystem
 */
public class DamageParser extends BaseParser {
    @Override
    public Class<? extends Component> getComponentClass() {
        return DamageEffectorComponent.class;
    }

    @Override
    public Map<String, String> getFields() {
        Map<String, String> result = super.getFields();
        result.put("damage", "Damage");
        return result;
    }
}
