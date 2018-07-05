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
package org.terasology.gooeyDefence.ui.towers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.gooeyDefence.DefenceField;
import org.terasology.gooeyDefence.components.towers.TowerComponent;
import org.terasology.gooeyDefence.towerBlocks.base.TowerEffector;
import org.terasology.gooeyDefence.towerBlocks.base.TowerTargeter;
import org.terasology.math.geom.Vector2i;
import org.terasology.rendering.nui.Canvas;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.rendering.nui.layouts.ColumnLayout;
import org.terasology.rendering.nui.widgets.UIButton;

public class TowerInfoScreen extends CoreScreenLayer {
    private static final Logger logger = LoggerFactory.getLogger(TowerInfoScreen.class);

    private ColumnLayout targeterList;
    private ColumnLayout effectorList;

    @Override
    public void initialise() {
        targeterList = find("targeterList", ColumnLayout.class);
        effectorList = find("effectorList", ColumnLayout.class);
    }

    public void setTower(TowerComponent tower) {
        effectorList.removeAllWidgets();
        for (EntityRef effector : tower.effector) {
            TowerEffector effectorComponent = DefenceField.getComponentExtending(effector, TowerEffector.class);
            UIButton button = new UIButton();
            button.setText(effectorComponent.getClass().getSimpleName());
            button.subscribe((widget) -> effectorButtonPressed(effectorComponent));
            effectorList.addWidget(button);
        }
        targeterList.removeAllWidgets();
        for (EntityRef targeter : tower.targeter) {
            TowerTargeter targeterComponent = DefenceField.getComponentExtending(targeter, TowerTargeter.class);
            UIButton button = new UIButton();
            button.setText(targeterComponent.getClass().getSimpleName());
            button.subscribe((widget) -> targeterButtonPressed(targeterComponent));
            targeterList.addWidget(button);
        }
    }

    private void effectorButtonPressed(TowerEffector effector) {
        logger.info("Button for effector " + effector.getClass().getSimpleName() + " was pressed");
    }

    private void targeterButtonPressed(TowerTargeter targeter) {
        logger.info("Button for targeter " + targeter.getClass().getSimpleName() + " was pressed");
    }

    @Override
    public Vector2i getPreferredContentSize(Canvas canvas, Vector2i areaHint) {
        return areaHint;
    }
}


