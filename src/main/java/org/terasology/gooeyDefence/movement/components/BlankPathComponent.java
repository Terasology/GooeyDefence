// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.movement.components;


import org.joml.Vector3f;

/**
 * Keeps the enemy at a specific location.
 * Used to pause it when a path is being calculated.
 *
 * @see PathComponent
 */
public class BlankPathComponent implements PathComponent<BlankPathComponent> {
    private Vector3f position = new Vector3f();

    /**
     * Empty constructor for deserialisation.
     */
    private BlankPathComponent() {

    }

    public BlankPathComponent(Vector3f position) {
        this.position.set(position);
    }


    @Override
    public int getStep() {
        return 0;
    }

    @Override
    public Vector3f getGoal() {
        return position;
    }

    @Override
    public void nextStep() {

    }

    @Override
    public boolean atEnd() {
        return false;
    }

    @Override
    public void copyFrom(BlankPathComponent other) {
        this.position.set(other.position);
    }
}
