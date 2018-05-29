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
package org.terasology.gooeyDefence;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.gooeyDefence.components.towers.TowerComponent;
import org.terasology.gooeyDefence.events.TowerCreatedEvent;
import org.terasology.gooeyDefence.events.TowerDestroyedEvent;

import java.util.HashSet;
import java.util.Set;

@RegisterSystem
public class TowerManager extends BaseComponentSystem {
    private Set<EntityRef> towerEntities = new HashSet<>();

    @ReceiveEvent
    public void onTowerCreated(TowerCreatedEvent event, EntityRef entity, TowerComponent component) {
        towerEntities.add(entity);
    }

    @ReceiveEvent
    public void onTowerDestroyed(TowerDestroyedEvent event, EntityRef entity, TowerComponent component) {
        towerEntities.remove(entity);
    }


}
