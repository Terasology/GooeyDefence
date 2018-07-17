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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.assets.management.AssetManager;
import org.terasology.entitySystem.ComponentContainer;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.gooeyDefence.ui.shop.ShopScreen;
import org.terasology.logic.console.commandSystem.annotations.Command;
import org.terasology.logic.inventory.InventoryManager;
import org.terasology.logic.inventory.ItemComponent;
import org.terasology.logic.permission.PermissionManager;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.registry.In;
import org.terasology.rendering.nui.NUIManager;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;
import org.terasology.world.block.items.BlockItemFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handles the purchasing of blocks
 */
@RegisterSystem
public class ShopManager extends BaseComponentSystem {
    private static final Logger logger = LoggerFactory.getLogger(ShopManager.class);

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
    @In
    private NUIManager nuiManager;

    private EntityRef character;
    private BlockItemFactory blockItemFactory;

    /**
     * Gets how much money a ware will cost.
     * Tries to use the cost on the purchasable component, with the value component as a fallback.
     *
     * @param ware The ware to get the price for
     * @return The price of the ware.
     */
    public static int getWareCost(ComponentContainer ware) {
        int cost = ware.getComponent(PurchasableComponent.class).getCost();
        if (cost < 0) {
            if (ware.hasComponent(ValueComponent.class)) {
                return ware.getComponent(ValueComponent.class).getValue();
            } else {
                return 0;
            }
        } else {
            return cost;
        }
    }

    @Override
    public void postBegin() {
        character = localPlayer.getCharacterEntity();
        blockItemFactory = new BlockItemFactory(entityManager);

        purchasableItems = assetManager.getLoadedAssets(Prefab.class)
                .stream()
                .filter(prefab -> prefab.hasComponent(ItemComponent.class)
                        && prefab.hasComponent(PurchasableComponent.class))
                .collect(Collectors.toSet());

        purchasableBlocks = blockManager.listRegisteredBlocks()
                .stream()
                .filter(block -> block.getPrefab()
                        .map(prefab -> prefab.hasComponent(PurchasableComponent.class))
                        .orElse(false))
                .collect(Collectors.toSet());

    }

    @Command(value = "showShop", shortDescription = "Show the shop screen",
            requiredPermission = PermissionManager.NO_PERMISSION)
    public String showShop() {
        ShopScreen shopScreen = nuiManager.pushScreen("GooeyDefence:ShopScreen", ShopScreen.class);
        shopScreen.addBlocks(purchasableBlocks);
        shopScreen.addItems(purchasableItems);
        shopScreen.subscribeBlockPurchase(block -> purchase(blockItemFactory.newInstance(block.getBlockFamily())));
        shopScreen.subscribePrefabPurchase(prefab -> purchase(entityManager.create(prefab)));
        return "Screen shown.";
    }

    private boolean purchase(EntityRef ware) {
        return EconomyManager.tryRemoveMoney(character, getWareCost(ware))
                && inventoryManager.giveItem(character, character, ware);
    }

}
