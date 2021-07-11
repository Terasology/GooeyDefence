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

import org.joml.Vector3f;
import org.joml.Vector3i;
import org.terasology.gooeyDefence.movement.PathfindingManager;

import java.util.List;

/**
 * Moves the enemy along a path from an entrance to the shrine.
 * <p>
 * Doesn't store the path internally to reduce on memory, instead stores a
 * reference to the PathfindingManager that holds the path.
 * This does result in needing to re-set the pathManager every time the game is loaded/created.
 *
 * @see PathfindingManager
 * @see CustomPathComponent
 * @see PathComponent
 */
public class EntrancePathComponent implements PathComponent<EntrancePathComponent> {
    private int step;
    private int entranceId;
    private Vector3f goal = new Vector3f();
    private transient PathfindingManager pathManager;

    /**
     * Empty constructor for deserialisation.
     */
    private EntrancePathComponent() {
    }

    /**
     * Create a new entrance path component specifying the position along the path to start at.
     *
     * @param entranceId  The ID of the entrance
     * @param pathManager The PathfindingManager the path is stored in
     * @param startStep   The step to start from. This must be a valid index in the path.
     */
    public EntrancePathComponent(int entranceId, PathfindingManager pathManager, int startStep) {
        this.entranceId = entranceId;
        this.pathManager = pathManager;
        /* The startStep given must be in the range of the path */
        if (startStep < 0 || startStep > pathManager.getPath(entranceId).size() - 1) {
            throw new IllegalArgumentException();
        }
        step = startStep;
        goal = new Vector3f(pathManager.getPath(entranceId).get(step));
    }

    public EntrancePathComponent(int entranceId, PathfindingManager pathManager) {
        this.entranceId = entranceId;
        this.pathManager = pathManager;
        step = pathManager.getPath(entranceId).size() - 1;
        goal = new Vector3f(pathManager.getPath(entranceId).get(step));
    }

    /**
     * Set the PathfindingManager the paths are stored in.
     * The field storing it cannot be serialised so it must be manually set.
     *
     * @param pathManager The new path manager to set
     */
    public void setPathManager(PathfindingManager pathManager) {
        this.pathManager = pathManager;
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
        List<Vector3i> path = pathManager.getPath(entranceId);
        step--;
        step = Math.min(Math.max(0, step), path.size() - 1);
        goal = new Vector3f(path.get(step));

    }

    /**
     * @return the id of the entrance path this component is following.
     */
    public int getEntranceId() {
        return entranceId;
    }

    @Override
    public void copy(EntrancePathComponent other) {
        this.step = other.step;
        this.entranceId = other.entranceId;
        this.goal.set(other.goal);
        this.pathManager = other.pathManager;
    }
}
