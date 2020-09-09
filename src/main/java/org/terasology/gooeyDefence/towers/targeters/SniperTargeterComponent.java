// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.towers.targeters;

import org.terasology.gooeyDefence.towers.components.TowerTargeter;

/**
 * Only targets enemies that are far away. Does higher damage but cannot target those nearby
 *
 * @see SniperTargeterSystem
 * @see TowerTargeter
 */
public class SniperTargeterComponent extends SingleTargeterComponent {
    /**
     * How far away enemies have to be before they can be selected.
     */
    public float minimumRange;

    @Override
    public float getMultiplier() {
        return 2;
    }

}
