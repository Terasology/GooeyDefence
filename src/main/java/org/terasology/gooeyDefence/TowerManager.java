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
    @In
    private DelayManager delayManager;
    @In
    private EntityManager entityManager;
    private Set<EntityRef> towerEntities = new HashSet<>();

    @ReceiveEvent
    public void onTowerCreated(TowerCreatedEvent event, EntityRef entity, TowerComponent component) {
        towerEntities.add(entity);
        delayManager.addPeriodicAction(entity, "towerUpdate" + entity.getId(), 100, 100);
    }

    @ReceiveEvent
    public void onTowerDestroyed(TowerDestroyedEvent event, EntityRef entity, TowerComponent component) {
        towerEntities.remove(entity);
        delayManager.cancelPeriodicAction(entity, "towerUpdate" + entity.getId());
    }

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

    private void handleTowerShooting(TowerComponent towerComponent) {
        for (long emitterID : towerComponent.emitters) {
            EntityRef emitter = entityManager.getEntity(emitterID);
            DoSelectEnemies shootEvent = new DoSelectEnemies();
            emitter.send(shootEvent);
        }
    }

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

    private <Y> Y getComponentExtending(EntityRef entity, Class<Y> superClass) {
        for (Component component : entity.iterateComponents()) {
            if (component.getClass().isInstance(superClass)) {
                return superClass.cast(component);
            }
        }
        return null;
    }

}
