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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.gooeyDefence.PathfindingSystem;
import org.terasology.math.geom.Vector3i;

import java.util.List;

public class EntrancePathComponent implements PathComponent {
    private static final Logger logger = LoggerFactory.getLogger(EntrancePathComponent.class);
    private int step;
    private int entranceID;
    private Vector3i goal;
    private PathfindingSystem pathManager;

    /**
     * empty constructor for deserialisation
     */
    private EntrancePathComponent() {
    }

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

    @Override
    public boolean atEnd() {
        return step == 0;
    }

    public int getEntranceID() {
        return entranceID;
    }
}
