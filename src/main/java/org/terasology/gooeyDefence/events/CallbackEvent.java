// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.events;

/**
 * An event that allows for asynchronous tasks to be tracked. When all tasks are finished, it will run the callback.
 */
public abstract class CallbackEvent {
    private Runnable callback;
    private int tasks;

    /**
     * Plain constructor for serialisation and networking.
     */
    public CallbackEvent() {

    }

    /**
     * Creates a new event with the specified callback
     *
     * @param callback The callback to use
     */
    public CallbackEvent(Runnable callback) {
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
