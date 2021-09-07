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
package org.terasology.gooeyDefence.events;

import org.joml.Vector3i;
import org.terasology.gestalt.entitysystem.event.Event;
import org.terasology.gooeyDefence.movement.PathfindingManager;

import java.util.List;

/**
 * Event sent when an entrance path is calculated.
 * This includes the initial calculation upon activating a world.
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
