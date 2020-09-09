// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.movement.components;

import org.terasology.engine.entitySystem.Component;
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
     * Check if the enemy is at the end of the path. Step zero must indicate the end.
     *
     * @return If the enemy is at step zero.
     */
    default boolean atEnd() {
        return getStep() == 0;
    }

}
