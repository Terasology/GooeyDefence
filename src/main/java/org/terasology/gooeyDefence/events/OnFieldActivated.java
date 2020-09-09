// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.events;

import org.terasology.engine.entitySystem.event.Event;
import org.terasology.gooeyDefence.components.SavedGameFlagComponent;

/**
 * Event sent to initialise the field after a new game or a save has been loaded.
 * <p>
 * This event does not distinguish between a new game, a reset game or a game loaded from a save. {@link
 * SavedGameFlagComponent} allows for checking if a game has been saved & {@link OnFieldReset} is only sent when a field
 * is reset.
 *
 * @see CallbackEvent
 * @see OnFieldReset
 */
public class OnFieldActivated extends CallbackEvent implements Event {
    public OnFieldActivated(Runnable runnable) {
        super(runnable);
    }
}
