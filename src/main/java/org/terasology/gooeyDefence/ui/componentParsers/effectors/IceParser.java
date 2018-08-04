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
import org.terasology.gooeyDefence.towers.effectors.IceEffectorComponent;
import org.terasology.gooeyDefence.ui.componentParsers.BaseParser;

import java.util.HashMap;
import java.util.Map;

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

    public String slow(boolean isUpgrade, float value) {
        if (!isUpgrade) {
            return (int) ((1 - value) * 100) + "%";
        } else {
            return "+" + (int) (Math.abs(value) * 100) + "%";
        }
    }
}
