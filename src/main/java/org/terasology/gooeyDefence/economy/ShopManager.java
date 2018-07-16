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
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.common.DisplayNameComponent;
import org.terasology.logic.console.commandSystem.annotations.Command;
import org.terasology.logic.inventory.ItemComponent;
import org.terasology.logic.permission.PermissionManager;
import org.terasology.registry.In;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;

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

    @Override
    public void postBegin() {
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

    @Command(value = "listWares", shortDescription = "List all the purchasable items and blocks",
            requiredPermission = PermissionManager.NO_PERMISSION)
    public String listWares(EntityRef sender) {
        return "Items: "
                + String.join(", ",
                purchasableItems.stream()
                        .map(prefab -> prefab.hasComponent(DisplayNameComponent.class)
                                ? prefab.getComponent(DisplayNameComponent.class).name
                                : prefab.getUrn().getResourceName().toString())
                        .collect(Collectors.toList()))
                + "\nBlocks: "
                + String.join(", ",
                purchasableBlocks.stream()
                        .map(block -> {
                            String displayName = block.getDisplayName();
                            return !displayName.equals("Untitled Block") ?
                                    displayName :
                                    block.getURI().getIdentifier().toString();
                        })
                        .collect(Collectors.toSet()));
    }


}
