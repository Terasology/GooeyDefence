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
package org.terasology.gooeyDefence.towerBlocks.effectors;

import org.terasology.gooeyDefence.towerBlocks.EffectCount;
import org.terasology.gooeyDefence.towerBlocks.EffectDuration;

/**
 * Deals an initial damage and then a smaller damage over time.
 */
public class PoisonEffectorComponent extends DamageEffectorComponent {
    /**
     * The damage dealt by each iteration of the poisoning
     */
    private int poisonDamage;
    /**
     * How long the poison will last for
     * given in milliseconds
     */
    private int poisonDuration;

    @Override
    public EffectCount getEffectCount() {
        return EffectCount.PER_SHOT;
    }

    @Override
    public EffectDuration getEffectDuration() {
        return EffectDuration.PERMANENT;
    }

    public int getPoisonDamage() {
        return poisonDamage;
    }

    public int getPoisonDuration() {
        return poisonDuration;
    }
}
