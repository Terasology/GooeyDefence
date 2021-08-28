// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.towers.effectors;

import org.terasology.gooeyDefence.towers.EffectCount;
import org.terasology.gooeyDefence.towers.EffectDuration;
import org.terasology.gooeyDefence.towers.components.TowerEffector;

/**
 * Applies a brief stun to an enemy.
 * This stun stops the enemy from moving for the duration of the stun.
 * <p>
 * A stun does not have a 100% chance of being applied on each attack.
 *
 * @see StunEffectorSystem
 * @see TowerEffector
 */
public class StunEffectorComponent extends TowerEffector {
    /**
     * How long the stun should last.-
     * Given in milliseconds.
     */
    public int stunDuration;

    @Override
    public EffectCount getEffectCount() {
        return EffectCount.PER_SHOT;
    }

    @Override
    public EffectDuration getEffectDuration() {
        return EffectDuration.LASTING;
    }

}
