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
import org.terasology.entitySystem.event.EventPriority;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.gooeyDefence.components.towers.TowerComponent;
import org.terasology.gooeyDefence.components.towers.TowerMultiBlockComponent;
import org.terasology.gooeyDefence.components.towers.base.TowerCoreComponent;
import org.terasology.gooeyDefence.components.towers.base.TowerEffectComponent;
import org.terasology.gooeyDefence.components.towers.base.TowerEmitterComponent;
import org.terasology.logic.characters.events.AttackEvent;
import org.terasology.logic.common.ActivateEvent;
import org.terasology.logic.health.DestroyEvent;
import org.terasology.logic.health.DoDestroyEvent;
import org.terasology.logic.health.EngineDamageTypes;
import org.terasology.math.Side;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.In;
import org.terasology.registry.Share;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.block.entity.placement.PlaceBlocks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Share(TowerBuildSystem.class)
@RegisterSystem
public class TowerBuildSystem extends BaseComponentSystem {

    private static final Logger logger = LoggerFactory.getLogger(TowerBuildSystem.class);
    private List<EntityRef> towerEntities = new ArrayList<>();
    @In
    private BlockEntityRegistry blockEntityRegistry;
    @In
    private EntityManager entityManager;

    /**
     * Make blocks destroy instantly
     */
    @ReceiveEvent(priority = EventPriority.PRIORITY_HIGH)
    public void onAttackEntity(AttackEvent event, EntityRef targetEntity) {
        targetEntity.send(new DestroyEvent(event.getInstigator(), event.getDirectCause(), EngineDamageTypes.PHYSICAL.get()));
    }

    /**
     * Test Event Handler
     *
     * @param event  The activate event
     * @param entity The entity
     */
    @ReceiveEvent
    public void onActivate(ActivateEvent event, EntityRef entity, TowerMultiBlockComponent component) {
        logger.info("");
    }

    /**
     * On a block being placed.
     *
     * @param event  The block placed event
     * @param entity The world entity placing the blocks
     */
    @ReceiveEvent
    public void onAddedBlocks(PlaceBlocks event, EntityRef entity) {
        for (Vector3i pos : event.getBlocks().keySet()) {
            EntityRef blockEntity = blockEntityRegistry.getEntityAt(pos);
            if (blockEntity.hasComponent(TowerMultiBlockComponent.class)) {
                handleTowerBlock(pos, blockEntity);
            }
        }
    }

    /**
     * Handles a tower block being placed
     *
     * @param pos         The position of the block being placed
     * @param blockEntity The entity of the block being placed
     */
    private void handleTowerBlock(Vector3i pos, EntityRef blockEntity) {
        /* Find all tower blocks nearby */
        List<EntityRef> attachedTowers = findAttachedTowers(pos);
        Set<Long> towers = new HashSet<>();
        attachedTowers.forEach(entityRef -> towers.add(entityRef.getComponent(TowerMultiBlockComponent.class).getTowerEntity()));
        switch (towers.size()) {
            /* No neighboring tower */
            case 0:
                addToTower(createNewTower(), blockEntity);
                break;
            /* One neighboring tower */
            case 1:
                addToTower(entityManager.getEntity(towers.iterator().next()), blockEntity);
                break;
            /* Multiple neighboring towers */
            default:
                addToTower(mergeTowers(towers), blockEntity);
                break;
        }
    }

    /**
     * Merge multiple towers into a new tower entity
     *
     * @param towers The towers to merge
     * @return The resulting tower entity
     */
    private EntityRef mergeTowers(Iterable<Long> towers) {
        EntityRef newTower = entityManager.create("GooeyDefence:TowerEntity");
        TowerComponent newComponent = newTower.getComponent(TowerComponent.class);
        for (long towerID : towers) {
            EntityRef towerEntity = entityManager.getEntity(towerID);
            TowerComponent component = towerEntity.getComponent(TowerComponent.class);
            newComponent.cores.addAll(component.cores);
            newComponent.effects.addAll(component.effects);
            newComponent.emitters.addAll(component.emitters);
            newComponent.plains.addAll(component.plains);
        }
        return newTower;
    }

    /**
     * Add a block to the given tower.
     *
     * @param blockEntity The block entity to add.
     * @param towerEntity The tower entity to add it to.
     */
    private void addToTower(EntityRef towerEntity, EntityRef blockEntity) {
        blockEntity.getComponent(TowerMultiBlockComponent.class).setTowerEntity(towerEntity.getId());
        TowerComponent towerComponent = towerEntity.getComponent(TowerComponent.class);

        /* Add it to the relevant list of blocks */
        for (Component component : blockEntity.iterateComponents()) {
            if (component instanceof TowerCoreComponent) {
                towerComponent.cores.add(blockEntity.getId());
                return;
            } else if (component instanceof TowerEffectComponent) {
                towerComponent.effects.add(blockEntity.getId());
                return;
            } else if (component instanceof TowerEmitterComponent) {
                towerComponent.emitters.add(blockEntity.getId());
                return;
            }
        }
        towerComponent.plains.add(blockEntity.getId());
    }

    /**
     * Create a new tower
     *
     * @return The new tower entity
     */
    private EntityRef createNewTower() {
        EntityRef towerEntity = entityManager.create("GooeyDefence:TowerEntity");
        towerEntities.add(towerEntity);
        return towerEntity;
    }

    /**
     * Scan around the position and find any tower blocks that ar part of an existing structure.
     *
     * @param position The position to scan around
     * @return Any block entities that are part of a tower.
     */
    private List<EntityRef> findAttachedTowers(Vector3i position) {
        List<EntityRef> results = new ArrayList<>();
        for (Side side : Side.values()) {
            Vector3i sidePos = new Vector3i(side.getVector3i()).add(position);
            EntityRef entity = blockEntityRegistry.getExistingEntityAt(sidePos);
            TowerMultiBlockComponent component = entity.getComponent(TowerMultiBlockComponent.class);
            if (component != null && component.getTowerEntity() != -1) {
                results.add(entity);
            }
        }
        return results;
    }

    @ReceiveEvent
    public void onDoDestroy(DoDestroyEvent event, EntityRef entity) {

    }
}
