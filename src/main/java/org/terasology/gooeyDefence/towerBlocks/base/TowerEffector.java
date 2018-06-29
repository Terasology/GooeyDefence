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
import org.terasology.gooeyDefence.towerBlocks.EffectDuration;
import org.terasology.gooeyDefence.towerBlocks.EffectCount;

/**
 * Base class for all the Effector blocks.
 * <p>
 * Effectors apply damage and special effects to the enemies.
 * They require power, provided by {@link TowerCore}'s
 *
 * @see TowerCore
 * @see TowerTargeter
 */
public abstract class TowerEffector implements Component {
    private int drain;

    public abstract EffectCount getEffectCount();
    public abstract EffectDuration getEffectDuration();

    /**
     * @return The amount of power required by this Effector
     */
    public int getDrain() {
        return drain;
    }
}
