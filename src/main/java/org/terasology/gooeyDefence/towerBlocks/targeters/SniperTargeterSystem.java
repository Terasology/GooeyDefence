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

import com.google.common.collect.Sets;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.gooeyDefence.EnemyManager;
import org.terasology.gooeyDefence.events.combat.SelectEnemiesEvent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3f;
import org.terasology.registry.In;

import java.util.Set;

/**
 * Selects a single target from far away, ignoring those close.
 */
@RegisterSystem
public class SniperTargeterSystem extends BaseTargeterSystem {
    @In
    private EnemyManager enemyManager;

    /**
     * Determine which enemies should be attacked.
     * Called against the targeter entity.
     * <p>
     * Filters on {@link LocationComponent} and {@link SniperTargeterComponent}
     *
     * @see SelectEnemiesEvent
     */
    @ReceiveEvent
    public void onSelectEnemies(SelectEnemiesEvent event, EntityRef entity, LocationComponent locationComponent, SniperTargeterComponent targeterComponent) {
        EntityRef target = targeterComponent.getLastTarget();
        Vector3f worldPos = locationComponent.getWorldPosition();
        if (!canUseTarget(target, worldPos, targeterComponent)) {

            Set<EntityRef> outerEnemies = enemyManager.getEnemiesInRange(worldPos, targeterComponent.getRange());
            Set<EntityRef> innerEnemies = enemyManager.getEnemiesInRange(worldPos, targeterComponent.getMinimumRange());
            Set<EntityRef> inRangeEnemies = Sets.difference(outerEnemies, innerEnemies);

            target = getSingleTarget(inRangeEnemies, targeterComponent.getSelectionMethod());
        }
        if (target.exists()) {
            event.addToList(target);
        }
        targeterComponent.setLastTarget(target);
    }

    /**
     * Checks if the given enemy can be targeted
     *
     * @param target            The enemy to check
     * @param targeterPos       The position of the target
     * @param targeterComponent The targeter
     * @return True if the targeter can attack the enemy
     */
    private boolean canUseTarget(EntityRef target, Vector3f targeterPos, SniperTargeterComponent targeterComponent) {
        if (target.exists()) {
            Vector3f enemyLocation = target.getComponent(LocationComponent.class).getWorldPosition();
            float enemyDistance = targeterPos.distanceSquared(enemyLocation);
            return enemyDistance < targeterComponent.getRange() * targeterComponent.getRange()
                    && enemyDistance > targeterComponent.getMinimumRange() * targeterComponent.getMinimumRange();
        } else {
            return false;
        }
    }
}
