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

import org.terasology.entitySystem.event.Event;

/**
 * Event sent to initialise a field after a new game or a save has been loaded.
 */
public class OnFieldActivated implements Event {
    private Runnable callback;
    private int tasks;

    public OnFieldActivated(Runnable callback) {
        this.callback = callback;
    }

    /**
     * Record that a new task is underway
     */
    public void beginTask() {
        tasks++;
    }

    /**
     * Record that a task has finished. If all tasks are over then run the callback.
     */
    public void finishTask() {
        tasks--;
        if (tasks <= 0) {
            callback.run();
        }
    }
}
