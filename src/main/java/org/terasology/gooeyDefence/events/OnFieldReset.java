// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.events;

import org.terasology.engine.entitySystem.event.Event;
import org.terasology.engine.logic.common.ActivateEvent;
import org.terasology.gooeyDefence.ui.DeathScreenSystem;

/**
 * Event sent when the reset option is chosen.
 * <p>
 * Calls on systems to reset their state to new.
 *
 * @see DeathScreenSystem
 * @see CallbackEvent
 * @see ActivateEvent
 */
public class OnFieldReset extends CallbackEvent implements Event {
    public OnFieldReset(Runnable runnable) {
        super(runnable);
    }
}
