// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.towers;

/**
 * Options for how long the effect should last.
 */
public enum EffectDuration {
    /**
     * Effect does not take any time. The effect will never be called to be removed.
     */
    INSTANT,
    /**
     * Effect lasts for the duration that the enemy is targeted. The effect will be called to be removed once it is no
     * longer targeted.
     */
    LASTING,
    /**
     * Effect lasts for longer than the enemy is targeted. The effect will never be called to be removed, but the
     * implementing system may remove it.
     */
    PERMANENT
}
