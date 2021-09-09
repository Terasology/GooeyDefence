// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.towers.effectors;

import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.delay.DelayManager;
import org.terasology.engine.logic.delay.DelayedActionTriggeredEvent;
import org.terasology.engine.logic.delay.PeriodicActionTriggeredEvent;
import org.terasology.engine.registry.In;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.gooeyDefence.DefenceField;
import org.terasology.gooeyDefence.DefenceUris;
import org.terasology.gooeyDefence.components.GooeyComponent;
import org.terasology.gooeyDefence.health.events.DamageEntityEvent;
import org.terasology.gooeyDefence.towers.TowerManager;
import org.terasology.gooeyDefence.towers.events.ApplyEffectEvent;
import org.terasology.gooeyDefence.visuals.InWorldRenderer;

/**
 * Deals an initial damage, then damage over time to a target.
 * <p>
 * Multiple poison effects cannot be stacked from the same effector,
 * however effects from different poison effectors can stack
 *
 * @see PoisonEffectorComponent
 * @see TowerManager
 */
@RegisterSystem
public class PoisonEffectorSystem extends BaseComponentSystem {
    /**
     * How often the damage over time will be dealt
     * given in milliseconds
     */
    private static final int POISON_RATE = 200;
    /**
     * The id to use when registering the periodic event
     */
    private static final String APPLY_POISON_ID = "applyPoisonDamage";
    /**
     * The id to use when registering the end periodic event
     */
    private static final String END_POISON_ID = "endPoisonDamage";

    @In
    private DelayManager delayManager;
    @In
    private EntityManager entityManager;
    @In
    private InWorldRenderer inWorldRenderer;

    /**
     * Applies the effect to the target
     * <p>
     * Filters on {@link PoisonEffectorComponent}
     * Sent against the effector
     *
     * @see ApplyEffectEvent
     */
    @ReceiveEvent
    public void onApplyEffect(ApplyEffectEvent event, EntityRef entity, PoisonEffectorComponent effectorComponent) {
        EntityRef target = event.getTarget();
        target.send(new DamageEntityEvent(effectorComponent.damage));

        String endId = buildEventID(END_POISON_ID, entity);
        String applyId = buildEventID(APPLY_POISON_ID, entity);

        if (delayManager.hasDelayedAction(target, endId)) {
            delayManager.cancelDelayedAction(target, endId);
            delayManager.addDelayedAction(target, endId, effectorComponent.poisonDuration);
        } else {
            inWorldRenderer.addParticleEffect(target, DefenceUris.POISON_PARTICLES);
            delayManager.addPeriodicAction(target, applyId, POISON_RATE, POISON_RATE);
            delayManager.addDelayedAction(target, endId, effectorComponent.poisonDuration);
        }
    }

    /**
     * Deals a unit of poison damage to the enemy.
     * <p>
     * Filters on {@link GooeyComponent}
     * Called against the poisoned enemy
     *
     * @see PeriodicActionTriggeredEvent
     */
    @ReceiveEvent
    public void onPeriodicActionTriggered(PeriodicActionTriggeredEvent event, EntityRef entity, GooeyComponent enemyComponent) {
        if (DefenceField.fieldActivated && isApplyEvent(event)) {

            EntityRef effector = getEffectorEntity(event.getActionId());
            PoisonEffectorComponent effectorComponent = effector.getComponent(PoisonEffectorComponent.class);
            entity.send(new DamageEntityEvent(effectorComponent.poisonDamage));
        }
    }

    /**
     * Ends the poison effect for an enemy.
     * <p>
     * Filters on {@link GooeyComponent}
     * Called against the poisoned enemy
     *
     * @see DelayedActionTriggeredEvent
     */
    @ReceiveEvent
    public void onDelayedActionTriggered(DelayedActionTriggeredEvent event, EntityRef entity, GooeyComponent enemyComponent) {
        if (isEndEvent(event)) {
            EntityRef effector = getEffectorEntity(event.getActionId());
            delayManager.cancelPeriodicAction(entity, buildEventID(APPLY_POISON_ID, effector));
            inWorldRenderer.removeParticleEffect(entity, DefenceUris.POISON_PARTICLES);
        }
    }

    /**
     * Creates the event ID given the base and the entity to encode
     *
     * @param baseId   The base ID string
     * @param effector The entity id to encode
     * @return The ID with the entity encoded
     */
    private String buildEventID(String baseId, EntityRef effector) {
        return baseId + "|" + effector.getId();
    }

    /**
     * Checks if the event has the correct ID for an apply poison event
     *
     * @param event The event to check
     * @return True if the event ID matches, false otherwise.
     */
    private boolean isApplyEvent(PeriodicActionTriggeredEvent event) {
        return event.getActionId().startsWith(APPLY_POISON_ID);
    }

    /**
     * Checks if the event has the correct ID for an end poison event
     *
     * @param event The event to check
     * @return True if the event ID matches, false otherwise.
     */
    private boolean isEndEvent(DelayedActionTriggeredEvent event) {
        return event.getActionId().startsWith(END_POISON_ID);
    }

    /**
     * Gets the entity encoded within the event ID
     *
     * @param eventID The id to extract from
     * @return The entity encoded.
     */
    private EntityRef getEffectorEntity(String eventID) {
        String id = eventID.substring(eventID.indexOf("|") + 1);
        return entityManager.getEntity(Long.parseLong(id));
    }
}
