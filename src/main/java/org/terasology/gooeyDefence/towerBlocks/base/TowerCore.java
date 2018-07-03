/*
 * Copyright 2017 MovingBlocks
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
package org.terasology.gooeyDefence.towerBlocks.base;

import org.terasology.entitySystem.Component;

/**
 * Base class for all the Core blocks
 * <p>
 * Tower Cores provide power to the other blocks in the tower.
 *
 * @see TowerEffector
 * @see TowerTargeter
 */
public abstract class TowerCore implements Component {
    /**
     * The power this core provides
     */
    private int power;

    /**
     * @return How much power this core provides
     */
    public int getPower() {
        return power;
    }
}
