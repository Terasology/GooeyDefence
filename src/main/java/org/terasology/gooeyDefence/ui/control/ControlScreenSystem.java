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
package org.terasology.gooeyDefence.ui.control;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.EventPriority;
import org.terasology.engine.entitySystem.event.Priority;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.input.binds.interaction.FrobButton;
import org.terasology.engine.logic.characters.CharacterComponent;
import org.terasology.engine.logic.common.ActivateEvent;
import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.nui.NUIManager;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.gooeyDefence.DefenceUris;
import org.terasology.gooeyDefence.components.ShrineComponent;
import org.terasology.gooeyDefence.waves.OnWaveEnd;
import org.terasology.input.ButtonState;

/**
 * Manages the display & closing of the ControlScreen.
 * Also updates the screen where needed with specific values.
 *
 * @see ControlScreen
 */
@RegisterSystem
public class ControlScreenSystem extends BaseComponentSystem {

    @In
    private NUIManager nuiManager;
    /**
     * Handles the situation where the screen is closed just after it is opened.
     */
    private boolean screenJustOpened;
    private ControlScreen screen;

    /**
     * Called when the shrine is interacted with, in order to display the control screen
     * <p>
     * Filters on {@link ShrineComponent}
     *
     * @see ActivateEvent
     */
    @ReceiveEvent(components = ShrineComponent.class)
    public void onActivate(ActivateEvent event, EntityRef entity) {
        if (nuiManager.isOpen(DefenceUris.CONTROL_SCREEN)) {
            nuiManager.closeScreen(DefenceUris.CONTROL_SCREEN);
            screen = null;
        } else {
            screen = nuiManager.pushScreen(DefenceUris.CONTROL_SCREEN, ControlScreen.class);
            screenJustOpened = true;
        }
    }

    /**
     * Used to update the WaveInfo shown in the ui screen
     * <p>
     * Called when a wave ends.
     *
     * @see OnWaveEnd
     */
    @ReceiveEvent
    public void onWaveEnd(OnWaveEnd event, EntityRef entity) {
        if (screen != null) {
            screen.reloadWaveInfo();
        }
    }

    /**
     * Used to close the tower screen if it is open.
     * <p>
     * Called when the Frob button is pressed.
     * Filters on {@link CharacterComponent}
     * Has an {@link EventPriority#PRIORITY_LOW}
     *
     * @see FrobButton
     */
    @Priority(EventPriority.PRIORITY_LOW)
    @ReceiveEvent(components = CharacterComponent.class)
    public void onFrobButton(FrobButton event, EntityRef entity) {
        if (event.getState() == ButtonState.UP
                && nuiManager.isOpen(DefenceUris.CONTROL_SCREEN)) {
            if (screenJustOpened) {
                screenJustOpened = false;
            } else {
                screen = null;
                nuiManager.closeScreen(DefenceUris.CONTROL_SCREEN);
            }
        }
    }

}
