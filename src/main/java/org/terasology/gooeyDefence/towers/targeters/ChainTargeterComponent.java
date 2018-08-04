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
 * Targeter selects a base enemy and then chains to nearby enemies.
 * The enemies chained to do not have to all be within range, however it is a linear chain.
 */
public class ChainTargeterComponent extends TowerTargeter {
    /**
     * How many chains will be made
     * given in number of enemies
     */
    private int chainLength;
    /**
     * The maximum distance that the chain can jump.
     * given in blocks.
     */
    private int chainRange;

    @Override
    public float getMultiplier() {
        return 0.4f;
    }

    public int getChainLength() {
        return chainLength;
    }

    public int getChainRange() {
        return chainRange;
    }
}
