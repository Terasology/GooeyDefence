// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.ui.towers;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.EventPriority;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.input.binds.interaction.FrobButton;
import org.terasology.engine.logic.characters.CharacterComponent;
import org.terasology.engine.logic.common.ActivateEvent;
import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.nui.NUIManager;
import org.terasology.gooeyDefence.DefenceUris;
import org.terasology.gooeyDefence.towers.components.TowerMultiBlockComponent;
import org.terasology.gooeyDefence.upgrading.UpgradingSystem;
import org.terasology.nui.input.ButtonState;

/**
 * Handles setting up and showing the tower screen when a tower is interacted with
 *
 * @see TowerInfoScreen
 */
@RegisterSystem
public class TowerInfoSystem extends BaseComponentSystem {

    @In
    private NUIManager nuiManager;
    @In
    private UpgradingSystem upgradingSystem;

    /**
     * Used to avoid a situation where a screen is closed right after opening
     */
    private boolean screenJustOpened;

    /**
     * Called when a tower block is interacted with
     * <p>
     * Filters on {@link TowerMultiBlockComponent}
     *
     * @see ActivateEvent
     */
    @ReceiveEvent(priority = EventPriority.PRIORITY_HIGH)
    public void onActivate(ActivateEvent event, EntityRef entity, TowerMultiBlockComponent component) {
        if (!nuiManager.isOpen(DefenceUris.TOWER_SCREEN)) {
            TowerInfoScreen infoScreen = (TowerInfoScreen) nuiManager.pushScreen(DefenceUris.TOWER_SCREEN);
            infoScreen.setUpgradingSystem(upgradingSystem);
            EntityRef tower = component.getTowerEntity();
            infoScreen.setTower(tower);
            screenJustOpened = true;
        }
    }

    /**
     * Called when the frob button is pressed. Used to close the tower screen if it is open.
     * <p>
     * Filters on {@link CharacterComponent}
     *
     * @see FrobButton
     */
    @ReceiveEvent(priority = EventPriority.PRIORITY_LOW, components = CharacterComponent.class)
    public void onFrobButton(FrobButton event, EntityRef entity) {
        if (event.getState() == ButtonState.UP
                && nuiManager.isOpen(DefenceUris.TOWER_SCREEN)) {
            if (screenJustOpened) {
                screenJustOpened = false;
            } else {
                nuiManager.closeScreen(DefenceUris.TOWER_SCREEN);
            }
        }
    }
}
