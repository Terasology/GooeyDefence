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
package org.terasology.gooeyDefence.towers.events;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.event.Event;
import org.terasology.gooeyDefence.towers.components.TowerTargeter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Event sent to select the enemies that will be attacked.
 * Sent against the targeter blocks in the tower.
 *
 * @see TowerTargeter
 */
public class SelectEnemiesEvent implements Event {
    private final Set<EntityRef> targets = new HashSet<>();

    /**
     * This method should only be used by the sending system after the event has been sent and processed
     *
     * @return The targets that have been selected by this event.
     */
    public Set<EntityRef> getTargets() {
        return targets;
    }

    /**
     * @param newTargets A collection of all the enemies to add to the target list
     */
    public void addToList(Collection<EntityRef> newTargets) {
        this.targets.addAll(newTargets);
    }

    /**
     * @param target The single enemy to add to this list
     */
    public void addToList(EntityRef target) {
        this.targets.add(target);
    }
}
