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
import org.terasology.gooeyDefence.components.SavedDataComponent;
import org.terasology.gooeyDefence.components.towers.TowerComponent;
import org.terasology.gooeyDefence.events.OnFieldActivated;
import org.terasology.gooeyDefence.events.TowerCreatedEvent;
import org.terasology.gooeyDefence.events.TowerDestroyedEvent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.In;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RegisterSystem
public class TowerManager extends BaseComponentSystem {
    private Set<EntityRef> towerEntities = new HashSet<>();

    @In
    private TowerBuildSystem towerBuildSystem;

    @Override
    public void preAutoSave() {
        preSave();
    }

    @Override
    public void preSave() {
        SavedDataComponent component = DefenceField.shrineEntity.getComponent(SavedDataComponent.class);
        if (component != null) {
            Set<Set<Vector3i>> towerData = new HashSet<>();
            for (EntityRef entity : towerEntities) {
                Set<Vector3i> blockPos = towerBuildSystem.getAllFrom(entity)
                        .stream()
                        .map(block -> block.getComponent(LocationComponent.class).getWorldPosition())
                        .map(Vector3i::new)
                        .collect(Collectors.toSet());
                towerData.add(blockPos);
            }
            component.setTowers(towerData);
        }
    }

    @ReceiveEvent
    public void onFieldActivated(OnFieldActivated event, EntityRef dataEntity, SavedDataComponent savedData) {
        savedData.getTowers().forEach(towerBuildSystem::loadTower);
    }

    @ReceiveEvent
    public void onTowerCreated(TowerCreatedEvent event, EntityRef entity, TowerComponent component) {
        towerEntities.add(entity);
    }

    @ReceiveEvent
    public void onTowerDestroyed(TowerDestroyedEvent event, EntityRef entity, TowerComponent component) {
        towerEntities.remove(entity);
    }


}
