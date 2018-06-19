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

import org.terasology.gooeyDefence.PathfindingSystem;
import org.terasology.math.geom.Vector3i;

import java.util.List;

/**
 * Moves the enemy along a path from an entrance to the shrine.
 * <p>
 * Doesn't store the path internally to reduce on memory, instead stores a
 * reference to the PathfindingSystem that holds the path.
 *
 * @see PathfindingSystem
 */
public class EntrancePathComponent implements PathComponent {
    private int step;
    private int entranceID;
    private Vector3i goal;
    private PathfindingSystem pathManager;

    /**
     * empty constructor for deserialisation
     */
    private EntrancePathComponent() {
    }

    /**
     * Create a new entrance path component specifying the position along the path to start at.
     *
     * @param entranceID  The ID of the entrance
     * @param pathManager The PathfindingSystem the path is stored in
     * @param startStep   The step to start from.
     */
    public EntrancePathComponent(int entranceID, PathfindingSystem pathManager, int startStep) {
        this.entranceID = entranceID;
        this.pathManager = pathManager;
        step = Math.max(Math.min(pathManager.getPath(entranceID).size() - 1, startStep), 0);
        goal = pathManager.getPath(entranceID).get(step);
    }

    public EntrancePathComponent(int entranceID, PathfindingSystem pathManager) {
        this.entranceID = entranceID;
        this.pathManager = pathManager;
        step = pathManager.getPath(entranceID).size() - 1;
        goal = pathManager.getPath(entranceID).get(step);
    }

    /**
     * Set the PathfindingSystem the paths are stored in.
     * Used as the field cannot be serialised.
     *
     * @param pathManager The new path manager to set
     */
    public void setPathManager(PathfindingSystem pathManager) {
        this.pathManager = pathManager;
    }

    @Override
    public int getStep() {
        return step;
    }

    @Override
    public Vector3i getGoal() {
        return goal;
    }

    @Override
    public void nextStep() {
        List<Vector3i> path = pathManager.getPath(entranceID);
        step--;
        step = Math.min(Math.max(0, step), path.size() - 1);
        goal = path.get(step);

    }

    /**
     * @return the ID of the entrance path this component is following.
     */
    public int getEntranceID() {
        return entranceID;
    }
}
