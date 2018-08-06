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
package org.terasology.gooeyDefence.ui.activation;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.gooeyDefence.DefenceWorldManager;
import org.terasology.gooeyDefence.components.SavedGameFlagComponent;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.registry.In;
import org.terasology.rendering.nui.NUIManager;
import org.terasology.rendering.nui.ScreenLayerClosedEvent;
import org.terasology.rendering.nui.layers.mainMenu.loadingScreen.LoadingScreen;

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

    @Override
    public void postBegin() {
        super.postBegin();
    }

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
        if (event.getClosedScreenUri().getResourceName().toString().equals("loadingScreen")) {
            ActivateGameScreen screen = nuiManager.pushScreen("activateGameScreen", ActivateGameScreen.class);
            screen.subscribeToBegin(widget -> nuiManager.closeScreen(screen));

            screen.setNewGame(!localPlayer.getCharacterEntity().hasComponent(SavedGameFlagComponent.class));
            localPlayer.getCharacterEntity().addOrSaveComponent(new SavedGameFlagComponent());
        } else if (event.getClosedScreenUri().getResourceName().toString().equals("activateGameScreen")) {
            DefenceWorldManager.activateWorld();
        }
    }
}
