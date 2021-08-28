// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.movement.components;


import com.google.common.collect.Lists;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.List;

/**
 * Moves the enemy along a path stored internally in the component.
 * <p>
 * Used for enemies that don't follow the standard entrance path.
 *
 * @see EntrancePathComponent
 * @see PathComponent
 */
public class CustomPathComponent implements PathComponent<CustomPathComponent> {
    private List<Vector3i> path = Lists.newArrayList();
    private Vector3f goal = new Vector3f();
    private int step;

    /**
     * Empty constructor for deserialisation.
     */
    private CustomPathComponent() {
    }

    public CustomPathComponent(List<Vector3i> path) {
        this.path = path;
        this.step = path.size() - 1;
        this.goal = new Vector3f(path.get(step));
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
        this.goal = new Vector3f(path.get(step));
    }

    @Override
    public boolean atEnd() {
        return step == 0;
    }


    @Override
    public void copyFrom(CustomPathComponent other) {
        this.path = Lists.newArrayList(other.path);
        this.goal.set(other.goal);
        this.step = other.step;
    }
}
