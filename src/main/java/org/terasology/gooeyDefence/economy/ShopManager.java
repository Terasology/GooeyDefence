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

import org.terasology.economy.components.CurrencyStorageComponent;
import org.terasology.economy.events.WalletUpdatedEvent;
import org.terasology.engine.entitySystem.event.EventPriority;
import org.terasology.engine.logic.players.event.OnPlayerSpawnedEvent;
import org.terasology.gestalt.assets.management.AssetManager;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.module.inventory.components.InventoryComponent;
import org.terasology.engine.logic.players.LocalPlayer;
import org.terasology.engine.registry.In;
import org.terasology.gooeyDefence.DefenceUris;
import org.terasology.gooeyDefence.events.OnFieldReset;

import java.util.Optional;

/**
 * Handles the purchasing of blocks
 */
@RegisterSystem
public class ShopManager extends BaseComponentSystem {
    @In
    private AssetManager assetManager;
    @In
    private LocalPlayer localPlayer;
    @In
    private EntityManager entityManager;

    @ReceiveEvent(priority = EventPriority.PRIORITY_LOW)
    public void onPlayerJoin(OnPlayerSpawnedEvent onPlayerSpawnedEvent, EntityRef player) {
        CurrencyStorageComponent component = new CurrencyStorageComponent();
        component.amount = 100;

        player.addOrSaveComponent(component);
        player.send(new WalletUpdatedEvent(component.amount));
    }

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
        localPlayer.getCharacterEntity().getComponent(CurrencyStorageComponent.class).amount = 100;
        localPlayer.getCharacterEntity().send(new WalletUpdatedEvent(100));
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
}
