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
import org.terasology.gooeyDefence.components.towers.TowerMultiBlockComponent;
import org.terasology.gooeyDefence.events.OnFieldActivated;
import org.terasology.gooeyDefence.events.tower.TowerCreatedEvent;
import org.terasology.gooeyDefence.events.tower.TowerDestroyedEvent;
import org.terasology.gooeyDefence.towerBlocks.base.TowerCore;
import org.terasology.gooeyDefence.towerBlocks.base.TowerEffector;
import org.terasology.gooeyDefence.towerBlocks.base.TowerTargeter;
import org.terasology.logic.common.ActivateEvent;
import org.terasology.logic.health.DoDestroyEvent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.Side;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.In;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.block.items.OnBlockItemPlaced;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RegisterSystem
public class TowerBuildSystem extends BaseComponentSystem {
    private static final Logger logger = LoggerFactory.getLogger(TowerBuildSystem.class);

    @In
    private BlockEntityRegistry blockEntityRegistry;
    @In
    private EntityManager entityManager;

    /**
     * Test Event Handler
     *
     * @param event  The activate event
     * @param entity The entity
     */
    @ReceiveEvent
    public void onActivate(ActivateEvent event, EntityRef entity, TowerMultiBlockComponent component) {
        logger.info("Tower Entity: " + component.getTowerEntity());

    }

    @ReceiveEvent
    public void onFieldActivated(OnFieldActivated event, EntityRef savedDataEntity) {
        Iterable<EntityRef> blockEntities = entityManager.getEntitiesWith(TowerMultiBlockComponent.class);
        /* Clear entities */
        blockEntities.forEach(entity -> entity.getComponent(TowerMultiBlockComponent.class).setTowerEntity(-1));
        /* Rebuild towers */
        blockEntities.forEach(this::handleTowerBlock);
    }

    /**
     * On a block being placed.
     *
     * @param event  The block placed event
     * @param entity The world entity placing the blocks
     */
    @ReceiveEvent
    public void onAddedBlocks(OnBlockItemPlaced event, EntityRef entity) {
        if (event.getPlacedBlock().hasComponent(TowerMultiBlockComponent.class)) {
            handleTowerBlock(event.getPosition(), event.getPlacedBlock());
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
                /* We add the block to any tower. It will be correctly sorted in the next line */
                addToTower(entityManager.getEntity(towers.iterator().next()), blockEntity);
                mergeTowers(towers);
                break;
        }
    }

    private void handleTowerBlock(EntityRef entityRef) {
        if (entityRef.hasComponent(LocationComponent.class)) {
            handleTowerBlock(new Vector3i(entityRef.getComponent(LocationComponent.class).getWorldPosition()), entityRef);
        }
    }

    /**
     * Merge multiple towers into a single tower entity
     *
     * @param towers The towers to merge
     */
    private void mergeTowers(Set<Long> towers) {
        /* Select a tower to merge into */
        EntityRef newTower = entityManager.getEntity(towers.iterator().next());
        TowerComponent newComponent = newTower.getComponent(TowerComponent.class);
        towers.remove(newTower.getId());
        for (long towerID : towers) {
            /* Get all the blocks from the old tower */
            EntityRef towerEntity = entityManager.getEntity(towerID);
            Set<EntityRef> blocks = getAllFrom(towerEntity);
            /* Set them all to the new tower */
            blocks.forEach(entityRef -> entityRef.getComponent(TowerMultiBlockComponent.class).setTowerEntity(newTower.getId()));

            /* Store them into the new tower */
            TowerComponent component = towerEntity.getComponent(TowerComponent.class);
            newComponent.cores.addAll(component.cores);
            newComponent.effector.addAll(component.effector);
            newComponent.targeter.addAll(component.targeter);
            newComponent.plains.addAll(component.plains);

            /* Destroy the old tower entity */
            removeTower(towerEntity);
        }
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
            if (component instanceof TowerCore) {
                towerComponent.cores.add(blockEntity.getId());
                return;
            } else if (component instanceof TowerEffector) {
                towerComponent.effector.add(blockEntity.getId());
                return;
            } else if (component instanceof TowerTargeter) {
                towerComponent.targeter.add(blockEntity.getId());
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
        towerEntity.send(new TowerCreatedEvent());
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

    /**
     * Called when a block is destroyed.
     * Rebuilds the tower the destroyed block belonged to.
     *
     * @param event     The destroy event.
     * @param entity    The entity of the block being destroyed.
     * @param component Flag component to filter only for tower blocks.
     */
    @ReceiveEvent
    public void onDoDestroy(DoDestroyEvent event, EntityRef entity, TowerMultiBlockComponent component) {
        if (component.getTowerEntity() != -1) {
            long towerId = component.getTowerEntity();
            removeBlockFromTower(entityManager.getEntity(towerId), entity);
            rebuildTower(towerId);
        }
    }

    /**
     * Remove a block from a tower.
     *
     * @param tower The tower to remove from
     * @param block The block to remove.
     */
    private void removeBlockFromTower(EntityRef tower, EntityRef block) {
        TowerComponent component = tower.getComponent(TowerComponent.class);
        component.cores.remove(block.getId());
        component.targeter.remove(block.getId());
        component.effector.remove(block.getId());
        component.plains.remove(block.getId());
        block.getComponent(TowerMultiBlockComponent.class).setTowerEntity(-1);
    }

    /**
     * Removes a tower entity.
     * Sending the appropriate events.
     *
     * @param tower The tower to remove
     */
    private void removeTower(EntityRef tower) {
        tower.send(new TowerDestroyedEvent());
        tower.destroy();
    }

    /**
     * Rebuilds a given tower into one or multiple towers
     *
     * @param towerID The tower to rebuild
     */
    private void rebuildTower(long towerID) {
        Set<EntityRef> blocks = getAllFrom(entityManager.getEntity(towerID));
        /* Remove their references to a tower entity */
        blocks.forEach(entityRef -> entityRef.getComponent(TowerMultiBlockComponent.class).setTowerEntity(-1));

        removeTower(entityManager.getEntity(towerID));
        for (EntityRef block : blocks) {
            Vector3i pos = new Vector3i(block.getComponent(LocationComponent.class).getWorldPosition());
            handleTowerBlock(pos, block);
        }
    }

    /**
     * Returns all the blocks that comprise a given tower.
     *
     * @param tower The tower entity to retrieve from
     * @return All the entities in the tower.
     */
    private Set<EntityRef> getAllFrom(EntityRef tower) {
        TowerComponent component = tower.getComponent(TowerComponent.class);
        Set<Long> results = new HashSet<>();
        results.addAll(component.cores);
        results.addAll(component.effector);
        results.addAll(component.targeter);
        results.addAll(component.plains);

        /* Convert the Longs to Entity Refs */
        return results.stream().map(id -> entityManager.getEntity(id)).collect(Collectors.toSet());
    }

}
