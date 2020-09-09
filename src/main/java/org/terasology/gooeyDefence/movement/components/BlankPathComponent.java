// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.movement.components;

import org.terasology.math.geom.Vector3f;

/**
 * Keeps the enemy at a specific location. Used to pause it when a path is being calculated.
 *
 * @see PathComponent
 */
public class BlankPathComponent implements PathComponent {
    private Vector3f position;

    /**
     * Empty constructor for deserialisation.
     */
    private BlankPathComponent() {

    }

    public BlankPathComponent(Vector3f position) {
        this.position = position;
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
}
