// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.towers.targeters;

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

/**
 * Selects a single target enemy and then targets all enemies within a small range of that enemy.
 *
 * @see SplashTargeterComponent
 * @see TowerManager
 */
@RegisterSystem
public class SplashTargeterSystem extends BaseTargeterSystem {

    @In
    private EnemyManager enemyManager;
    @In
    private InWorldRenderer inWorldRenderer;

    /**
     * Determine which enemies should be attacked. Called against the targeter entity.
     * <p>
     * Filters on {@link LocationComponent} and {@link SplashTargeterComponent}
     *
     * @see SelectEnemiesEvent
     */
    @ReceiveEvent
    public void onSelectEnemies(SelectEnemiesEvent event, EntityRef entity, LocationComponent locationComponent,
                                SplashTargeterComponent targeterComponent) {
        EntityRef target = getTarget(locationComponent.getWorldPosition(), targeterComponent, enemyManager);

        if (target.exists()) {
            LocationComponent targetLocation = target.getComponent(LocationComponent.class);
            event.addToList(enemyManager.getEnemiesInRange(targetLocation.getWorldPosition(),
                    targeterComponent.splashRange));

            inWorldRenderer.shootBulletTowards(
                    target,
                    locationComponent.getWorldPosition(),
                    new SplashBulletComponent(targeterComponent.splashRange));
        }

        targeterComponent.lastTarget = target;
    }
}
