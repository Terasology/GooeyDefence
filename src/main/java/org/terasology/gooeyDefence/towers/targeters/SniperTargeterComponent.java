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
package org.terasology.gooeyDefence.towers.targeters;

import org.terasology.gooeyDefence.towers.components.TowerTargeter;

/**
 * Only targets enemies that are far away.
 * Does higher damage but cannot target those nearby
 */
public class SniperTargeterComponent extends TowerTargeter {
    /**
     * How far away enemies have to be before they can be selected.
     */
    private float minimumRange;

    @Override
    public float getMultiplier() {
        return 2;
    }

    public float getMinimumRange() {
        return minimumRange;
    }
}
