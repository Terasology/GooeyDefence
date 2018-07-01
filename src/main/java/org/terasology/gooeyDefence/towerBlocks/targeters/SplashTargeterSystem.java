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
package org.terasology.gooeyDefence.towerBlocks.targeters;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.gooeyDefence.EnemyManager;
import org.terasology.gooeyDefence.events.combat.SelectEnemiesEvent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.registry.In;

/**
 * Selects a single target enemy and then targets all enemies within a small range of that enemy.
 */
@RegisterSystem
public class SplashTargeterSystem extends BaseTargeterSystem {

    @In
    private EnemyManager enemyManager;

    /**
     * Determine which enemies should be attacked.
     * Called against the targeter entity.
     * <p>
     * Filters on {@link LocationComponent} and {@link SplashTargeterComponent}
     *
     * @see SelectEnemiesEvent
     */
    @ReceiveEvent
    public void onSelectEnemies(SelectEnemiesEvent event, EntityRef entity, LocationComponent locationComponent, SplashTargeterComponent targeterComponent) {
        EntityRef target = getTarget(locationComponent.getWorldPosition(), targeterComponent, enemyManager);

        if (target.exists()) {
            LocationComponent targetLocation = target.getComponent(LocationComponent.class);
            event.addToList(enemyManager.getEnemiesInRange(targetLocation.getWorldPosition(), targeterComponent.getSplashRange()));
        }
        targeterComponent.setLastTarget(target);
    }
}
