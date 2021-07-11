// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.towers.components;

import com.google.common.collect.Sets;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.gooeyDefence.towers.TowerManager;

import java.util.HashSet;
import java.util.Set;

/**
 * Component for the abstract tower entity.
 * Stores the IDs of all the blocks that make up the tower.
 * <p>
 * Only collates together the component parts of the tower. Functionality is
 * provided by the {@link TowerCore}, {@link TowerTargeter} or {@link TowerEffector} components.
 *
 * @see TowerManager
 */
public class TowerComponent implements Component<TowerComponent> {
    public Set<EntityRef> cores = new HashSet<>();
    public Set<EntityRef> effector = new HashSet<>();
    public Set<EntityRef> targeter = new HashSet<>();
    public Set<EntityRef> plains = new HashSet<>();

    @Override
    public void copy(TowerComponent other) {
        this.cores = Sets.newHashSet(other.cores);
        this.effector = Sets.newHashSet(other.effector);
        this.targeter = Sets.newHashSet(other.targeter);
        this.plains = Sets.newHashSet(other.plains);
    }
}
