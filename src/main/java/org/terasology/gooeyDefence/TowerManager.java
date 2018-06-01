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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.gooeyDefence.components.towers.TowerComponent;
import org.terasology.gooeyDefence.events.DoSelectEnemies;
import org.terasology.gooeyDefence.events.TowerCreatedEvent;
import org.terasology.gooeyDefence.events.TowerDestroyedEvent;
import org.terasology.gooeyDefence.towerBlocks.base.TowerCore;
import org.terasology.gooeyDefence.towerBlocks.base.TowerEffect;
import org.terasology.gooeyDefence.towerBlocks.base.TowerEmitter;
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
     * Adds a tower to the central store.
     *
     * @param event  The the addition event
     * @param entity The tower entity
     */
    @ReceiveEvent(components = {TowerComponent.class})
    public void onTowerCreated(TowerCreatedEvent event, EntityRef entity) {
        towerEntities.add(entity);
        delayManager.addPeriodicAction(entity, "towerUpdate" + entity.getId(), 100, 100);
    }

    /**
     * Removes a tower to the central store.
     *
     * @param event  The tower destroyed event
     * @param entity The tower entity to remove
     */
    @ReceiveEvent(components = {TowerComponent.class})
    public void onTowerDestroyed(TowerDestroyedEvent event, EntityRef entity) {
        towerEntities.remove(entity);
        delayManager.cancelPeriodicAction(entity, "towerUpdate" + entity.getId());
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
        if (event.getActionId().equals("towerUpdate" + entity.getId())) {
            int corePower = getTotalCorePower(component);
            int totalDrain = getEffectsDrain(component) + getEmitterDrain(component);
            if (corePower >= totalDrain) {
                handleTowerShooting(component);
            }
        }
    }

    /**
     * Handles the steps involved in making the tower shoot.
     *
     * @param towerComponent The TowerComponent of the tower entity shooting.
     */
    private void handleTowerShooting(TowerComponent towerComponent) {
        for (long emitterID : towerComponent.emitters) {
            EntityRef emitter = entityManager.getEntity(emitterID);
            DoSelectEnemies shootEvent = new DoSelectEnemies();
            emitter.send(shootEvent);
        }
    }

    /**
     * Get the drain caused by all the emitters on a tower
     *
     * @param towerComponent The TowerComponent of the tower entity
     * @return The total drain. Zero if the tower has no emitters
     */
    private int getEmitterDrain(TowerComponent towerComponent) {
        int drain = 0;
        for (long emitterID : towerComponent.emitters) {
            EntityRef emitterEntity = entityManager.getEntity(emitterID);
            TowerEmitter emitter = getComponentExtending(emitterEntity, TowerEmitter.class);
            if (emitter != null) {
                drain += emitter.getDrain();
            }
        }
        return drain;
    }

    /**
     * Get the drain caused by all the effects on a tower
     *
     * @param towerComponent The TowerComponent of the tower entity
     * @return The total drain. Zero if the tower has no effects
     */
    private int getEffectsDrain(TowerComponent towerComponent) {
        int drain = 0;
        for (long effectID : towerComponent.effects) {
            EntityRef effectEntity = entityManager.getEntity(effectID);
            TowerEffect effect = getComponentExtending(effectEntity, TowerEffect.class);
            if (effect != null) {
                drain += effect.getDrain();
            }
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
        for (long coreID : towerComponent.cores) {
            EntityRef coreEntity = entityManager.getEntity(coreID);
            TowerCore core = getComponentExtending(coreEntity, TowerCore.class);
            if (core != null) {
                power += core.getPower();
            }
        }
        return power;
    }

    /**
     * Helper method for getting a component given one of it's superclasses
     *
     * @param entity     The entity to search on
     * @param superClass The superclass of the component to filter for
     * @param <Y>        The type of the superclass
     * @return The component that extends the superclass
     */
    private <Y> Y getComponentExtending(EntityRef entity, Class<Y> superClass) {
        for (Component component : entity.iterateComponents()) {
            if (superClass.isInstance(component)) {
                return superClass.cast(component);
            }
        }
        return null;
    }

}
