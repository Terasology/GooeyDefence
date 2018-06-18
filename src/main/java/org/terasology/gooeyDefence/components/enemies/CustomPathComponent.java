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

import org.terasology.math.geom.Vector3f;

import java.util.List;

public class CustomPathComponent implements PathComponent {
    private List<Vector3f> path;
    private int step;

    /**
     * empty constructor for deserialisation
     */
    public CustomPathComponent() {
    }

    public CustomPathComponent(List<Vector3f> path) {
        this.path = path;
        this.step = path.size() - 1;
    }


    @Override
    public int getStep() {
        return step;
    }

    @Override
    public Vector3f getGoal() {
        if (step >= 1) {
            return path.get(step - 1);
        } else {
            return null;
        }
    }

    @Override
    public void nextStep() {
        step = Math.min(Math.max(0, step--), path.size() - 1);
    }

    @Override
    public boolean atEnd() {
        return step == 0;
    }
}
