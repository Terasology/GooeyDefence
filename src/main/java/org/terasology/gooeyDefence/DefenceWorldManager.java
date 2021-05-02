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

import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.terasology.gestalt.assets.management.AssetManager;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.EventPriority;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.characters.events.AttackEvent;
import org.terasology.engine.logic.health.DestroyEvent;
import org.terasology.engine.logic.health.EngineDamageTypes;
import org.terasology.engine.logic.inventory.events.DropItemEvent;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.registry.In;
import org.terasology.engine.registry.Share;
import org.terasology.engine.utilities.procedural.Noise;
import org.terasology.engine.utilities.procedural.WhiteNoise;
import org.terasology.engine.world.WorldProvider;
import org.terasology.engine.world.block.Block;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.engine.world.block.entity.CreateBlockDropsEvent;
import org.terasology.engine.world.block.items.BlockItemFactory;
import org.terasology.engine.world.sun.CelestialSystem;
import org.terasology.gooeyDefence.components.DestructibleBlockComponent;
import org.terasology.gooeyDefence.components.FieldConfigComponent;
import org.terasology.gooeyDefence.events.OnFieldActivated;
import org.terasology.gooeyDefence.events.OnFieldReset;
import org.terasology.gooeyDefence.worldGeneration.providers.RandomFillingProvider;

import java.util.Optional;

/**
 * Performs miscellaneous tasks not located in other tasks
 * A catch all class.
 *
 * @see DefenceField
 */
@Share(DefenceWorldManager.class)
@RegisterSystem
public class DefenceWorldManager extends BaseComponentSystem {
    private static boolean settingUpField;
    @In
    private CelestialSystem celestialSystem;
    @In
    private EntityManager entityManager;
    @In
    private BlockManager blockManager;
    @In
    private WorldProvider worldProvider;
    @In
    private AssetManager assetManager;

    private Block air;
    private Block shrineBlock;
    private Block fieldBlock;
    private BlockItemFactory factory;

    /**
     * Initialises the defence field
     */
    public static void activateWorld() {
        if (!settingUpField) {
            settingUpField = true;
            OnFieldActivated activateEvent = new OnFieldActivated(() -> DefenceField.fieldActivated = true);
            activateEvent.beginTask();
            DefenceField.getShrineEntity().send(activateEvent);
            activateEvent.finishTask();
        }
    }

    @Override
    public void initialise() {
        Optional<Prefab> prefab = assetManager.getAsset(DefenceUris.FIELD_CONFIG, Prefab.class);
        Prefab configPrefab = prefab.orElseThrow(() -> new IllegalStateException("No field config found!"));
        DefenceField.loadFieldValues(configPrefab.getComponent(FieldConfigComponent.class));
    }

    @Override
    public void preBegin() {
        if (!celestialSystem.isSunHalted()) {
            celestialSystem.toggleSunHalting(0.5f);
        }
        factory = new BlockItemFactory(entityManager);
        air = blockManager.getBlock(BlockManager.AIR_ID);
        shrineBlock = blockManager.getBlock(DefenceUris.SHRINE);
        fieldBlock = blockManager.getBlock(DefenceUris.PLAIN_WORLD_BLOCK);
    }

    /**
     * Make blocks destroy instantly
     */
    @ReceiveEvent(priority = EventPriority.PRIORITY_HIGH)
    public void onAttackEntity(AttackEvent event, EntityRef targetEntity) {
        event.consume();
        if (targetEntity.hasComponent(DestructibleBlockComponent.class)) {
            targetEntity.send(new DestroyEvent(event.getInstigator(), event.getDirectCause(), EngineDamageTypes.PHYSICAL.get()));
        }
    }

    /**
     * Make the world gen blocks drop a plain block as an item
     */
    @ReceiveEvent
    public void onCreateBlockDrops(CreateBlockDropsEvent event, EntityRef entity, LocationComponent component) {
        if (entity.getParentPrefab().getName().equals(DefenceUris.PLAIN_WORLD_BLOCK)) {
            event.consume();
            factory.newInstance(blockManager.getBlockFamily(DefenceUris.PLAIN_BLOCK)).send(new DropItemEvent(component.getWorldPosition(new Vector3f())));
        }
    }

    /**
     * @see OnFieldReset
     */
    @ReceiveEvent
    public void onFieldReset(OnFieldReset event, EntityRef entity) {
        clearField(DefenceField.outerRingSize);
        createRandomFill(DefenceField.outerRingSize);
    }

    /**
     * Clears all non world gen block from the field.
     * <p>
     * The only allowed blocks will be
     * - {@link DefenceUris#SHRINE}
     * - {@link BlockManager#AIR_ID}
     *
     * @param size The size of the field to clear.
     */
    private void clearField(int size) {
        Vector3i pos = new Vector3i();
        Block block;
        for (int x = -size; x <= size; x++) {
            /* We use circle eq "x^2 + y^2 = r^2" to work out where we need to start */
            int width = (int) Math.floor(Math.sqrt(size * size - x * x));
            for (int z = -width; z <= width; z++) {
                /* We use sphere eq "x^2 + y^2 + z^2 = r^2" to work out how high we need to go */
                int height = (int) Math.floor(Math.sqrt(size * size - z * z - x * x) - 0.001f);
                for (int y = 0; y <= height; y++) {
                    pos.set(x, y, z);
                    block = worldProvider.getBlock(pos);
                    if (block != air && block != shrineBlock) {
                        worldProvider.setBlock(pos, air);
                    }
                }
            }
        }
    }

    /**
     * Randomly fills in an area, according to the same rules as the world gen
     *
     * @param size The size of the area to fill
     * @see RandomFillingProvider
     */
    private void createRandomFill(int size) {
        Noise noise = new WhiteNoise(System.currentTimeMillis());
        Vector2i pos2i = new Vector2i();
        Vector3i pos3i = new Vector3i();

        for (int x = -size; x <= size; x++) {
            int width = (int) Math.floor(Math.sqrt(size * size - x * x));
            for (int y = -width; y <= width; y++) {
                pos2i.set(x,y);
                if (RandomFillingProvider.shouldSpawnBlock(pos2i, noise)) {
                    pos3i.x = pos2i.x;
                    pos3i.z = pos2i.y;
                    worldProvider.setBlock(pos3i, fieldBlock);
                }
            }
        }
    }

}
