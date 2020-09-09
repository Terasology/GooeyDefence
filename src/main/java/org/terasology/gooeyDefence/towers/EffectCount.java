// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.towers;

/**
 * Represents how many times an effect should be applied to a given target
 */
public enum EffectCount {
    /**
     * Effect should be applied on every single shot. The effector will be called for every shot whilst the enemy is
     * within range
     */
    PER_SHOT,
    /**
     * The effect should only be applied once to the target. The effector will only be called each time an enemy is
     * targeted.
     */
    CONTINUOUS
}
