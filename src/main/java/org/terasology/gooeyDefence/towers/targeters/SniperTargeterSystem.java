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

import com.google.common.collect.Sets;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.gooeyDefence.EnemyManager;
import org.terasology.gooeyDefence.towers.TowerManager;
import org.terasology.gooeyDefence.towers.events.SelectEnemiesEvent;
import org.terasology.gooeyDefence.visuals.InWorldRenderer;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3f;
import org.terasology.registry.In;

import java.util.Set;

/**
 * Selects a single target from far away. The targeter cannot select nearby enemies.
 *
 * @see SniperTargeterComponent
 * @see TowerManager
 */
@RegisterSystem
public class SniperTargeterSystem extends BaseTargeterSystem {
    @In
    private EnemyManager enemyManager;
    @In
    private InWorldRenderer inWorldRenderer;

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
        Vector3f worldPos = locationComponent.getWorldPosition();
        EntityRef target = getTarget(worldPos, targeterComponent);
        if (target.exists()) {
            event.addToList(target);
            inWorldRenderer.shootBulletTowards(
                    target,
                    locationComponent.getWorldPosition());
        }
        targeterComponent.lastTarget = target;
    }

    /**
     * Checks if the given enemy can be targeted
     *
     * @param target            The enemy to check
     * @param targeterPos       The position of the target
     * @param targeterComponent The targeter
     * @return True if the targeter can attack the enemy
     */
    protected boolean canUseTarget(EntityRef target, Vector3f targeterPos, SniperTargeterComponent targeterComponent) {
        if (target.exists()) {
            Vector3f enemyLocation = target.getComponent(LocationComponent.class).getWorldPosition();
            float enemyDistance = targeterPos.distanceSquared(enemyLocation);
            return enemyDistance < targeterComponent.range * targeterComponent.range
                    && enemyDistance > targeterComponent.minimumRange * targeterComponent.minimumRange;
        } else {
            return false;
        }
    }

    /**
     * Gets a single targetable enemy within the tower's range
     * <p>
     * Attempts to use the entity that was targeted last round.
     * If that is not possible it picks an enemy in range based on the selection method listed
     *
     * @param targeterPos       The position of the targeter block
     * @param targeterComponent The targeter component on the targeter
     * @return A suitable enemy in range, or the null entity if none was found
     */
    protected EntityRef getTarget(Vector3f targeterPos, SniperTargeterComponent targeterComponent) {
        EntityRef target = targeterComponent.lastTarget;
        if (!canUseTarget(target, targeterPos, targeterComponent)) {

            Set<EntityRef> outerEnemies = enemyManager.getEnemiesInRange(targeterPos, targeterComponent.range);
            Set<EntityRef> innerEnemies = enemyManager.getEnemiesInRange(targeterPos, targeterComponent.minimumRange);
            Set<EntityRef> inRangeEnemies = Sets.difference(outerEnemies, innerEnemies);

            target = getSingleTarget(inRangeEnemies, targeterComponent.selectionMethod);
        }
        return target;
    }
}
