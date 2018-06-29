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
import java.util.SortedSet;
import java.util.TreeSet;

@RegisterSystem
public class TowerManager extends BaseComponentSystem {
    private static final Logger logger = LoggerFactory.getLogger(TowerManager.class);

    @In
    private DelayManager delayManager;
    @In
    private EntityManager entityManager;
    private Set<EntityRef> towerEntities = new HashSet<>();

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
        Set<Long> attackSpeeds = getAttackSpeeds(towerComponent.cores);
        towerComponent.attackCount = attackSpeeds.size();
        int i = 0;
        for (Long speed : attackSpeeds) {
            delayManager.addPeriodicAction(towerEntity, buildEventId(towerEntity, i), speed, speed);
            i++;
        }
    }

    /**
     * Called when a tower is changed.
     * Cancels the old periodic actions and schedules new ones.
     * <p>
     * Filters on {@link TowerComponent}
     *
     * @see TowerChangedEvent
     */
    @ReceiveEvent
    public void onTowerChanged(TowerChangedEvent event, EntityRef towerEntity, TowerComponent towerComponent) {
        for (int i = 0; i < towerComponent.attackCount; i++) {
            delayManager.cancelPeriodicAction(towerEntity, buildEventId(towerEntity, i));
        }
        Set<Long> attackSpeeds = getAttackSpeeds(towerComponent.cores);
        towerComponent.attackCount = attackSpeeds.size();
        int i = 0;
        for (Long speed : attackSpeeds) {
            delayManager.addPeriodicAction(towerEntity, buildEventId(towerEntity, i), speed, speed);
            i++;
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
        towerEntities.remove(towerEntity);
        for (int i = 0; i < towerComponent.attackCount; i++) {
            delayManager.cancelPeriodicAction(towerEntity, buildEventId(towerEntity, i));
        }
    }

    /**
     * Get the attack rates of this tower.
     *
     * @param cores The cores of the tower
     * @return The attack speeds of the tower
     */
    private Set<Long> getAttackSpeeds(Set<EntityRef> cores) {
        /* Collect the attack rates of all the cores */
        SortedSet<Long> attackRates = new TreeSet<>();
        for (EntityRef coreEntity : cores) {
            TowerCore towerCore = DefenceField.getComponentExtending(coreEntity, TowerCore.class);
            attackRates.add(towerCore.getAttackSpeed());
        }
        return filterFactors(attackRates);
    }

    /**
     * Removes any elements that are a factor of another element.
     * That is, no element in the set will divide another element in the set.
     *
     * @param input The sorted set to filter
     * @return A set with no elements as factors.
     */
    private Set<Long> filterFactors(SortedSet<Long> input) {
        /* Only keep those that do not divide each other */
        Set<Long> results = new HashSet<>();
        while (!input.isEmpty()) {
            Long first = input.first();
            input.remove(first);
            boolean isDivisible = false;
            for (Long attackRate : results) {
                if (first % attackRate == 0) {
                    isDivisible = true;
                    break;
                }
            }
            if (!isDivisible) {
                results.add(first);
            }
        }
        return results;
    }

    /**
     * Called every attack cycle. Checks if a tower can fire.
     *
     * @param event     The periodic event
     * @param entity    The tower entity
     * @param component The TowerComponent of the entity
     */
    @ReceiveEvent
    public void onPeriodicActionTriggered(PeriodicActionTriggeredEvent event, EntityRef entity, TowerComponent component) {
        if (event.getActionId().startsWith("towerDefence" + entity.getId())) {
            int corePower = getTotalCorePower(component);
            int totalDrain = getEffectorDrain(component) + getEmitterDrain(component);
            if (corePower >= totalDrain) {
                handleTowerShooting(component);
            }
        }
    }


    /**
     * Get the drain caused by all the targeters on a tower
     *
     * @param towerComponent The TowerComponent of the tower entity
     * @return The total drain. Zero if the tower has no targeters
     */
    private int getEmitterDrain(TowerComponent towerComponent) {
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
     * Handles the steps involved in making the tower shoot.
     *
     * @param towerComponent The TowerComponent of the tower entity shooting.
     */
    private void handleTowerShooting(TowerComponent towerComponent) {
        Set<EntityRef> currentTargets = getTargetedEnemies(towerComponent.targeter);
        applyEffectsToTargets(towerComponent.effector, currentTargets, towerComponent.lastTargets);
        towerComponent.lastTargets = currentTargets;
    }

    /**
     * Runs all the targeters in the tower and gets the targeted enemies.
     *
     * @param targeters The targeters to run through
     * @return All entities targeted by the tower.
     * @see TowerTargeter
     */
    private Set<EntityRef> getTargetedEnemies(Set<EntityRef> targeters) {
        /* Run emitter event */
        SelectEnemiesEvent shootEvent = new SelectEnemiesEvent();
        for (EntityRef emitter : targeters) {
            emitter.send(shootEvent);
        }
        return shootEvent.getTargets();
    }

    /**
     * Applies all the effects on a tower to the targeted enemies
     *
     * @param effectors      The effectors on the tower
     * @param currentTargets The current targets of the tower
     * @param lastTargets    The enemies targeted last attack
     * @see TowerEffector
     */
    private void applyEffectsToTargets(Set<EntityRef> effectors, Set<EntityRef> currentTargets, Set<EntityRef> lastTargets) {
        Set<EntityRef> newTargets = Sets.difference(currentTargets, lastTargets);
        Set<EntityRef> oldTargets = Sets.difference(lastTargets, currentTargets);


        for (EntityRef effector : effectors) {
            applyEffect(effector, currentTargets, newTargets);
            endEffects(effector, oldTargets);
        }
    }

    /**
     * Calls on the effectors to apply effects to the targets
     *
     * @param effector       The effectors to check through
     * @param currentTargets All targets this attack round.
     * @param newTargets     The enemies that have been newly targeted this round
     */
    private void applyEffect(EntityRef effector, Set<EntityRef> currentTargets, Set<EntityRef> newTargets) {
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
            ApplyEffectEvent effectEvent = new ApplyEffectEvent(entity, 1, 1);
            effector.send(effectEvent);
        }
    }

    /**
     * Calls on each effector to end the effect on a target, where applicable.
     *
     * @param effector   The effectors to check through
     * @param oldTargets The targets to remove the effects from
     */
    private void endEffects(EntityRef effector, Set<EntityRef> oldTargets) {
        TowerEffector effectorComponent = DefenceField.getComponentExtending(effector, TowerEffector.class);
        switch (effectorComponent.getEffectDuration()) {
            case INSTANT:
                break;
            case PERMANENT:
                break;
            case LASTING:
                for (EntityRef entity : oldTargets) {
                    RemoveEffectEvent effectEvent = new RemoveEffectEvent(entity);
                    effector.send(effectEvent);
                }
                break;
            default:
                throw new EnumConstantNotPresentException(EffectDuration.class, effectorComponent.getEffectCount().toString());
        }
    }

    private static String buildEventId(EntityRef entity, int index) {
        return "towerDefence" + entity.getId() + "|" + index;
    }
}
