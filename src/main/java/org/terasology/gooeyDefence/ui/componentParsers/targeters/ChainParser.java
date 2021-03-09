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
package org.terasology.gooeyDefence.ui.componentParsers.targeters;

import org.terasology.engine.entitySystem.Component;
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
