/*
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.gooeyDefence.components.towers;

import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.world.block.ForceBlockActive;

/**
 * Used to mark the block as being used in towers.
 * <p>
 * Stores a reference to the tower entity this block belongs to.
 *
 * @see TowerComponent
 */
@ForceBlockActive
public class TowerMultiBlockComponent implements Component {
    private EntityRef towerEntity = EntityRef.NULL;

    public EntityRef getTowerEntity() {
        return towerEntity;
    }

    public void setTowerEntity(EntityRef towerEntity) {
        this.towerEntity = towerEntity;
    }
}
