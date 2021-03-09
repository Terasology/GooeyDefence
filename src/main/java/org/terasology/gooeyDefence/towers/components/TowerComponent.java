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
package org.terasology.gooeyDefence.towers.components;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.gooeyDefence.towers.TowerManager;

import java.util.HashSet;
import java.util.Set;

/**
 * Component for the abstract tower entity.
 * Stores the IDs of all the blocks that make up the tower.
 * <p>
 * Only collates together the component parts of the tower. Functionality is
 * provided by the {@link TowerCore}, {@link TowerTargeter} or {@link TowerEffector} components.
 *
 * @see TowerManager
 */
public class TowerComponent implements Component {
    public Set<EntityRef> cores = new HashSet<>();
    public Set<EntityRef> effector = new HashSet<>();
    public Set<EntityRef> targeter = new HashSet<>();
    public Set<EntityRef> plains = new HashSet<>();
}
