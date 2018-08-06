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

import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;

import java.util.List;

/**
 * Moves the enemy along a path stored internally in the component.
 * <p>
 * Used for enemies that don't follow the standard entrance path.
 *
 * @see EntrancePathComponent
 * @see PathComponent
 */
public class CustomPathComponent implements PathComponent {
    private List<Vector3i> path;
    private Vector3f goal;
    private int step;

    /**
     * Empty constructor for deserialisation.
     */
    private CustomPathComponent() {
    }

    public CustomPathComponent(List<Vector3i> path) {
        this.path = path;
        this.step = path.size() - 1;
        this.goal = path.get(step).toVector3f();
    }


    @Override
    public int getStep() {
        return step;
    }

    @Override
    public Vector3f getGoal() {
        return goal;
    }

    @Override
    public void nextStep() {
        step--;
        step = Math.min(Math.max(0, step), path.size() - 1);
        this.goal = path.get(step).toVector3f();
    }

    @Override
    public boolean atEnd() {
        return step == 0;
    }
}
