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
package org.terasology.gooeyDefence.towers.targeters;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.gooeyDefence.towers.SelectionMethod;
import org.terasology.gooeyDefence.towers.components.TowerTargeter;

/**
 * Targets a single enemy within range.
 *
 * @see SingleTargeterSystem
 * @see TowerTargeter
 */
public class SingleTargeterComponent extends TowerTargeter {
    /**
     * The method used to determine which enemy to select within range.
     * Not always applicable
     */
    public SelectionMethod selectionMethod = SelectionMethod.FIRST;
    /**
     * The enemy attacked last attack
     * Not always applicable
     */
    public EntityRef lastTarget = EntityRef.NULL;

    @Override
    public float getMultiplier() {
        return 1;
    }
}
