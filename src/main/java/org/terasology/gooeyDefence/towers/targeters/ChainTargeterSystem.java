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
import org.terasology.gooeyDefence.towers.events.SelectEnemiesEvent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3f;
import org.terasology.registry.In;

import java.util.Set;

/**
 * Targets a base enemy and then chains off to nearby enemies as well.
 */
@RegisterSystem
public class ChainTargeterSystem extends BaseTargeterSystem {

    @In
    private EnemyManager enemyManager;

    /**
     * Determine which enemies should be attacked.
     * Called against the targeter entity.
     * <p>
     * Filters on {@link LocationComponent} and {@link ChainTargeterComponent}
     *
     * @see SelectEnemiesEvent
     */
    @ReceiveEvent
    public void onSelectEnemies(SelectEnemiesEvent event, EntityRef entity, LocationComponent locationComponent, ChainTargeterComponent targeterComponent) {
        EntityRef target = getTarget(locationComponent.getWorldPosition(), targeterComponent, enemyManager);

        if (target.exists()) {
            event.addToList(chainToNearby(target, targeterComponent.getChainLength(), targeterComponent.getChainRange()));
        }
        targeterComponent.lastTarget = target;
    }

    /**
     * Chain to nearby enemies from a starting enemy.
     *
     * @param start      The enemy to start chaining from
     * @param maxChain   The maximum number of enemies to chain to.
     * @param chainRange The maximum length of each chain link
     * @return A set containing all chained enemies.
     */
    private Set<EntityRef> chainToNearby(EntityRef start, int maxChain, float chainRange) {
        Vector3f position = start.getComponent(LocationComponent.class).getWorldPosition();
        Set<EntityRef> result = Sets.newHashSet(start);

        for (int i = 0; i < maxChain; i++) {
            Set<EntityRef> enemies = enemyManager.getEnemiesInRange(position, chainRange);
            enemies = Sets.difference(enemies, result);
            if (enemies.isEmpty()) {
                return result;
            }
            EntityRef closestEnemy = enemies.stream().min((first, second) -> {
                LocationComponent firstComponent = first.getComponent(LocationComponent.class);
                LocationComponent secondComponent = second.getComponent(LocationComponent.class);
                float firstDistance = firstComponent.getWorldPosition().distanceSquared(position);
                float secondDistance = secondComponent.getWorldPosition().distanceSquared(position);
                return Float.compare(firstDistance, secondDistance);
            }).orElse(EntityRef.NULL);
            result.add(closestEnemy);
        }
        return result;
    }


}
