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
import org.terasology.gooeyDefence.upgrading.BlockUpgradesComponent;
import org.terasology.gooeyDefence.upgrading.UpgradeInfo;
import org.terasology.gooeyDefence.upgrading.UpgradeList;
import org.terasology.gooeyDefence.upgrading.UpgradingSystem;
import org.terasology.math.geom.Vector2i;
import org.terasology.rendering.nui.Canvas;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.rendering.nui.databinding.ReadOnlyBinding;
import org.terasology.rendering.nui.layouts.ColumnLayout;
import org.terasology.rendering.nui.layouts.relative.RelativeLayout;
import org.terasology.rendering.nui.widgets.UIButton;
import org.terasology.rendering.nui.widgets.UILabel;

import java.util.List;

public class TowerInfoScreen extends CoreScreenLayer {
    private static final Logger logger = LoggerFactory.getLogger(TowerInfoScreen.class);


    private UILabel blockName;
    private UIBlockStats blockStats;
    private UIUpgrades blockUpgrades;

    private ColumnLayout effectorList;
    private RelativeLayout effectorLayout;

    private ColumnLayout targeterList;
    private RelativeLayout targeterLayout;

    private TowerEffector currentEffector = null;
    private TowerTargeter currentTargeter = null;
    private UpgradingSystem upgradingSystem;

    private ReadOnlyBinding<Boolean> generalVisibleBinding = new ReadOnlyBinding<Boolean>() {
        @Override
        public Boolean get() {
            return getSelectedComponent() != null;
        }
    };
    private ReadOnlyBinding<Boolean> effectorVisibleBinding = new ReadOnlyBinding<Boolean>() {
        @Override
        public Boolean get() {
            return currentEffector != null;
        }
    };
    private ReadOnlyBinding<Boolean> targeterVisibleBinding = new ReadOnlyBinding<Boolean>() {
        @Override
        public Boolean get() {
            return currentTargeter != null;
        }
    };

    @Override
    public void initialise() {
        blockName = find("blockName", UILabel.class);
        blockStats = find("blockStats", UIBlockStats.class);
        blockUpgrades = find("blockUpgrades", UIUpgrades.class);

        effectorList = find("effectorList", ColumnLayout.class);
        effectorLayout = find("effectorLayout", RelativeLayout.class);

        targeterList = find("targeterList", ColumnLayout.class);
        targeterLayout = find("targeterLayout", RelativeLayout.class);


        bindGeneralWidgets();
        bindEffectorWidgets();
        bindTargeterWidgets();
    }

    private Component getSelectedComponent() {
        return currentEffector != null ? currentEffector : currentTargeter;
    }

    /**
     * Setup all the binding for the general block info widgets
     */
    private void bindGeneralWidgets() {
        blockName.bindEnabled(generalVisibleBinding);
        blockStats.bindEnabled(generalVisibleBinding);
        blockUpgrades.bindEnabled(generalVisibleBinding);

        blockName.bindText(new ReadOnlyBinding<String>() {
            @Override
            public String get() {
                return getSelectedComponent() != null ? getSelectedComponent().getClass().getSimpleName() : "";
            }
        });

        blockStats.bindComponent(new ReadOnlyBinding<Component>() {
            @Override
            public Component get() {
                return getSelectedComponent();
            }
        });

        blockUpgrades.subscribe(this::upgradePressed);
    }

    /**
     * Set up all the bindings for all the effector specific widgets
     */
    private void bindEffectorWidgets() {
        effectorLayout.bindVisible(effectorVisibleBinding);
    }

    /**
     * Setup all the binding for the targeter widgets
     */
    private void bindTargeterWidgets() {
        targeterLayout.bindVisible(targeterVisibleBinding);
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
            button.subscribe((widget) -> effectorButtonPressed(effectorComponent, effector));
            effectorList.addWidget(button);
        }
        targeterList.removeAllWidgets();
        for (EntityRef targeter : tower.targeter) {
            TowerTargeter targeterComponent = DefenceField.getComponentExtending(targeter, TowerTargeter.class);
            UIButton button = new UIButton();
            button.setText(targeterComponent.getClass().getSimpleName());
            button.subscribe((widget) -> targeterButtonPressed(targeterComponent, targeter));
            targeterList.addWidget(button);
        }
    }


    /**
     * Subscriber called when a button on the list of effectors is pushed.
     * Sets the current effector and nulls the current targeter
     *
     * @param effector The effector of the pushed button
     */
    private void effectorButtonPressed(TowerEffector effector, EntityRef entity) {
        currentTargeter = null;
        currentEffector = effector;

        blockUpgrades.setUpgrades(entity.getComponent(BlockUpgradesComponent.class));
        logger.info("Button for effector " + effector.getClass().getSimpleName() + " was pressed");
    }

    /**
     * Subscriber called when a button on the list of targeters is pushed.
     * Sets the current targeter and nulls the current effector
     *
     * @param targeter The targeter of the pushed button
     */
    private void targeterButtonPressed(TowerTargeter targeter, EntityRef entity) {
        currentEffector = null;
        currentTargeter = targeter;

        blockUpgrades.setUpgrades(entity.getComponent(BlockUpgradesComponent.class));
        logger.info("Button for targeter " + targeter.getClass().getSimpleName() + " was pressed");
    }

    /**
     * Applies the given upgrade to the currently selected component
     *
     * @param upgrade The upgrade to apply
     */
    private void upgradePressed(UpgradeList upgrade) {
        List<UpgradeInfo> stages = upgrade.getStages();
        if (!stages.isEmpty()) {
            upgradingSystem.applyUpgrade(getSelectedComponent(), stages.remove(0));
        }
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


