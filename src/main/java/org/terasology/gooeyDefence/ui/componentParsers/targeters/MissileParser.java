// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.ui.componentParsers.targeters;

import org.terasology.engine.entitySystem.Component;
import org.terasology.gooeyDefence.towers.targeters.MissileTargeterComponent;
import org.terasology.gooeyDefence.ui.towers.UIUpgrader;
import org.terasology.gooeyDefence.upgrading.UpgradingSystem;

import java.util.Map;

/**
 * Converts values for the Aoe targeter
 *
 * @see MissileTargeterComponent
 * @see UIUpgrader
 * @see UpgradingSystem
 */
public class MissileParser extends SniperParser {
    @Override
    public Class<? extends Component> getComponentClass() {
        return MissileTargeterComponent.class;
    }

    @Override
    public Map<String, String> getFields() {
        Map<String, String> result = super.getFields();
        result.put("splashRange", "Splash Radius");
        return result;
    }

}
