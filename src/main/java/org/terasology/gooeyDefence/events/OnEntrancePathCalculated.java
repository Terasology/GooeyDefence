// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.events;

import org.terasology.engine.entitySystem.event.Event;
import org.terasology.gooeyDefence.movement.PathfindingManager;
import org.terasology.math.geom.Vector3i;

import java.util.List;

/**
 * Event sent when an entrance path is calculated. This includes the initial calculation upon activating a world.
 * <p>
 * This event is not sent when other non-entrance paths are changed.
 *
 * @see PathfindingManager
 */
public class OnEntrancePathCalculated implements Event {
    private final int pathId;
    private final List<Vector3i> newPath;

    public OnEntrancePathCalculated(int pathId, List<Vector3i> newPath) {
        this.pathId = pathId;
        this.newPath = newPath;
    }

    /**
     * @return The new path that was changed.
     */
    public List<Vector3i> getNewPath() {
        return newPath;
    }

    /**
     * @return The ID of the path that was changed.
     */
    public int getPathId() {
        return pathId;
    }
}
