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
package org.terasology.gooeyDefence.towers;

import com.google.common.collect.Sets;
import org.joml.RoundingMode;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.health.DoDestroyEvent;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.math.Side;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.BlockEntityRegistry;
import org.terasology.engine.world.block.items.OnBlockItemPlaced;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.gooeyDefence.DefenceField;
import org.terasology.gooeyDefence.DefenceUris;
import org.terasology.gooeyDefence.events.OnFieldActivated;
import org.terasology.gooeyDefence.towers.components.TowerComponent;
import org.terasology.gooeyDefence.towers.components.TowerCore;
import org.terasology.gooeyDefence.towers.components.TowerEffector;
import org.terasology.gooeyDefence.towers.components.TowerMultiBlockComponent;
import org.terasology.gooeyDefence.towers.components.TowerTargeter;
import org.terasology.gooeyDefence.towers.events.OnBlocksAdded;
import org.terasology.gooeyDefence.towers.events.TowerCreatedEvent;
import org.terasology.gooeyDefence.towers.events.TowerDestroyedEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * Handles the creation and destruction of towers
 */
@RegisterSystem
public class TowerBuildSystem extends BaseComponentSystem {

    @In
    private BlockEntityRegistry blockEntityRegistry;
    @In
    private EntityManager entityManager;


    /**
     * Called when the field is activated, either from a new game, loaded save or reset.
     *
     * @see OnFieldActivated
     */
    @ReceiveEvent
    public void onFieldActivated(OnFieldActivated event, EntityRef savedDataEntity) {
        Iterable<EntityRef> towerEntities = entityManager.getEntitiesWith(TowerComponent.class);
        towerEntities.forEach(EntityRef::destroy);

        Iterable<EntityRef> blockEntities = entityManager.getEntitiesWith(TowerMultiBlockComponent.class);
        /* Clear entities */
        blockEntities.forEach(entity -> entity.getComponent(TowerMultiBlockComponent.class).setTowerEntity(EntityRef.NULL));
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
     * Wrapper to allow easy iteration over a collection of entities.
     *
     * @param entityRef The block entity to handle
     * @see #handleTowerBlock(Vector3ic, EntityRef)
     */
    private void handleTowerBlock(EntityRef entityRef) {
        if (entityRef.hasComponent(LocationComponent.class)) {
            handleTowerBlock(new Vector3i(entityRef.getComponent(LocationComponent.class).getWorldPosition(new Vector3f()), RoundingMode.FLOOR), entityRef);
        }
    }

    /**
     * Handles a tower block being placed
     *
     * @param pos         The position of the block being placed
     * @param blockEntity The entity of the block being placed
     */
    private void handleTowerBlock(Vector3ic pos, EntityRef blockEntity) {
        /* Find all tower blocks nearby */
        Set<EntityRef> towers = findAttachedTowers(pos);
        switch (towers.size()) {
            /* No neighboring tower */
            case 0:
                addToTower(createNewTower(), blockEntity)
                        .send(new TowerCreatedEvent());
                break;
            /* One neighboring tower */
            case 1:
                addToTower(towers.iterator().next(), blockEntity)
                        .send(new OnBlocksAdded(blockEntity));
                break;
            /* Multiple neighboring towers */
            default:
                /* Pick a tower to merge all the others into */
                EntityRef targetTower = towers.iterator().next();
                Set<EntityRef> oldBlocks = getAllFrom(targetTower);

                addToTower(targetTower, blockEntity);
                towers.remove(targetTower);
                mergeTowers(targetTower, towers);

                Set<EntityRef> newBlocks = getAllFrom(targetTower);
                newBlocks = Sets.difference(newBlocks, oldBlocks);
                targetTower.send(new OnBlocksAdded(newBlocks));
                break;
        }
    }

    /**
     * Merge multiple towers into a single tower entity
     *
     * @param towers      The towers to merge
     * @param targetTower The tower to merge all the others into
     */
    private void mergeTowers(EntityRef targetTower, Set<EntityRef> towers) {
        /* Select a tower to merge into */
        for (EntityRef oldTower : towers) {
            mergeTowers(targetTower, oldTower);
        }
    }

    /**
     * Merges two towers into a single tower entity.
     *
     * @param destination The tower to merge into
     * @param source      The tower merging into the other. This entity will be destroyed.
     */
    private void mergeTowers(EntityRef destination, EntityRef source) {
        TowerComponent destComponent = destination.getComponent(TowerComponent.class);
        /* Get all the blocks from the old tower */
        Set<EntityRef> blocks = getAllFrom(source);
        /* Set them all to the new tower */
        blocks.forEach(entityRef -> entityRef.getComponent(TowerMultiBlockComponent.class).setTowerEntity(destination));

        /* Store them into the new tower */
        TowerComponent component = source.getComponent(TowerComponent.class);
        destComponent.cores.addAll(component.cores);
        destComponent.effector.addAll(component.effector);
        destComponent.targeter.addAll(component.targeter);
        destComponent.plains.addAll(component.plains);

        /* Destroy the old tower entity */
        source.send(new TowerDestroyedEvent());
        removeTower(source);
    }

    /**
     * Add a block to the given tower.
     *
     * @param blockEntity The block entity to add.
     * @param towerEntity The tower entity to add it to.
     */
    private EntityRef addToTower(EntityRef towerEntity, EntityRef blockEntity) {
        blockEntity.getComponent(TowerMultiBlockComponent.class).setTowerEntity(towerEntity);
        TowerComponent towerComponent = towerEntity.getComponent(TowerComponent.class);

        /* Add it to the relevant list of blocks */
        if (DefenceField.hasComponentExtending(blockEntity, TowerCore.class)) {
            towerComponent.cores.add(blockEntity);
        }
        if (DefenceField.hasComponentExtending(blockEntity, TowerEffector.class)) {
            towerComponent.effector.add(blockEntity);
        }
        if (DefenceField.hasComponentExtending(blockEntity, TowerTargeter.class)) {
            towerComponent.targeter.add(blockEntity);
        }

        towerComponent.plains.add(blockEntity);
        return towerEntity;
    }

    /**
     * Create a new tower
     *
     * @return The new tower entity
     */
    private EntityRef createNewTower() {
        return entityManager.create(DefenceUris.TOWER_ENTITY);
    }

    /**
     * Scan around the position and find any tower blocks that are a part of an existing structure.
     *
     * @param position The position to scan around
     * @return The neighbouring towers
     */
    private Set<EntityRef> findAttachedTowers(Vector3ic position) {
        Set<EntityRef> results = new HashSet<>();
        for (Side side : Side.values()) {
            Vector3i sidePos = new Vector3i(side.direction()).add(position);
            EntityRef entity = blockEntityRegistry.getExistingEntityAt(sidePos);

            TowerMultiBlockComponent component = entity.getComponent(TowerMultiBlockComponent.class);
            if (component != null && component.getTowerEntity().exists()) {
                results.add(component.getTowerEntity());
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
        EntityRef tower = component.getTowerEntity();
        if (tower.exists()) {
            tower.send(new TowerDestroyedEvent());
            removeBlockFromTower(tower, entity);
            rebuildTower(tower);
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
        component.cores.remove(block);
        component.targeter.remove(block);
        component.effector.remove(block);
        component.plains.remove(block);
        block.getComponent(TowerMultiBlockComponent.class).setTowerEntity(EntityRef.NULL);
    }

    /**
     * Removes a tower entity.
     * Sending the appropriate events.
     *
     * @param tower The tower to remove
     */
    private void removeTower(EntityRef tower) {
        tower.destroy();
    }

    /**
     * Rebuilds a given tower into one or multiple towers
     *
     * @param tower The tower to rebuild
     */
    private void rebuildTower(EntityRef tower) {
        Set<EntityRef> blocks = getAllFrom(tower);
        removeTower(tower);

        /* Remove their references to a tower entity */
        blocks.forEach(entityRef -> entityRef.getComponent(TowerMultiBlockComponent.class).setTowerEntity(EntityRef.NULL));

        for (EntityRef block : blocks) {
            Vector3i pos = new Vector3i(block.getComponent(LocationComponent.class).getWorldPosition(new Vector3f()), RoundingMode.FLOOR);
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
        Set<EntityRef> results = new HashSet<>();
        results.addAll(component.cores);
        results.addAll(component.effector);
        results.addAll(component.targeter);
        results.addAll(component.plains);

        return results;
    }

}
