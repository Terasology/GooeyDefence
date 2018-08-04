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
package org.terasology.gooeyDefence.towers.targeters;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.gooeyDefence.EnemyManager;
import org.terasology.gooeyDefence.InWorldRenderer;
import org.terasology.gooeyDefence.towers.events.SelectEnemiesEvent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.registry.In;

import java.util.Set;

/**
 *
 */
@RegisterSystem
public class AoeTargeterSystem extends BaseTargeterSystem {

    @In
    private EnemyManager enemyManager;

    @In
    private InWorldRenderer inWorldRenderer;

    /**
     * Targets enemies in an aoe around the tower.
     * <p>
     * Filters on {@link LocationComponent} and {@link AoeTargeterComponent}.
     *
     * @see SelectEnemiesEvent
     */
    @ReceiveEvent
    public void onSelectEnemies(SelectEnemiesEvent event, EntityRef entity, LocationComponent locationComponent, AoeTargeterComponent targeterComponent) {
        Set<EntityRef> targets = enemyManager.getEnemiesInRange(locationComponent.getWorldPosition(), targeterComponent.getRange());
        event.addToList(targets);
        if (!targets.isEmpty()) {
            inWorldRenderer.displayExpandingSphere(locationComponent.getWorldPosition(), (float) targeterComponent.getAttackSpeed() / 1000, targeterComponent.getRange() * 2 + 1);
        }
    }
}
