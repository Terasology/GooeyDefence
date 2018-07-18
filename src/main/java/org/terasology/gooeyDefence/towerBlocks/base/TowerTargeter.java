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
 * They require power, provided by {@link TowerCore}s
 * <p>
 * Provides a number of common properties.
 *
 * @see TowerCore
 * @see TowerEffector
 */
public abstract class TowerTargeter implements Component {
    /**
     * How much energy this targeter will use
     */
    private int drain;
    /**
     * The range of this targeter
     * given in blocks
     */
    private int range;
    /**
     * The time between attacks for this targeter
     * given in ms
     */
    private int attackSpeed;
    /**
     * The method used to determine which enemy to select within range.
     * Not always applicable
     */
    private SelectionMethod selectionMethod = SelectionMethod.FIRST;
    /**
     * All enemies hit by an effect last attack
     */
    private Set<EntityRef> affectedEnemies = new HashSet<>();
    /**
     * The enemy attacked last attack
     * Not always applicable
     */
    private EntityRef lastTarget = EntityRef.NULL;

    public EntityRef getLastTarget() {
        return lastTarget;
    }

    public void setLastTarget(EntityRef lastTarget) {
        this.lastTarget = lastTarget;
    }

    public int getDrain() {
        return drain;
    }

    public int getRange() {
        return range;
    }

    public int getAttackSpeed() {
        return attackSpeed;
    }

    public Set<EntityRef> getAffectedEnemies() {
        return affectedEnemies;
    }

    public void setAffectedEnemies(Set<EntityRef> affectedEnemies) {
        this.affectedEnemies = affectedEnemies;
    }

    public abstract float getMultiplier();

    public SelectionMethod getSelectionMethod() {
        return selectionMethod;
    }

    public void setSelectionMethod(SelectionMethod selectionMethod) {
        this.selectionMethod = selectionMethod;
    }
}
