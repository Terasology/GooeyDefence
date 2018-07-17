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
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.gooeyDefence.ui.shop.ShopScreen;
import org.terasology.logic.common.DisplayNameComponent;
import org.terasology.logic.console.commandSystem.annotations.Command;
import org.terasology.logic.console.commandSystem.annotations.CommandParam;
import org.terasology.logic.console.commandSystem.annotations.Sender;
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
        shopScreen.setBlocks(purchasableBlocks);
        shopScreen.setItems(purchasableItems);
        return "Screen shown.";
    }

    @Command(shortDescription = "Gives the player one of the item.")
    public String purchase(@Sender EntityRef sender, @CommandParam(value = "Name of Item") String[] wareNameSplit) {
        String wareName = String.join(" ", wareNameSplit);
        EntityRef ware = getWareMatching(wareName);
        if (ware == EntityRef.NULL) {
            return "Unable to find ware matching " + wareName;
        } else {
            if (EconomyManager.tryRemoveMoney(character, getWareCost(ware))
                    && inventoryManager.giveItem(character, character, ware)) {
                return "Given ware '" + wareName + "' to you";
            } else {
                return "Unable to give you the ware '" + wareName + "'";
            }
        }
    }

    /**
     * Gets the name of a prefab.
     * This is the human readable variant of it.
     *
     * @param prefab The prefab to get the name of
     * @return The string name of the prefab
     */
    private String getPrefabName(Prefab prefab) {
        return prefab.hasComponent(DisplayNameComponent.class)
                ? prefab.getComponent(DisplayNameComponent.class).name
                : prefab.getUrn().getResourceName().toString();
    }

    /**
     * Gets the name of a block.
     * This is the human readable variant of it.
     *
     * @param block The block to get the name of
     * @return The string name of the block
     */
    private String getBlockName(Block block) {
        String displayName = block.getDisplayName();
        return !displayName.equals("Untitled Block") ?
                displayName :
                block.getURI().getIdentifier().toString();
    }

    /**
     * Locates a prefab matching the given string.
     * <p>
     * If multiple are found, then no guarantee is made about which will be picked.
     * If none is found, null is returned
     *
     * @param wareName The name of the item to search for
     * @return The item that matches, or else null
     */
    private Prefab findItemMatching(String wareName) {
        return purchasableItems.stream()
                .filter(prefab ->
                        prefab.getUrn().getResourceName().toString().equals(wareName)
                                || (prefab.hasComponent(DisplayNameComponent.class)
                                && prefab.getComponent(DisplayNameComponent.class).name.equals(wareName)))
                .findAny()
                .orElse(null);
    }

    /**
     * Locates a prefab matching the given string.
     * <p>
     * If multiple are found, then no guarantee is made about which will be picked.
     * If none is found, null is returned
     *
     * @param wareName The name of the item to search for
     * @return The item that matches, or else null
     */
    private Block findBlockMatching(String wareName) {
        return purchasableBlocks.stream()
                .filter(block ->
                        block.getURI().getIdentifier().toString().equals(wareName)
                                || block.getDisplayName().equals(wareName))
                .findAny()
                .orElse(null);
    }

    /**
     * Gets an entity representing the ware given by the string.
     * If no ware could be found, then a null entity is returned.
     *
     * @param wareName The name of the ware to find
     * @return The ware, or a null entity if it wasn't found
     */
    private EntityRef getWareMatching(String wareName) {
        Prefab item = findItemMatching(wareName);
        EntityRef ware = EntityRef.NULL;
        if (item != null) {
            ware = entityManager.create(item);
        } else {
            Block block = findBlockMatching(wareName);
            if (block != null) {
                ware = blockItemFactory.newInstance(block.getBlockFamily());
            }
        }
        return ware;
    }

    /**
     * Gets how much money a ware will cost.
     * Tries to use the cost on the purchasable component, with the value component as a fallback.
     *
     * @param ware The ware to get the price for
     * @return The price of the ware.
     */
    private int getWareCost(EntityRef ware) {
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
}
