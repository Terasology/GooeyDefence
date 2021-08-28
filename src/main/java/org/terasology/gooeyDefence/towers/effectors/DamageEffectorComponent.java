// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.towers.effectors;

import org.terasology.gooeyDefence.towers.EffectCount;
import org.terasology.gooeyDefence.towers.EffectDuration;
import org.terasology.gooeyDefence.towers.components.TowerEffector;

/**
 * Effector that only deals damage to the enemies.
 *
 * @see DamageEffectorSystem
 * @see TowerEffector
 */
public class DamageEffectorComponent<T extends DamageEffectorComponent> extends TowerEffector<T> {
    /**
     * The damage to apply to the targets.
     */
    public int damage;

    @Override
    public EffectCount getEffectCount() {
        return EffectCount.PER_SHOT;
    }

    @Override
    public EffectDuration getEffectDuration() {
        return EffectDuration.INSTANT;
    }


    @Override
    public void copyFrom(T other) {
        super.copyFrom(other);
        this.damage = other.damage;
    }
}
