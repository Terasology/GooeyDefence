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

/**
 * Targets a single enemy within range.
 *
 * @see SingleTargeterComponent
 * @see TowerManager
 */
@RegisterSystem
public class SingleTargeterSystem extends BaseTargeterSystem {

    @In
    protected EnemyManager enemyManager;
    @In
    private InWorldRenderer inWorldRenderer;

    /**
     * Determine which enemies should be attacked. Called against the targeter entity.
     * <p>
     * Filters on {@link LocationComponent} and {@link SingleTargeterComponent}
     *
     * @see SelectEnemiesEvent
     */
    @ReceiveEvent
    public void onDoSelectEnemies(SelectEnemiesEvent event, EntityRef entity, LocationComponent locationComponent,
                                  SingleTargeterComponent targeterComponent) {
        EntityRef target = getTarget(locationComponent.getWorldPosition(), targeterComponent, enemyManager);

        if (target.exists()) {
            event.addToList(target);
            inWorldRenderer.shootBulletTowards(
                    target,
                    locationComponent.getWorldPosition());
        }
        targeterComponent.lastTarget = target;

    }


}
