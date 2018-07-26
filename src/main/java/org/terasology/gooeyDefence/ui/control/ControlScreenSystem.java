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

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.EventPriority;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.gooeyDefence.components.ShrineComponent;
import org.terasology.gooeyDefence.waves.OnWaveEnd;
import org.terasology.input.ButtonState;
import org.terasology.input.binds.interaction.FrobButton;
import org.terasology.logic.characters.CharacterComponent;
import org.terasology.logic.common.ActivateEvent;
import org.terasology.registry.In;
import org.terasology.rendering.nui.NUIManager;

/**
 * Manages the display, closing of the ControlScreen.
 * Also updates the screen where needed with specific values.
 *
 * @see ControlScreen
 */
@RegisterSystem
public class ControlScreenSystem extends BaseComponentSystem {

    @In
    private NUIManager nuiManager;
    private boolean screenJustOpened = false;
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
        if (nuiManager.isOpen("GooeyDefence:ControlScreen")) {
            nuiManager.closeScreen("GooeyDefence:ControlScreen");
            screen = null;
        } else {
            screen = nuiManager.pushScreen("GooeyDefence:ControlScreen", ControlScreen.class);
            screenJustOpened = true;
        }
    }

    /**
     * Sent when a wave ends.
     * Used to update the WaveInfo shown in the ui screen
     *
     * @see OnWaveEnd
     */
    @ReceiveEvent
    public void onWaveEnd(OnWaveEnd event, EntityRef entity) {
        screen.reloadWaveInfo();
    }

    /**
     * Called when the Frob button is pressed.
     * Used to close the tower screen if it is open.
     * <p>
     * Filters on {@link CharacterComponent}
     *
     * @see FrobButton
     */
    @ReceiveEvent(priority = EventPriority.PRIORITY_LOW, components = CharacterComponent.class)
    public void onFrobButton(FrobButton event, EntityRef entity) {
        if (event.getState() == ButtonState.UP
                && nuiManager.isOpen("GooeyDefence:ControlScreen")) {
            if (screenJustOpened) {
                screenJustOpened = false;
            } else {
                screen = null;
                nuiManager.closeScreen("GooeyDefence:ControlScreen");
            }
        }
    }

}
