// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.towers.targeters;

import org.terasology.gooeyDefence.towers.components.TowerTargeter;

/**
 * Targeter selects a base enemy and then chains to nearby enemies. The enemies chained to do not have to all be within
 * range, however it is a linear chain.
 *
 * @see TowerTargeter
 * @see ChainTargeterSystem
 */
public class ChainTargeterComponent extends SingleTargeterComponent {
    /**
     * How many chains will be made given in number of enemies
     */
    public int chainLength;
    /**
     * The maximum distance that the chain can jump. given in blocks.
     */
    public int chainRange;

    @Override
    public float getMultiplier() {
        return 0.4f;
    }

}
