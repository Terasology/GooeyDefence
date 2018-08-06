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
import org.terasology.entitySystem.entity.EntityRef;

import java.util.HashSet;
import java.util.Set;

/**
 * Base class for all the Targeter blocks.
 * <p>
 * Targeters select the enemies the tower will attack.
 * They require power, provided by {@link TowerCore}s
 * <p>
 * Provides a number of common properties.
 *
 * @see TowerCore
 * @see TowerEffector
 */
public abstract class TowerTargeter implements Component {
    /**
     * How much energy this targeter will use
     */
    public int drain;
    /**
     * The range of this targeter
     * given in blocks
     */
    public int range;
    /**
     * The time between attacks for this targeter
     * given in ms
     */
    public int attackSpeed;
    /**
     * All enemies hit by an effect last attack
     */
    public Set<EntityRef> affectedEnemies = new HashSet<>();

    /**
     * A balancing multiplier passed to effectors on this tower.
     * It's used to provide balancing between different tower types.
     *
     * @return The multiplier to be passed to all effectors
     */
    public abstract float getMultiplier();

}
