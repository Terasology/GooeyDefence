// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.towers.components;

import org.terasology.engine.entitySystem.Component;

/**
 * Base class for all the Core blocks
 * <p>
 * Tower Cores provide power to the other blocks in the tower.
 *
 * @see TowerEffector
 * @see TowerTargeter
 */
public abstract class TowerCore implements Component {
    /**
     * The power this core provides
     */
    public int power;

}
