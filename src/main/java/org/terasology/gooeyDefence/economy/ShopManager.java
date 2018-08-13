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
package org.terasology.gooeyDefence.economy;

import org.terasology.assets.management.AssetManager;
import org.terasology.entitySystem.ComponentContainer;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.gooeyDefence.DefenceUris;
import org.terasology.gooeyDefence.events.OnFieldReset;
import org.terasology.logic.inventory.InventoryComponent;
import org.terasology.logic.inventory.InventoryManager;
import org.terasology.logic.inventory.ItemComponent;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.registry.In;
import org.terasology.registry.Share;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockExplorer;
import org.terasology.world.block.BlockManager;
import org.terasology.world.block.BlockUri;
import org.terasology.world.block.family.BlockFamily;
import org.terasology.world.block.items.BlockItemFactory;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handles the purchasing of blocks
 */
@RegisterSystem
@Share(ShopManager.class)
public class ShopManager extends BaseComponentSystem {

    private Set<Block> purchasableBlocks = new HashSet<>();
    private Set<Prefab> purchasableItems = new HashSet<>();

    @In
    private AssetManager assetManager;
    @In
    private BlockManager blockManager;
    @In
    private InventoryManager inventoryManager;
    @In
    private LocalPlayer localPlayer;
    @In
    private EntityManager entityManager;

    private BlockItemFactory blockItemFactory;

    /**
     * Gets how much money a ware will cost.
     * Tries to use the cost on the purchasable component, with the value component as a fallback.
     *
     * @param ware The ware to get the price for
     * @return The price of the ware.
     */
    public static int getWareCost(ComponentContainer ware) {
        int cost = ware.getComponent(PurchasableComponent.class).cost;
        if (cost < 0) {
            if (ware.hasComponent(ValueComponent.class)) {
                return ware.getComponent(ValueComponent.class).value;
            } else {
                return 0;
            }
        } else {
            return cost;
        }
    }

    @Override
    public void postBegin() {
        blockItemFactory = new BlockItemFactory(entityManager);
        BlockExplorer blockExplorer = new BlockExplorer(assetManager);

        purchasableItems = assetManager.getLoadedAssets(Prefab.class)
                .stream()
                .filter(prefab -> prefab.hasComponent(ItemComponent.class)
                        && prefab.hasComponent(PurchasableComponent.class))
                .collect(Collectors.toSet());

        Set<BlockUri> blocks = new HashSet<>();
        blocks.addAll(blockManager.listRegisteredBlockUris());
        blocks.addAll(blockExplorer.getAvailableBlockFamilies());
        blocks.addAll(blockExplorer.getFreeformBlockFamilies());

        purchasableBlocks = blocks.stream()
                .map(blockManager::getBlockFamily)
                .map(BlockFamily::getArchetypeBlock)
                .filter(block -> block.getPrefab().isPresent())
                .filter(block -> block.getPrefab().get().hasComponent(PurchasableComponent.class))
                .collect(Collectors.toSet());
    }

    /**
     * Deletes any dropped money, resets the players money, and clears their inventory
     * <p>
     * Called when the field is reset.
     *
     * @see OnFieldReset
     */
    @ReceiveEvent
    public void onFieldReset(OnFieldReset event, EntityRef entity) {
        resetMoney();
        cleanUpMoney();
        cleanUpInventory();
    }

    private void cleanUpInventory() {
        InventoryComponent component = assetManager.getAsset(DefenceUris.PLAYER, Prefab.class)
                .map(prefab -> prefab.getComponent(InventoryComponent.class))
                .orElse(new InventoryComponent(0));
        localPlayer.getCharacterEntity().addOrSaveComponent(component);
    }

    private void resetMoney() {
        WalletComponent component = assetManager.getAsset(DefenceUris.PLAYER, Prefab.class)
                .map(prefab -> prefab.getComponent(WalletComponent.class))
                .orElse(new WalletComponent());
        localPlayer.getCharacterEntity().addOrSaveComponent(component);
    }

    private void cleanUpMoney() {
        Optional<Prefab> optionalPrefab = assetManager.getAsset(DefenceUris.MONEY_ITEM, Prefab.class);
        if (optionalPrefab.isPresent()) {
            Prefab moneyPrefab = optionalPrefab.get();
            for (EntityRef entityRef : entityManager.getAllEntities()) {
                if (moneyPrefab.equals(entityRef.getParentPrefab())) {
                    entityRef.destroy();
                }
            }
        }
    }

    /**
     * @return All the blocks for sale
     */
    public Set<Block> getAllBlocks() {
        return purchasableBlocks;
    }

    /**
     * @return All the items for sale
     */
    public Set<Prefab> getAllItems() {
        return purchasableItems;
    }

    /**
     * Attempt to purchase a block.
     * <p>
     * Calls on {@link #purchase(EntityRef)}
     *
     * @param block The block to purchase
     */
    public void purchase(Block block) {
        purchase(blockItemFactory.newInstance(block.getBlockFamily()));
    }

    /**
     * Attempt to purchase a prefab.
     * <p>
     * Calls on {@link #purchase(EntityRef)}
     *
     * @param prefab The prefab to purchase
     */
    public void purchase(Prefab prefab) {
        purchase(entityManager.create(prefab));
    }

    /**
     * Tries to purchase an entity, by removing the cost and giving the item.
     *
     * @param ware The item to buy
     */
    private void purchase(EntityRef ware) {
        EntityRef character = localPlayer.getCharacterEntity();
        if (EconomyManager.tryRemoveMoney(character, getWareCost(ware))) {
            inventoryManager.giveItem(character, character, ware);
        }
    }
}
