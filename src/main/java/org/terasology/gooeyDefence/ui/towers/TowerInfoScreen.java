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
import org.terasology.gooeyDefence.upgrading.UpgradeInfo;
import org.terasology.gooeyDefence.upgrading.UpgradingSystem;
import org.terasology.logic.common.DisplayNameComponent;
import org.terasology.math.geom.Vector2i;
import org.terasology.rendering.nui.Canvas;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.rendering.nui.databinding.ReadOnlyBinding;
import org.terasology.rendering.nui.layouts.ColumnLayout;
import org.terasology.rendering.nui.layouts.relative.RelativeLayout;
import org.terasology.rendering.nui.widgets.UIButton;
import org.terasology.rendering.nui.widgets.UILabel;

/**
 * Screen for displaying a myriad of stats and options about a tower.
 */
public class TowerInfoScreen extends CoreScreenLayer {
    private static final Logger logger = LoggerFactory.getLogger(TowerInfoScreen.class);

    private UIUpgrader upgrading;

    private UILabel blockName;
    private UILabel blockDescription;

    private ColumnLayout effectorList;
    private RelativeLayout effectorLayout;

    private ColumnLayout targeterList;
    private RelativeLayout targeterLayout;

    private boolean isEffectorSelected = false;
    private boolean isTargeterSelected = false;
    private EntityRef currentEntity = EntityRef.NULL;
    private UpgradingSystem upgradingSystem;

    private ReadOnlyBinding<Boolean> generalVisibleBinding = new ReadOnlyBinding<Boolean>() {
        @Override
        public Boolean get() {
            return isEffectorSelected || isTargeterSelected;
        }
    };
    private ReadOnlyBinding<Boolean> effectorVisibleBinding = new ReadOnlyBinding<Boolean>() {
        @Override
        public Boolean get() {
            return isEffectorSelected;
        }
    };
    private ReadOnlyBinding<Boolean> targeterVisibleBinding = new ReadOnlyBinding<Boolean>() {
        @Override
        public Boolean get() {
            return isTargeterSelected;
        }
    };

    @Override
    public void initialise() {
        upgrading = find("upgrading", UIUpgrader.class);
        blockName = find("blockName", UILabel.class);
        blockDescription = find("blockDescription", UILabel.class);

        effectorList = find("effectorList", ColumnLayout.class);
        effectorLayout = find("effectorLayout", RelativeLayout.class);

        targeterList = find("targeterList", ColumnLayout.class);
        targeterLayout = find("targeterLayout", RelativeLayout.class);


        bindGeneralWidgets();
        bindEffectorWidgets();
        bindTargeterWidgets();
    }

    /**
     * Called when the screen is closed to clear the widgets and selections
     */
    @Override
    public void onClosed() {
        isEffectorSelected = false;
        isTargeterSelected = false;
        currentEntity = EntityRef.NULL;
        effectorList.removeAllWidgets();
        targeterList.removeAllWidgets();
        super.onClosed();
    }

    @Override
    public Vector2i getPreferredContentSize(Canvas canvas, Vector2i areaHint) {
        return areaHint;
    }

    /**
     * Setup all the binding for the general block info widgets
     */
    private void bindGeneralWidgets() {
        blockName.bindVisible(generalVisibleBinding);
        blockDescription.bindVisible(generalVisibleBinding);
        upgrading.bindVisible(generalVisibleBinding);

        upgrading.bindEntity(new ReadOnlyBinding<EntityRef>() {
            @Override
            public EntityRef get() {
                return currentEntity;
            }
        });
        blockName.bindText(new ReadOnlyBinding<String>() {
            @Override
            public String get() {
                if (currentEntity == EntityRef.NULL) {
                    return "";
                }
                if (currentEntity.hasComponent(DisplayNameComponent.class)) {
                    return currentEntity.getComponent(DisplayNameComponent.class).name;
                } else {
                    return currentEntity.getParentPrefab().getName();
                }
            }
        });
        blockDescription.bindText(new ReadOnlyBinding<String>() {
            @Override
            public String get() {
                DisplayNameComponent displayComponent = currentEntity.getComponent(DisplayNameComponent.class);
                return displayComponent != null ? displayComponent.description : "";
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
        for (EntityRef effector : tower.effector) {
            TowerEffector effectorComponent = DefenceField.getComponentExtending(effector, TowerEffector.class);
            UIButton button = new UIButton();
            button.setText(effectorComponent.getClass().getSimpleName());
            button.subscribe((widget) -> towerBlockSelected(effector, false));
            effectorList.addWidget(button);
        }
        for (EntityRef targeter : tower.targeter) {
            TowerTargeter targeterComponent = DefenceField.getComponentExtending(targeter, TowerTargeter.class);
            UIButton button = new UIButton();
            button.setText(targeterComponent.getClass().getSimpleName());
            button.subscribe((widget) -> towerBlockSelected(targeter, true));
            targeterList.addWidget(button);
        }
    }


    /**
     * Subscriber called when a button on the list of effectors is pushed.
     * Sets the current effector and nulls the current targeter
     */
    private void towerBlockSelected(EntityRef entity, boolean isBlockTargeter) {
        currentEntity = entity;
        isTargeterSelected = isBlockTargeter;
        isEffectorSelected = !isBlockTargeter;
    }


    /**
     * Sets the upgrader system used to calculate values for the children
     *
     * @param newSystem The upgrader system to set.
     */
    public void setUpgradingSystem(UpgradingSystem newSystem) {
        upgrading.setUpgradingSystem(newSystem);
        upgradingSystem = newSystem;
    }
}


