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

import java.util.Set;

/**
 * Handles selecting the enemies for the {@link AoeTargeterComponent}.
 * <p>
 * Selects all enemies within range of the tower
 *
 * @see TowerManager
 * @see AoeTargeterComponent
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
    public void onSelectEnemies(SelectEnemiesEvent event, EntityRef entity, LocationComponent locationComponent,
                                AoeTargeterComponent targeterComponent) {
        Set<EntityRef> targets = enemyManager.getEnemiesInRange(locationComponent.getWorldPosition(),
                targeterComponent.range);
        event.addToList(targets);
        if (!targets.isEmpty()) {
            inWorldRenderer.displayExpandingSphere(locationComponent.getWorldPosition(),
                    (float) targeterComponent.attackSpeed / 1000, targeterComponent.range * 2 + 1);
        }
    }
}
