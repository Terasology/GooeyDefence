// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.towers.effectors;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.delay.DelayManager;
import org.terasology.engine.logic.delay.DelayedActionTriggeredEvent;
import org.terasology.engine.logic.delay.PeriodicActionTriggeredEvent;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.registry.In;
import org.terasology.engine.utilities.random.FastRandom;
import org.terasology.engine.utilities.random.Random;
import org.terasology.gooeyDefence.DefenceUris;
import org.terasology.gooeyDefence.EnemyManager;
import org.terasology.gooeyDefence.components.GooeyComponent;
import org.terasology.gooeyDefence.health.events.DamageEntityEvent;
import org.terasology.gooeyDefence.towers.TowerManager;
import org.terasology.gooeyDefence.towers.events.ApplyEffectEvent;
import org.terasology.gooeyDefence.visuals.InWorldRenderer;
import org.terasology.math.geom.Vector3f;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Applies the fire effect to the targeted enemies.
 * <p>
 * Fire applies a damage over time, with a chance to have the effect spread to nearby enemies. After a short duration,
 * the burning ends but the enemy can be re-ignited by other enemies.
 *
 * @see FireEffectorComponent
 * @see TowerManager
 */
@RegisterSystem
public class FireEffectorSystem extends BaseComponentSystem {
    /**
     * The id of the event to initiate the burn
     */
    private static final String APPLY_BURN_ID = "applyBurn";
    /**
     * The id of the event to end the burn
     */
    private static final String END_BURN_ID = "endBurn";
    /**
     * How often the fire should have a chance to spread. Given in milliseconds.
     */
    private static final int BURN_RATE = 500;
    /**
     * How close an enemy has to be before it can be ignited. Given in blocks
     */
    private static final float BURN_RANGE = 1;
    /**
     * The chance an enemy has of being ignited by a nearby burning enemy.
     */
    private static final float BURN_SPREAD_CHANCE = 0.4f;
    private final Random random = new FastRandom();
    /**
     * All enemies currently on fire.
     */
    private Set<EntityRef> burningEnemies = new HashSet<>();
    @In
    private EnemyManager enemyManager;
    @In
    private DelayManager delayManager;
    @In
    private InWorldRenderer inWorldRenderer;

    /**
     * Applies the initial fire effect to an entity
     * <p>
     * Filters on {@link FireEffectorComponent} Sent against the effector
     *
     * @see ApplyEffectEvent
     */
    @ReceiveEvent
    public void onApplyEffect(ApplyEffectEvent event, EntityRef entity, FireEffectorComponent effectorComponent) {
        burningEnemies.add(event.getTarget());
        if (!delayManager.hasPeriodicAction(entity, APPLY_BURN_ID)) {
            delayManager.addPeriodicAction(entity, APPLY_BURN_ID, BURN_RATE, BURN_RATE);
        }
        inWorldRenderer.addParticleEffect(event.getTarget(), DefenceUris.FIRE_PARTICLES);
        delayManager.addDelayedAction(event.getTarget(), END_BURN_ID, effectorComponent.fireDuration);
    }

    /**
     * Processes all burning enemies
     * <p>
     * Filters on {@link FireEffectorComponent} Sent against the effector
     *
     * @see PeriodicActionTriggeredEvent
     */
    @ReceiveEvent
    public void onPeriodicActionTriggered(PeriodicActionTriggeredEvent event, EntityRef entity,
                                          FireEffectorComponent effectorComponent) {
        Set<EntityRef> newEnemies = new HashSet<>();
        for (EntityRef enemy : burningEnemies) {
            enemy.send(new DamageEntityEvent(effectorComponent.damage));
            if (enemy.exists()) {
                newEnemies.addAll(spreadFire(enemy));
            }
        }

        burningEnemies.addAll(newEnemies);
        burningEnemies = burningEnemies.stream()
                .filter(EntityRef::exists)
                .collect(Collectors.toSet());

        for (EntityRef newEnemy : newEnemies) {
            delayManager.addDelayedAction(newEnemy, END_BURN_ID, effectorComponent.fireDuration);
            inWorldRenderer.addParticleEffect(newEnemy, DefenceUris.FIRE_PARTICLES);
        }

        if (burningEnemies.isEmpty()) {
            delayManager.cancelPeriodicAction(entity, APPLY_BURN_ID);
        }
    }


    /**
     * Stops an enemy from burning
     * <p>
     * Filters on {@link GooeyComponent} Sent against the burnt enemy
     *
     * @see DelayedActionTriggeredEvent
     */
    @ReceiveEvent(components = GooeyComponent.class)
    public void onDelayedActionTriggered(DelayedActionTriggeredEvent event, EntityRef entity) {
        if (event.getActionId().equals(END_BURN_ID)) {
            burningEnemies.remove(entity);
            inWorldRenderer.removeParticleEffect(entity, DefenceUris.FIRE_PARTICLES);
        }
    }

    /**
     * Gets all the enemies to spread the fire too. Does not return any enemies already on fire.
     *
     * @param source The enemy spreading the fire
     * @return All enemies to spread the fire too.
     */
    private Set<EntityRef> spreadFire(EntityRef source) {
        Vector3f sourcePos = source.getComponent(LocationComponent.class).getWorldPosition();
        return enemyManager.getEnemiesInRange(sourcePos, BURN_RANGE)
                .stream()
                .filter(enemy -> !burningEnemies.contains(enemy) && canBurn())
                .collect(Collectors.toSet());
    }

    /**
     * Uses the burn chance to check if an enemy should be ignited or not.
     *
     * @return True if the enemy should be ignited, false otherwise
     */
    private boolean canBurn() {
        return random.nextFloat() <= BURN_SPREAD_CHANCE;
    }
}
