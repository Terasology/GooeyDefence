// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.towers.components;

import com.google.common.collect.Sets;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.component.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Base class for all the Targeter blocks.
 * <p>
 * Targeters select the enemies the tower will attack.
 * They require power, provided by {@link TowerCore}s
 * <p>
 * Provides a number of common properties.
 *
 * @see TowerCore
 * @see TowerEffector
 */
public abstract class TowerTargeter<T extends TowerTargeter> implements Component<T> {
    /**
     * How much energy this targeter will use
     */
    public int drain;
    /**
     * The range of this targeter
     * given in blocks
     */
    public int range;
    /**
     * The time between attacks for this targeter
     * given in ms
     */
    public int attackSpeed;
    /**
     * All enemies hit by an effect last attack
     */
    public Set<EntityRef> affectedEnemies = new HashSet<>();

    /**
     * A balancing multiplier passed to effectors on this tower.
     * It's used to provide balancing between different tower types.
     *
     * @return The multiplier to be passed to all effectors
     */
    public abstract float getMultiplier();

    @Override
    public void copyFrom(T other) {
        this.drain = other.drain;
        this.range = other.range;
        this.attackSpeed = other.attackSpeed;
        this.affectedEnemies = Sets.newHashSet(other.affectedEnemies);
    }
}
