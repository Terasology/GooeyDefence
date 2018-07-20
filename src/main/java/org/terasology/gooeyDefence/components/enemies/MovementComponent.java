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
package org.terasology.gooeyDefence.components.enemies;

import org.terasology.entitySystem.Component;
import org.terasology.math.geom.Vector3i;

/**
 * Stores information on how to move the enemy.
 */
public class MovementComponent implements Component {
    private float speed;
    private Vector3i goal = Vector3i.zero();
    private float reachedDistance = 0.1f;

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }


    public Vector3i getGoal() {
        return goal;
    }

    public void setGoal(Vector3i goal) {
        this.goal = goal;
    }

    public float getReachedDistance() {
        return reachedDistance;
    }

    public void setReachedDistance(float reachedDistance) {
        this.reachedDistance = reachedDistance;
    }
}
