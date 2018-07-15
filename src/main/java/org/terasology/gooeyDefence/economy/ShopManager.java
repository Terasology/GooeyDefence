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
import org.terasology.entitySystem.prefab.PrefabManager;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.console.commandSystem.annotations.Command;
import org.terasology.logic.permission.PermissionManager;
import org.terasology.registry.In;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handles the purchasing of blocks
 */
@RegisterSystem
public class ShopManager extends BaseComponentSystem {

    private Set<Prefab> purchasablePrefabs;

    @In
    private AssetManager assetManager;
    @In
    private PrefabManager prefabManager;

    @Override
    public void postBegin() {
        purchasablePrefabs = assetManager.getLoadedAssets(Prefab.class)
                .stream()
                .filter(prefab -> prefab.hasComponent(PurchasableComponent.class))
                .collect(Collectors.toSet());
    }

    @Command(value = "listWares", shortDescription = "List all the purchasable blocks",
            requiredPermission = PermissionManager.NO_PERMISSION)
    public String listWares(EntityRef sender) {
        return String.join(", ",
                purchasablePrefabs.stream()
                        .map(prefab -> prefab.getUrn().getResourceName().toString())
                        .collect(Collectors.toList()));
    }
}
