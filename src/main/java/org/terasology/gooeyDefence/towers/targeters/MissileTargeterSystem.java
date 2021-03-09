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

import org.joml.Vector3f;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.registry.In;
import org.terasology.gooeyDefence.EnemyManager;
import org.terasology.gooeyDefence.towers.TowerManager;
import org.terasology.gooeyDefence.towers.events.SelectEnemiesEvent;
import org.terasology.gooeyDefence.visuals.InWorldRenderer;
import org.terasology.gooeyDefence.visuals.components.SplashBulletComponent;

import java.util.Set;

/**
 * Targets in an AOE around a distant enemy.
 * This tower cannot target nearby enemies, same as {@link SniperTargeterComponent}.
 *
 * @see MissileTargeterComponent
 * @see TowerManager
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

        Vector3f worldPos = locationComponent.getWorldPosition(new Vector3f());
        EntityRef target = getTarget(worldPos, targeterComponent);

        if (target.exists()) {
            Vector3f targetPos = target.getComponent(LocationComponent.class).getWorldPosition(new Vector3f());
            Set<EntityRef> targets = enemyManager.getEnemiesInRange(targetPos, targeterComponent.splashRange);
            event.addToList(targets);
            inWorldRenderer.shootBulletTowards(
                    target,
                    locationComponent.getWorldPosition(new Vector3f()),
                    new SplashBulletComponent(targeterComponent.splashRange));
        }
        targeterComponent.lastTarget = target;
    }
}
