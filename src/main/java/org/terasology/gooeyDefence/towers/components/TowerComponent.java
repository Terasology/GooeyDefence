// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.towers.components;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.gooeyDefence.towers.TowerManager;

import java.util.HashSet;
import java.util.Set;

/**
 * Component for the abstract tower entity. Stores the IDs of all the blocks that make up the tower.
 * <p>
 * Only collates together the component parts of the tower. Functionality is provided by the {@link TowerCore}, {@link
 * TowerTargeter} or {@link TowerEffector} components.
 *
 * @see TowerManager
 */
public class TowerComponent implements Component {
    public Set<EntityRef> cores = new HashSet<>();
    public Set<EntityRef> effector = new HashSet<>();
    public Set<EntityRef> targeter = new HashSet<>();
    public Set<EntityRef> plains = new HashSet<>();
}
