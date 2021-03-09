/*
 * Copyright 2018 MovingBlocks
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
package org.terasology.gooeyDefence.towers.events;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.Event;
import org.terasology.gooeyDefence.towers.TowerBuildSystem;
import org.terasology.gooeyDefence.towers.components.TowerComponent;

import java.util.Collections;
import java.util.Set;

/**
 * Event sent when a tower is changed.
 * Note that this event can only be sent when a block is added.
 * If a block is destroyed the whole tower is destroyed and rebuilt
 * <p>
 * Sent against the newly changed tower
 *
 * @see TowerComponent
 * @see TowerBuildSystem
 * @see TowerDestroyedEvent
 * @see TowerCreatedEvent
 */
public class OnBlocksAdded implements Event {
    /*TODO: split this into the different block types */
    private final Set<EntityRef> changedBlocks;

    public OnBlocksAdded(EntityRef changedBlock) {
        changedBlocks = Collections.singleton(changedBlock);
    }

    public OnBlocksAdded(Set<EntityRef> changedBlocks) {
        this.changedBlocks = changedBlocks;
    }

    /**
     * @return All the blocks that were added to the tower.
     */
    public Set<EntityRef> getAddedBlock() {
        return changedBlocks;
    }
}
