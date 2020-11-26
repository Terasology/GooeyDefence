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
package org.terasology.gooeyDefence.movement.components;


import org.joml.Vector3f;

/**
 * Keeps the enemy at a specific location.
 * Used to pause it when a path is being calculated.
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
