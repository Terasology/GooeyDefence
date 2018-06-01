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
package org.terasology.gooeyDefence.towerBlocks.emitters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.gooeyDefence.EnemyManager;
import org.terasology.gooeyDefence.components.GooeyComponent;
import org.terasology.gooeyDefence.events.DoSelectEnemies;
import org.terasology.logic.location.LocationComponent;
import org.terasology.registry.In;

import java.util.Optional;
import java.util.Set;

@RegisterSystem
public class SingleTargetEmitterSystem extends BaseComponentSystem {
    private static final Logger logger = LoggerFactory.getLogger(SingleTargetEmitterSystem.class);
    @In
    private EnemyManager enemyManager;

    /**
     * Determine which enemies should be attacked.
     *
     * @param event             The event to store the result in
     * @param entity            The emitter entity searching
     * @param locationComponent The location component of the entity
     * @param emitterComponent  The emitter component of the entity
     */
    @ReceiveEvent
    public void onDoSelectEnemies(DoSelectEnemies event, EntityRef entity, LocationComponent locationComponent, SingleTargetEmitterComponent emitterComponent) {
        Set<EntityRef> targets = enemyManager.getEnemiesInRange(
                locationComponent.getWorldPosition(),
                emitterComponent.getRange());
        Optional<EntityRef> firstEnemy = targets.stream().max((first, second) -> {
            GooeyComponent firstComponent = first.getComponent(GooeyComponent.class);
            GooeyComponent secondComponent = second.getComponent(GooeyComponent.class);
            return firstComponent.currentStep - secondComponent.currentStep;
        });
        firstEnemy.ifPresent(event::addToList);
    }
}
