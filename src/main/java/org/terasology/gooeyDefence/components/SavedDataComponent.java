/*
 * Copyright 2017 MovingBlocks
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
package org.terasology.gooeyDefence.components;

import org.terasology.entitySystem.Component;
import org.terasology.gooeyDefence.DefenceField;
import org.terasology.gooeyDefence.components.towers.TowerComponent;
import org.terasology.math.geom.Vector3i;
import org.terasology.reflection.MappedContainer;
import org.terasology.world.block.ForceBlockActive;

import java.util.List;
import java.util.Set;

@ForceBlockActive
public class SavedDataComponent implements Component {
    private Set<Set<Vector3i>> towers;
    private List<List<Vector3i>> paths;
    private boolean saved;
    private int health;

    public List<List<Vector3i>> getPaths() {
        return paths;
    }

    public void setPaths(List<List<Vector3i>> paths) {
        this.paths = paths;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int change) {
        health = change;
    }

    public Set<Set<Vector3i>> getTowers() {
        return towers;
    }

    public void setTowers(Set<Set<Vector3i>> towers) {
        this.towers = towers;
    }
}
