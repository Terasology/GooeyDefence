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
package org.terasology.gooeyDefence.towerBlocks.base;

import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.gooeyDefence.towerBlocks.SelectionMethod;

import java.util.HashSet;
import java.util.Set;

/**
 * Base class for all the Targeter blocks.
 * <p>
 * Targeters select the enemies the tower will attack.
 * They require power, provided by {@link TowerCore}'s
 *
 * @see TowerCore
 * @see TowerEffector
 */
public abstract class TowerTargeter implements Component {
    private int drain;
    private int range;
    private int attackSpeed;
    private SelectionMethod selectionMethod = SelectionMethod.FIRST;
    private Set<EntityRef> lastTargets = new HashSet<>();

    public int getDrain() {
        return drain;
    }

    public int getRange() {
        return range;
    }

    public int getAttackSpeed() {
        return attackSpeed;
    }

    public Set<EntityRef> getLastTargets() {
        return lastTargets;
    }

    public void setLastTargets(Set<EntityRef> lastTargets) {
        this.lastTargets = lastTargets;
    }

    public abstract float getMultiplier();

    public SelectionMethod getSelectionMethod() {
        return selectionMethod;
    }
}
