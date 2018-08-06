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
package org.terasology.gooeyDefence.towers.components;

import org.terasology.entitySystem.Component;
import org.terasology.gooeyDefence.towers.EffectCount;
import org.terasology.gooeyDefence.towers.EffectDuration;

/**
 * Base class for all the Effector blocks.
 * <p>
 * Effectors apply damage and special effects to the enemies.
 * They require power, provided by {@link TowerCore}s
 *
 * @see TowerCore
 * @see TowerTargeter
 */
public abstract class TowerEffector implements Component {
    /**
     * The amount of power that the effector requires
     */
    public int drain;

    /**
     * Controls how often the effect should be applied on a more abstract level.
     *
     * @return The amount of times that this effect should be applied
     * @see EffectCount
     */
    public abstract EffectCount getEffectCount();

    /**
     * Controls how the effect should be called to be removed.
     *
     * @return How long the effect is intended to last.
     * @see EffectDuration
     */
    public abstract EffectDuration getEffectDuration();

}
