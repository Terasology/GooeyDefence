// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.towers.components;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.world.block.ForceBlockActive;
import org.terasology.gestalt.entitysystem.component.Component;

/**
 * Used to mark the block as being used in towers.
 * <p>
 * Stores a reference to the tower entity this block belongs to.
 *
 * @see TowerComponent
 */
@ForceBlockActive
public class TowerMultiBlockComponent implements Component<TowerMultiBlockComponent> {
    private EntityRef towerEntity = EntityRef.NULL;

    public EntityRef getTowerEntity() {
        return towerEntity;
    }

    public void setTowerEntity(EntityRef towerEntity) {
        this.towerEntity = towerEntity;
    }

    @Override
    public void copy(TowerMultiBlockComponent other) {
        this.towerEntity = other.towerEntity;
    }
}
