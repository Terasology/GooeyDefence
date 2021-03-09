/*
 * Copyright 2017 MovingBlocks
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
package org.terasology.gooeyDefence.events;

import org.terasology.engine.entitySystem.event.Event;
import org.terasology.gooeyDefence.components.SavedGameFlagComponent;

/**
 * Event sent to initialise the field after a new game or a save has been loaded.
 * <p>
 * This event does not distinguish between a new game, a reset game or a game loaded from a save.
 * {@link SavedGameFlagComponent} allows for checking if a game has been saved & {@link OnFieldReset} is only sent
 * when a field is reset.
 *
 * @see CallbackEvent
 * @see OnFieldReset
 */
public class OnFieldActivated extends CallbackEvent implements Event {
    public OnFieldActivated(Runnable runnable) {
        super(runnable);
    }
}
