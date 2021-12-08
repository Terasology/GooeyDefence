// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.economy;

import org.terasology.economy.components.AllowShopScreenComponent;
import org.terasology.economy.components.CurrencyStorageComponent;
import org.terasology.economy.events.WalletUpdatedEvent;
import org.terasology.economy.ui.MarketUiClientSystem;
import org.terasology.engine.core.SimpleUri;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.entity.lifecycleEvents.OnAddedComponent;
import org.terasology.engine.entitySystem.event.EventPriority;
import org.terasology.engine.entitySystem.event.Priority;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.input.InputSystem;
import org.terasology.engine.logic.players.LocalPlayer;
import org.terasology.engine.logic.players.event.OnPlayerSpawnedEvent;
import org.terasology.engine.network.ClientComponent;
import org.terasology.engine.registry.In;
import org.terasology.engine.unicode.EnclosedAlphanumerics;
import org.terasology.gestalt.assets.management.AssetManager;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.gooeyDefence.DefenceUris;
import org.terasology.gooeyDefence.events.OnFieldReset;
import org.terasology.input.ButtonState;
import org.terasology.input.Input;
import org.terasology.module.inventory.components.InventoryComponent;
import org.terasology.module.inventory.input.InventoryButton;
import org.terasology.notifications.events.ExpireNotificationEvent;
import org.terasology.notifications.events.ShowNotificationEvent;
import org.terasology.notifications.model.Notification;
import org.terasology.nui.Color;
import org.terasology.nui.FontColor;

import java.util.Optional;

/**
 * Handles the purchasing of blocks
 */
@RegisterSystem
public class ShopManager extends BaseComponentSystem {
    private static final String NOTIFICATION_ID = "GooeyDefence:firstTime";

    @In
    private InputSystem inputSystem;
    @In
    private AssetManager assetManager;
    @In
    private LocalPlayer localPlayer;
    @In
    private EntityManager entityManager;

    @Priority(EventPriority.PRIORITY_LOW)
    @ReceiveEvent
    public void onPlayerJoin(OnPlayerSpawnedEvent onPlayerSpawnedEvent, EntityRef player, CurrencyStorageComponent currencyStorageComponent) {
        // Fill player's wallet
        CurrencyStorageComponent component = assetManager.getAsset(DefenceUris.PLAYER, Prefab.class)
                .map(prefab -> prefab.getComponent(CurrencyStorageComponent.class))
                .orElse(new CurrencyStorageComponent());
        player.addOrSaveComponent(component);
        player.send(new WalletUpdatedEvent(component.amount));

        // Ensure that the client has the {@link AllowShopScreenComponent} such that they can use the in-game shop from the Economy module.
        localPlayer.getClientEntity().upsertComponent(AllowShopScreenComponent.class, c -> c.orElse(new AllowShopScreenComponent()));
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
        CurrencyStorageComponent component = assetManager.getAsset(DefenceUris.PLAYER, Prefab.class)
                .map(prefab -> prefab.getComponent(CurrencyStorageComponent.class))
                .orElse(new CurrencyStorageComponent());
        localPlayer.getCharacterEntity().addOrSaveComponent(component);
        localPlayer.getCharacterEntity().send(new WalletUpdatedEvent(component.amount));
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
     * Handles the button event if in-game shop is enabled.
     * Needs to have a higher priority than {@link MarketUiClientSystem#onToggleInventory(InventoryButton, EntityRef)}
     * to receive the {@link InventoryButton} event before it is consumed.
     *
     * @param event the help button event.
     * @param entity the entity to display the help screen to.
     */
    @Priority(EventPriority.PRIORITY_CRITICAL)
    @ReceiveEvent(components = {ClientComponent.class, AllowShopScreenComponent.class})
    public void onInGameShopButton(InventoryButton event, EntityRef entity) {
        if (event.getState() == ButtonState.DOWN) {
            entity.send(new ExpireNotificationEvent(NOTIFICATION_ID));
        }
    }

    /**
     * Get a formatted representation of the primary {@link Input} associated with the given button binding.
     *
     * If the display name of the primary bound key is a single character this representation will be the encircled
     * character. Otherwise the full display name is used. The bound key will be printed in yellow.
     *
     * If no key binding was found the text "n/a" in red color is returned.
     *
     * @param button the URI of a bindable button
     * @return a formatted text to be used as representation for the player
     */
    //TODO: put this in a common place? Duplicated in Dialogs, EventualSkills, and InGameHelp
    private String getActivationKey(SimpleUri button) {
        return inputSystem.getInputsForBindButton(button).stream()
                .findFirst()
                .map(Input::getDisplayName)
                .map(key -> {
                    if (key.length() == 1) {
                        // print the key in yellow within a circle
                        int off = key.charAt(0) - 'A';
                        char code = (char) (EnclosedAlphanumerics.CIRCLED_LATIN_CAPITAL_LETTER_A + off);
                        return String.valueOf(code);
                    } else {
                        return key;
                    }
                })
                .map(key -> FontColor.getColored(key, Color.yellow))
                .orElse(FontColor.getColored("n/a", Color.red));
    }

    @ReceiveEvent(components = AllowShopScreenComponent.class)
    public void onShopComponentAdded(OnAddedComponent event, EntityRef entity) {
        Notification notification = new Notification(NOTIFICATION_ID,
                "Shut Up and Take My Money!",
                "Press " + getActivationKey(new SimpleUri("Inventory:inventory")) + " to buy tower parts",
                "Economy:GoldCoin");
        localPlayer.getClientEntity().send(new ShowNotificationEvent(notification));
    }
}
