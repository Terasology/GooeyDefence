// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.ui.componentParsers.targeters;

import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.gooeyDefence.towers.targeters.SniperTargeterComponent;
import org.terasology.gooeyDefence.ui.towers.UIUpgrader;
import org.terasology.gooeyDefence.upgrading.UpgradingSystem;

import java.util.Map;

/**
 * Converts values for the Aoe targeter
 *
 * @see SniperTargeterComponent
 * @see UIUpgrader
 * @see UpgradingSystem
 */
public class SniperParser extends SingleParser {
    @Override
    public Class<? extends Component> getComponentClass() {
        return SniperTargeterComponent.class;
    }

    @Override
    public Map<String, String> getFields() {
        Map<String, String> result = super.getFields();
        result.put("minimumRange", "Minimum Range");
        return result;
    }

}
