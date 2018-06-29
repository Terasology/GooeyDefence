/*
 * Copyright 2017 MovingBlocks
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
package org.terasology.gooeyDefence;

import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.gooeyDefence.components.towers.TowerComponent;
import org.terasology.gooeyDefence.events.combat.ApplyEffectEvent;
import org.terasology.gooeyDefence.events.combat.RemoveEffectEvent;
import org.terasology.gooeyDefence.events.combat.SelectEnemiesEvent;
import org.terasology.gooeyDefence.events.tower.TowerChangedEvent;
import org.terasology.gooeyDefence.events.tower.TowerCreatedEvent;
import org.terasology.gooeyDefence.events.tower.TowerDestroyedEvent;
import org.terasology.gooeyDefence.towerBlocks.EffectCount;
import org.terasology.gooeyDefence.towerBlocks.EffectDuration;
import org.terasology.gooeyDefence.towerBlocks.base.TowerCore;
import org.terasology.gooeyDefence.towerBlocks.base.TowerEffector;
import org.terasology.gooeyDefence.towerBlocks.base.TowerTargeter;
import org.terasology.logic.delay.DelayManager;
import org.terasology.logic.delay.PeriodicActionTriggeredEvent;
import org.terasology.registry.In;

import java.util.HashSet;
import java.util.Set;

@RegisterSystem
public class TowerManager extends BaseComponentSystem {
    private static final Logger logger = LoggerFactory.getLogger(TowerManager.class);

    @In
    private DelayManager delayManager;
    @In
    private EntityManager entityManager;
    private Set<EntityRef> towerEntities = new HashSet<>();

    /**
     * Creates the periodic event id for the targeter on a tower
     *
     * @param tower    The tower the targeter is on
     * @param targeter The targeter the event is sending for
     * @return The id for that periodic action event.
     * @see PeriodicActionTriggeredEvent
     */
    private static String buildEventId(EntityRef tower, EntityRef targeter) {
        return "towerDefence" + tower.getId() + "|" + targeter.getId();
    }

    /**
     * Checks that the periodic event is intended for the given tower.
     *
     * @param tower   The tower to check for
     * @param eventId The id of the periodic event
     * @return True if the event belongs to the tower
     * @see PeriodicActionTriggeredEvent
     */
    private static boolean isEventIdCorrect(EntityRef tower, String eventId) {
        return eventId.startsWith("towerDefence" + tower.getId());
    }

    /**
     * Gets the ID of the targeter from the periodic event id.
     *
     * @param eventId The id of the periodic event
     * @return The ID of the targeter entity ref
     */
    private static long getTargeterId(String eventId) {
        String id = eventId.substring(eventId.indexOf('|') + 1);
        return Long.parseLong(id);
    }

    /**
     * Called when a tower is created.
     * Adds the tower to the list and sets the periodic actions for it's attacks
     * <p>
     * Filters on {@link TowerComponent}
     *
     * @see TowerCreatedEvent
     */
    @ReceiveEvent
    public void onTowerCreated(TowerCreatedEvent event, EntityRef towerEntity, TowerComponent towerComponent) {
        towerEntities.add(towerEntity);
        for (EntityRef targeter : towerComponent.targeter) {
            TowerTargeter targeterComponent = DefenceField.getComponentExtending(targeter, TowerTargeter.class);
            delayManager.addPeriodicAction(towerEntity,
                    buildEventId(towerEntity, targeter),
                    targeterComponent.getAttackSpeed(),
                    targeterComponent.getAttackSpeed());
        }
    }

    /**
     * Called when a block is added to a tower.
     * Cancels the old periodic actions and schedules new ones.
     * <p>
     * Filters on {@link TowerComponent}
     *
     * @see TowerChangedEvent
     */
    @ReceiveEvent
    public void onTowerChanged(TowerChangedEvent event, EntityRef towerEntity, TowerComponent towerComponent) {
        for (EntityRef newBlock : event.getChangedBlocks()) {
            try {
                TowerTargeter targeter = DefenceField.getComponentExtending(newBlock, TowerTargeter.class);
                delayManager.addPeriodicAction(towerEntity,
                        buildEventId(towerEntity, newBlock),
                        targeter.getAttackSpeed(),
                        targeter.getAttackSpeed());
            } catch (IllegalArgumentException ignored) {
                /* The block is not a Targeter so we ignore it. */
            }
        }
    }

    /**
     * Called when a tower is destroyed.
     * Removes all the periodic actions and the tower from the store.
     * <p>
     * Filters on {@link TowerComponent}
     */
    @ReceiveEvent
    public void onTowerDestroyed(TowerDestroyedEvent event, EntityRef towerEntity, TowerComponent towerComponent) {
        for (EntityRef targeter : towerComponent.targeter) {
            delayManager.cancelPeriodicAction(towerEntity, buildEventId(towerEntity, targeter));
        }
        towerEntities.remove(towerEntity);
    }

    /**
     * Called every attack cycle per targeter.
     * Checks if the tower can fire, and if so, fires that targeter.
     *
     * @param event     The periodic event
     * @param entity    The tower entity
     * @param component The TowerComponent of the entity
     */
    @ReceiveEvent
    public void onPeriodicActionTriggered(PeriodicActionTriggeredEvent event, EntityRef entity, TowerComponent component) {
        if (isEventIdCorrect(entity, event.getActionId())) {
            int corePower = getTotalCorePower(component);
            int totalDrain = getEffectorDrain(component) + getTargeterDrain(component);
            if (corePower >= totalDrain) {
                EntityRef targeter = entityManager.getEntity(getTargeterId(event.getActionId()));
                //TowerTargeter targeterComponent = DefenceField.getComponentExtending(targeter, TowerTargeter.class);
                handleTowerShooting(component, targeter);
            }
        }
    }

    /**
     * Get the drain caused by all the targeters on a tower
     *
     * @param towerComponent The TowerComponent of the tower entity
     * @return The total drain. Zero if the tower has no targeters
     */
    private int getTargeterDrain(TowerComponent towerComponent) {
        int drain = 0;
        for (EntityRef emitterEntity : towerComponent.targeter) {
            TowerTargeter emitter = DefenceField.getComponentExtending(emitterEntity, TowerTargeter.class);
            drain += emitter.getDrain();
        }
        return drain;
    }

    /**
     * Get the drain caused by all the effector on a tower
     *
     * @param towerComponent The TowerComponent of the tower entity
     * @return The total drain. Zero if the tower has no effector
     */
    private int getEffectorDrain(TowerComponent towerComponent) {
        int drain = 0;
        for (EntityRef effectorEntity : towerComponent.effector) {
            TowerEffector effector = DefenceField.getComponentExtending(effectorEntity, TowerEffector.class);
            drain += effector.getDrain();
        }
        return drain;
    }

    /**
     * Get the power generated by all the cores on a tower
     *
     * @param towerComponent The TowerComponent of the tower entity
     * @return The total power. Zero if the tower has no cores
     */
    private int getTotalCorePower(TowerComponent towerComponent) {
        int power = 0;
        for (EntityRef coreEntity : towerComponent.cores) {
            TowerCore core = DefenceField.getComponentExtending(coreEntity, TowerCore.class);
            power += core.getPower();
        }
        return power;
    }

    /**
     * Handles the steps involved in making a targeter shoot.
     *
     * @param towerComponent The TowerComponent of the tower entity shooting.
     * @param targeter       The targeter that's shooting
     */
    private void handleTowerShooting(TowerComponent towerComponent, EntityRef targeter) {
        Set<EntityRef> currentTargets = getTargetedEnemies(targeter);
        TowerTargeter towerTargeter = DefenceField.getComponentExtending(targeter, TowerTargeter.class);

        applyEffectsToTargets(towerComponent.effector, currentTargets, towerTargeter);

        towerTargeter.setLastTargets(currentTargets);
    }

    /**
     * Calls on the targeter to obtain the enemies it's targeting.
     *
     * @param targeter The targeter to call on
     * @return All entities targeted by that targeter.
     * @see TowerTargeter
     */
    private Set<EntityRef> getTargetedEnemies(EntityRef targeter) {
        SelectEnemiesEvent shootEvent = new SelectEnemiesEvent();
        targeter.send(shootEvent);
        return shootEvent.getTargets();
    }

    /**
     * Applies all the effects on a tower to the targeted enemies
     *
     * @param effectors      The effectors on the tower
     * @param currentTargets The current targets of the tower
     * @param towerTargeter  The targeter shooting
     * @see TowerEffector
     */
    private void applyEffectsToTargets(Set<EntityRef> effectors, Set<EntityRef> currentTargets, TowerTargeter towerTargeter) {

        Set<EntityRef> newTargets = Sets.difference(currentTargets, towerTargeter.getLastTargets());
        Set<EntityRef> oldTargets = Sets.difference(towerTargeter.getLastTargets(), currentTargets);

        for (EntityRef effector : effectors) {
            applyEffect(effector, currentTargets, newTargets, towerTargeter.getMultiplier());
            endEffects(effector, oldTargets, towerTargeter.getMultiplier());
        }
    }

    /**
     * Calls on the effectors to apply effects to the targets
     *
     * @param effector       The effectors to check through
     * @param currentTargets All targets this attack round.
     * @param newTargets     The enemies that have been newly targeted this round
     * @param multiplier     The effect multiplier to apply to the event
     */
    private void applyEffect(EntityRef effector, Set<EntityRef> currentTargets, Set<EntityRef> newTargets, float multiplier) {
        TowerEffector effectorComponent = DefenceField.getComponentExtending(effector, TowerEffector.class);
        Set<EntityRef> targets;
        switch (effectorComponent.getEffectCount()) {
            case PER_SHOT:
                targets = currentTargets;
                break;
            case CONTINUOUS:
                targets = newTargets;
                break;
            default:
                throw new EnumConstantNotPresentException(EffectCount.class, effectorComponent.getEffectCount().toString());
        }
        for (EntityRef entity : targets) {
            ApplyEffectEvent effectEvent = new ApplyEffectEvent(entity, multiplier);
            effector.send(effectEvent);
        }
    }

    /**
     * Calls on each effector to end the effect on a target, where applicable.
     *
     * @param effector   The effectors to check through
     * @param multiplier The effect multiplier to apply to the event
     * @param oldTargets The targets to remove the effects from
     */
    private void endEffects(EntityRef effector, Set<EntityRef> oldTargets, float multiplier) {
        TowerEffector effectorComponent = DefenceField.getComponentExtending(effector, TowerEffector.class);
        switch (effectorComponent.getEffectDuration()) {
            case INSTANT:
                break;
            case PERMANENT:
                break;
            case LASTING:
                for (EntityRef entity : oldTargets) {
                    RemoveEffectEvent effectEvent = new RemoveEffectEvent(entity, multiplier);
                    effector.send(effectEvent);
                }
                break;
            default:
                throw new EnumConstantNotPresentException(EffectDuration.class, effectorComponent.getEffectCount().toString());
        }
    }
}
