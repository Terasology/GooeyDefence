// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.towers.targeters;

import org.terasology.gooeyDefence.towers.components.TowerTargeter;

/**
 * Selects a base enemy and then splashes to nearby enemies in a small radius.
 *
 * @see SplashTargeterSystem
 * @see TowerTargeter
 */
public class SplashTargeterComponent extends SingleTargeterComponent {
    /**
     * The range of the splash around the chosen target given in blocks.
     */
    public float splashRange;

    @Override
    public float getMultiplier() {
        return 0.8f;
    }

}
