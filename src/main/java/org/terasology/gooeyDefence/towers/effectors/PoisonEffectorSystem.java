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
package org.terasology.gooeyDefence.towers.effectors;

import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.gooeyDefence.DefenceField;
import org.terasology.gooeyDefence.components.GooeyComponent;
import org.terasology.gooeyDefence.health.events.DamageEntityEvent;
import org.terasology.gooeyDefence.towers.events.ApplyEffectEvent;
import org.terasology.gooeyDefence.visuals.InWorldRenderer;
import org.terasology.logic.delay.DelayManager;
import org.terasology.logic.delay.DelayedActionTriggeredEvent;
import org.terasology.logic.delay.PeriodicActionTriggeredEvent;
import org.terasology.registry.In;

/**
 * Deals an initial damage, then damage over time to a target\
 * <p>
 * Multiple poison effects cannot be stacked from the same effector.
 * However effects from different poison effectors can stack
 *
 * @see PoisonEffectorComponent
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
            inWorldRenderer.addParticleEffect(target, "GooeyDefence:PoisonParticleEffect");
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
        if (DefenceField.isFieldActivated() && isApplyEvent(event)) {

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
            inWorldRenderer.removeParticleEffect(entity, "GooeyDefence:PoisonParticleEffect");
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
