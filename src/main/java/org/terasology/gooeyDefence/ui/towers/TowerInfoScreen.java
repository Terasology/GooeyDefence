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
import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.gooeyDefence.DefenceField;
import org.terasology.gooeyDefence.components.towers.TowerComponent;
import org.terasology.gooeyDefence.towerBlocks.base.TowerEffector;
import org.terasology.gooeyDefence.towerBlocks.base.TowerTargeter;
import org.terasology.gooeyDefence.upgrading.UpgradingSystem;
import org.terasology.math.geom.Vector2i;
import org.terasology.rendering.nui.Canvas;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.rendering.nui.databinding.ReadOnlyBinding;
import org.terasology.rendering.nui.layouts.ColumnLayout;
import org.terasology.rendering.nui.layouts.relative.RelativeLayout;
import org.terasology.rendering.nui.widgets.UIButton;
import org.terasology.rendering.nui.widgets.UILabel;

public class TowerInfoScreen extends CoreScreenLayer {
    private static final Logger logger = LoggerFactory.getLogger(TowerInfoScreen.class);

    private ColumnLayout targeterList;
    private ColumnLayout effectorList;


    private UILabel blockName;
    private UIBlockStats blockStats;

    private RelativeLayout effectorLayout;

    private TowerEffector currentEffector = null;
    private UpgradingSystem upgradingSystem;

    private ReadOnlyBinding<Boolean> generalVisibleBinding = new ReadOnlyBinding<Boolean>() {
        @Override
        public Boolean get() {
            return currentEffector != null;
        }
    };
    private ReadOnlyBinding<Boolean> effectorVisibleBinding = new ReadOnlyBinding<Boolean>() {
        @Override
        public Boolean get() {
            return currentEffector != null;
        }
    };

    @Override
    public void initialise() {
        targeterList = find("targeterList", ColumnLayout.class);

        effectorList = find("effectorList", ColumnLayout.class);
        effectorLayout = find("effectorLayout", RelativeLayout.class);

        blockName = find("blockName", UILabel.class);
        blockStats = find("blockStats", UIBlockStats.class);

        bindGeneralWidgets();
        bindEffectorWidgets();
    }

    /**
     * Setup all the binding for the general block info widgets
     */
    private void bindGeneralWidgets() {
        blockName.bindEnabled(generalVisibleBinding);
        blockStats.bindEnabled(generalVisibleBinding);

        blockName.bindText(new ReadOnlyBinding<String>() {
            @Override
            public String get() {
                return currentEffector != null ? currentEffector.getClass().getSimpleName() : "";
            }
        });

        blockStats.bindComponent(new ReadOnlyBinding<Component>() {
            @Override
            public Component get() {
                return currentEffector;
            }
        });
    }

    /**
     * Set up all the bindings for all the effector specific widgets
     */
    private void bindEffectorWidgets() {
        effectorLayout.bindVisible(effectorVisibleBinding);
    }

    /**
     * Set the tower to display.
     * Handles refreshing all the widgets and info.
     *
     * @param tower The new tower to set
     */
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

    /**
     * Subscriber called when a button on the list of effectors is pushed.
     * Sets the current effector and nulls the current targeter
     *
     * @param effector The effector of the pushed button
     */
    private void effectorButtonPressed(TowerEffector effector) {
        currentEffector = effector;
        logger.info("Button for effector " + effector.getClass().getSimpleName() + " was pressed");
    }

    /**
     * Subscriber called when a button on the list of targeters is pushed.
     * Sets the current targeter and nulls the current effector
     *
     * @param targeter The targeter of the pushed button
     */
    private void targeterButtonPressed(TowerTargeter targeter) {
        currentEffector = null;
        logger.info("Button for targeter " + targeter.getClass().getSimpleName() + " was pressed");
    }

    /**
     * Sets the upgrader system used by some of the child widgets.
     * Caches the value to prevent multiple resettings of child components
     *
     * @param newSystem The upgrader system to set.
     */
    public void setUpgradingSystem(UpgradingSystem newSystem) {
        if (upgradingSystem == null) {
            upgradingSystem = newSystem;
            blockStats.setUpgradingSystem(newSystem);
        }
    }

    @Override
    public Vector2i getPreferredContentSize(Canvas canvas, Vector2i areaHint) {
        return areaHint;
    }
}


