// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.ui.activation;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.players.LocalPlayer;
import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.nui.NUIManager;
import org.terasology.engine.rendering.nui.ScreenLayerClosedEvent;
import org.terasology.engine.rendering.nui.layers.mainMenu.loadingScreen.LoadingScreen;
import org.terasology.gestalt.assets.ResourceUrn;
import org.terasology.gooeyDefence.DefenceUris;
import org.terasology.gooeyDefence.DefenceWorldManager;
import org.terasology.gooeyDefence.components.SavedGameFlagComponent;

/**
 * System that handles the display of the {@link ActivateGameScreen}.
 * <p>
 * Handles listening for the begin button and setting the new game flag in the screen
 *
 * @see SavedGameFlagComponent
 * @see ActivateGameScreen
 */
@RegisterSystem
public class ActivateScreenSystem extends BaseComponentSystem {

    @In
    private NUIManager nuiManager;
    @In
    private LocalPlayer localPlayer;

    /**
     * Displays the activate screen when the loading screen is closed.
     * <p>
     * Called when a screen is closed
     *
     * @see ScreenLayerClosedEvent
     * @see LoadingScreen
     */
    @ReceiveEvent
    public void onPlayerSpawned(ScreenLayerClosedEvent event, EntityRef entity) {
        if (event.getClosedScreenUri().equals(new ResourceUrn(DefenceUris.LOADING_SCREEN))) {
            ActivateGameScreen screen = nuiManager.pushScreen(DefenceUris.ACTIVATE_SCREEN, ActivateGameScreen.class);
            screen.subscribeToBegin(widget -> nuiManager.closeScreen(screen));

            screen.setNewGame(!localPlayer.getCharacterEntity().hasComponent(SavedGameFlagComponent.class));
            localPlayer.getCharacterEntity().addOrSaveComponent(new SavedGameFlagComponent());
        } else if (event.getClosedScreenUri().equals(new ResourceUrn(DefenceUris.ACTIVATE_SCREEN))) {
            DefenceWorldManager.activateWorld();
        }
    }
}
