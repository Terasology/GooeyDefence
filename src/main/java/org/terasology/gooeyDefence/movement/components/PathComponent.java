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

import org.terasology.entitySystem.Component;
import org.terasology.math.geom.Vector3f;

/**
 * A component that provides a path for the enemy to follow.
 * <p>
 * Intended to have multiple implementations for different path following situations.
 *
 * @see EntrancePathComponent
 * @see BlankPathComponent
 * @see CustomPathComponent
 */
public interface PathComponent extends Component {

    /**
     * Step zero must be the end of the path.
     *
     * @return The current step of the path
     */
    int getStep();

    /**
     * @return the current block the enemy is moving towards
     */
    Vector3f getGoal();

    /**
     * Advance internal counters to the next step.
     */
    void nextStep();

    /**
     * Check if the enemy is at the end of the path.
     * Step zero must indicate the end.
     *
     * @return If the enemy is at step zero.
     */
    default boolean atEnd() {
        return getStep() == 0;
    }

}
