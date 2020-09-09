// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.towers.effectors;

import org.terasology.gooeyDefence.towers.EffectCount;
import org.terasology.gooeyDefence.towers.EffectDuration;
import org.terasology.gooeyDefence.towers.components.TowerEffector;

/**
 * Deals a damage over time to the entity, that has a chance of spreading to other enemies.
 *
 * @see FireEffectorSystem
 * @see TowerEffector
 */
public class FireEffectorComponent extends DamageEffectorComponent {
    /**
     * How long the enemy is on fire for. given in milliseconds
     */
    public int fireDuration;

    @Override
    public EffectCount getEffectCount() {
        return EffectCount.CONTINUOUS;
    }

    @Override
    public EffectDuration getEffectDuration() {
        return EffectDuration.PERMANENT;
    }

}
