// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.movement.components;

import org.terasology.engine.entitySystem.Component;
import org.terasology.gooeyDefence.movement.MovementSystem;
import org.terasology.math.geom.Vector3f;

/**
 * Stores information on the speed, and goal to move an entity towards.
 *
 * @see MovementSystem
 */
public class MovementComponent implements Component {
    public float speed;
    public Vector3f goal = Vector3f.zero();
    public float reachedDistance = 0.1f;


}
