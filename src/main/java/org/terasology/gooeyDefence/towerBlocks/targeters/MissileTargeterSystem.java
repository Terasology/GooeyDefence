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
import org.terasology.gooeyDefence.InWorldRenderer;
import org.terasology.gooeyDefence.components.SplashBulletComponent;
import org.terasology.gooeyDefence.events.combat.SelectEnemiesEvent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3f;
import org.terasology.registry.In;

import java.util.Set;

/**
 * Uses the same base target selection as the SniperTargeter
 * <p>
 * Targets in an AOE around a distant enemy.
 *
 * @see SniperTargeterSystem
 */
@RegisterSystem
public class MissileTargeterSystem extends SniperTargeterSystem {

    @In
    private EnemyManager enemyManager;
    @In
    private InWorldRenderer inWorldRenderer;

    /**
     * Determine which enemies should be attacked.
     * Called against the targeter entity.
     * <p>
     * Filters on {@link LocationComponent} and {@link MissileTargeterComponent}
     *
     * @see SelectEnemiesEvent
     */
    @ReceiveEvent
    public void onSelectEnemies(SelectEnemiesEvent event, EntityRef entity, LocationComponent locationComponent, MissileTargeterComponent targeterComponent) {

        Vector3f worldPos = locationComponent.getWorldPosition();
        EntityRef target = getTarget(worldPos, targeterComponent, enemyManager);

        if (target.exists()) {
            Vector3f targetPos = target.getComponent(LocationComponent.class).getWorldPosition();
            Set<EntityRef> targets = enemyManager.getEnemiesInRange(targetPos, targeterComponent.getSplashRange());
            event.addToList(targets);
            inWorldRenderer.shootBulletTowards(
                    target,
                    locationComponent.getWorldPosition(),
                    new SplashBulletComponent());
        }
        targeterComponent.setLastTarget(target);
    }
}
