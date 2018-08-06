/*
 * Copyright 2018 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.gooeyDefence.ui.componentParsers.effectors;

import org.terasology.entitySystem.Component;
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
     * @param value     The value to convert
     * @return The value as a string.
     */
    public String stunDuration(boolean isUpgrade, int value) {
        return isUpgrade ? "+" : "" + convertDuration(value);
    }
}
