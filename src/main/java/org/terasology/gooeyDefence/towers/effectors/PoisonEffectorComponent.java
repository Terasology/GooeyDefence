// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.towers.effectors;

import org.terasology.gooeyDefence.towers.EffectCount;
import org.terasology.gooeyDefence.towers.EffectDuration;
import org.terasology.gooeyDefence.towers.components.TowerEffector;

/**
 * Deals an initial damage and then a smaller damage over time.
 *
 * @see PoisonEffectorSystem
 * @see TowerEffector
 */
public class PoisonEffectorComponent extends DamageEffectorComponent<PoisonEffectorComponent> {
    /**
     * The damage dealt by each iteration of the poisoning
     */
    public int poisonDamage;
    /**
     * How long the poison will last for
     * given in milliseconds
     */
    public int poisonDuration;

    @Override
    public EffectCount getEffectCount() {
        return EffectCount.PER_SHOT;
    }

    @Override
    public EffectDuration getEffectDuration() {
        return EffectDuration.PERMANENT;
    }

    @Override
    public void copy(PoisonEffectorComponent other) {
        super.copy(other);
        this.poisonDamage = other.poisonDamage;
        this.poisonDuration = other.poisonDuration;
    }
}
