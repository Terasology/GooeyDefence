// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.ui;

import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.gooeyDefence.DefenceField;
import org.terasology.gooeyDefence.DefenceUris;
import org.terasology.gooeyDefence.components.ShrineComponent;
import org.terasology.gooeyDefence.events.OnFieldActivated;
import org.terasology.gooeyDefence.events.OnFieldReset;
import org.terasology.gooeyDefence.health.events.EntityDeathEvent;
import org.terasology.nui.WidgetUtil;
import org.terasology.registry.In;
import org.terasology.rendering.nui.NUIManager;
import org.terasology.rendering.nui.layers.ingame.DeathScreen;

/**
 * Handles overwriting the death screen to allow for resetting and to disable information shown and the respawn option.
 *
 * @see DeathScreen
 */
@RegisterSystem
public class DeathScreenSystem extends BaseComponentSystem {
    @In
    private NUIManager nuiManager;
    @In
    private Time time;

    /**
     * Used to display the death screen, and apply modifications to it.
     * <p>
     * Sent when an entity dies
     * Filters on {@link ShrineComponent}
     *
     * @see EntityDeathEvent
     */
    @ReceiveEvent(components = ShrineComponent.class)
    public void onEntityDeath(EntityDeathEvent event, EntityRef entity) {
        if (!nuiManager.isOpen(DefenceUris.DEATH_SCREEN)) {
            time.setPaused(true);
            DeathScreen deathScreen = nuiManager.pushScreen(DefenceUris.DEATH_SCREEN, DeathScreen.class);
            WidgetUtil.trySubscribe(deathScreen, "retry", widget -> triggerReset());
        }
    }

    /**
     * Triggers the resetting of the field.
     */
    private void triggerReset() {
        DefenceField.fieldActivated = false;
        OnFieldReset event = new OnFieldReset(this::doActivation);
        event.beginTask();
        DefenceField.getShrineEntity().send(event);
        event.finishTask();
    }

    /**
     * Triggers the activation of the required game systems.
     */
    private void doActivation() {
        OnFieldActivated activateEvent = new OnFieldActivated(this::finishReset);
        activateEvent.beginTask();
        DefenceField.getShrineEntity().send(activateEvent);
        activateEvent.finishTask();
    }

    /**
     * Called when the systems have finished resetting & activating.
     * Un-pauses the game and closes the screen
     */
    private void finishReset() {
        DefenceField.fieldActivated = true;
        time.setPaused(false);
        nuiManager.closeScreen(DefenceUris.DEATH_SCREEN);
    }

}
