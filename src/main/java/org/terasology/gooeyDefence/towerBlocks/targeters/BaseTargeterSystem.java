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
package org.terasology.gooeyDefence.towerBlocks.targeters;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.gooeyDefence.DefenceField;
import org.terasology.gooeyDefence.components.enemies.PathComponent;
import org.terasology.gooeyDefence.health.HealthComponent;
import org.terasology.gooeyDefence.towerBlocks.SelectionMethod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 *
 */

public class BaseTargeterSystem extends BaseComponentSystem {



    protected EntityRef getSingleTarget(Set<EntityRef> targets, SelectionMethod selectionMethod) {
        Comparator<EntityRef> comparator;
        switch (selectionMethod) {
            case RANDOM:
                List<EntityRef> listTargets = new ArrayList<>(targets);
                Collections.shuffle(listTargets);
                return listTargets.get(0);
            case WEAK:
                comparator = (first, second) -> {
                    HealthComponent firstComponent = first.getComponent(HealthComponent.class);
                    HealthComponent secondComponent = second.getComponent(HealthComponent.class);
                    return firstComponent.getHealth() - secondComponent.getHealth();
                };
                break;
            case FIRST:
                comparator = (first, second) -> {
                    PathComponent firstComponent = DefenceField.getComponentExtending(first, PathComponent.class);
                    PathComponent secondComponent = DefenceField.getComponentExtending(second, PathComponent.class);
                    return firstComponent.getStep() - secondComponent.getStep();
                };
                break;
            case STRONG:
                comparator = (first, second) -> {
                    HealthComponent firstComponent = first.getComponent(HealthComponent.class);
                    HealthComponent secondComponent = second.getComponent(HealthComponent.class);
                    return secondComponent.getHealth() - firstComponent.getHealth();
                };
                break;
            default:
                throw new EnumConstantNotPresentException(SelectionMethod.class, selectionMethod.toString());
        }
        Optional<EntityRef> chosenEnemy = targets.stream().min(comparator);
        return chosenEnemy.orElse(EntityRef.NULL);
    }
}
