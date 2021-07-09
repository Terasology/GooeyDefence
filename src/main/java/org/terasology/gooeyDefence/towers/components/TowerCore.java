// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.towers.components;

import org.terasology.gestalt.entitysystem.component.Component;

/**
 * Base class for all the Core blocks
 * <p>
 * Tower Cores provide power to the other blocks in the tower.
 *
 * @see TowerEffector
 * @see TowerTargeter
 */
public abstract class TowerCore implements Component<TowerCore> {
    /**
     * The power this core provides
     */
    public int power;

}
