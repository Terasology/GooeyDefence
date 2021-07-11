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
package org.terasology.gooeyDefence.towers.effectors;

import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.gooeyDefence.towers.EffectCount;
import org.terasology.gooeyDefence.towers.EffectDuration;
import org.terasology.gooeyDefence.towers.components.TowerEffector;

/**
 * Slows down an enemy.
 * It does not deal any damage to the enemy.
 *
 * @see IceEffectorSystem
 * @see TowerEffector
 */
public class IceEffectorComponent extends TowerEffector<IceEffectorComponent> {
    /**
     * A multiplier for the enemies speed.
     * 0.9 will make them 10% slower, or 90% of their full speed;
     */
    public float slow;

    @Override
    public EffectCount getEffectCount() {
        return EffectCount.CONTINUOUS;
    }

    @Override
    public EffectDuration getEffectDuration() {
        return EffectDuration.LASTING;
    }

    @Override
    public void copy(IceEffectorComponent other) {
        this.slow = other.slow;
    }


}
