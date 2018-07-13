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
import org.terasology.gooeyDefence.TowerManager;
import org.terasology.gooeyDefence.components.towers.TowerComponent;
import org.terasology.gooeyDefence.upgrading.UpgradingSystem;
import org.terasology.logic.common.DisplayNameComponent;
import org.terasology.math.geom.Vector2i;
import org.terasology.rendering.nui.Canvas;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.rendering.nui.databinding.ReadOnlyBinding;
import org.terasology.rendering.nui.itemRendering.StringTextRenderer;
import org.terasology.rendering.nui.layouts.relative.RelativeLayout;
import org.terasology.rendering.nui.widgets.UILabel;
import org.terasology.rendering.nui.widgets.UIList;

import java.util.ArrayList;
import java.util.List;

/**
 * Screen for displaying a myriad of stats and options about a tower.
 */
public class TowerInfoScreen extends CoreScreenLayer {
    private static final Logger logger = LoggerFactory.getLogger(TowerInfoScreen.class);
    private static final String WHITE_TEXT = "whiteText";
    private static final String RED_TEXT = "redText";

    private UIUpgrader blockUpgrades;

    private RelativeLayout blockInfoPanel;
    private UILabel blockName;
    private UILabel blockDescription;

    private UILabel powerProduction;
    private UILabel powerUsage;

    private UIList<EntityRef> coreList;
    private UIList<EntityRef> effectorList;
    private UIList<EntityRef> targeterList;

    private EntityRef blockEntity = EntityRef.NULL;
    private EntityRef towerEntity = EntityRef.NULL;

    private ReadOnlyBinding<Boolean> generalVisibleBinding = new ReadOnlyBinding<Boolean>() {
        @Override
        public Boolean get() {
            return blockEntity != EntityRef.NULL;
        }
    };
    private StringTextRenderer<EntityRef> entityToStringRenderer = new StringTextRenderer<EntityRef>() {
        @Override
        public String getString(EntityRef entity) {
            DisplayNameComponent nameComponent = entity.getComponent(DisplayNameComponent.class);
            return nameComponent != null ? nameComponent.name : entity.getParentPrefab().getName();
        }
    };

    @Override
    public void initialise() {
        findAllWidgets();

        bindGeneralWidgets();
        bindGeneralInfoWigets();
        bindEffectorWidgets();
        bindTargeterWidgets();
        bindCoreWidgets();
    }


    /**
     * Called when the screen is closed to clear the widgets and selections
     */
    @Override
    public void onClosed() {
        blockEntity = EntityRef.NULL;
        towerEntity = EntityRef.NULL;

        effectorList.setSelection(null);
        targeterList.setSelection(null);
        super.onClosed();
    }

    @Override
    public Vector2i getPreferredContentSize(Canvas canvas, Vector2i areaHint) {
        return areaHint;
    }

    /**
     * `{@link #find(String, Class)}'s all the widgets needed for this screen.
     */
    private void findAllWidgets() {

        blockUpgrades = find("blockUpgrades", UIUpgrader.class);

        blockInfoPanel = find("blockInfoPanel", RelativeLayout.class);
        blockName = find("blockName", UILabel.class);
        blockDescription = find("blockDescription", UILabel.class);

        powerProduction = find("powerProduction", UILabel.class);
        powerUsage = find("powerUsage", UILabel.class);

        coreList = find("coreList", UIList.class);
        effectorList = find("effectorList", UIList.class);
        targeterList = find("targeterList", UIList.class);
    }

    /**
     * Setup all the binding for the general block info widgets
     */
    private void bindGeneralWidgets() {
        blockInfoPanel.bindVisible(generalVisibleBinding);

        blockUpgrades.bindEntity(new ReadOnlyBinding<EntityRef>() {
            @Override
            public EntityRef get() {
                return blockEntity;
            }
        });
        blockName.bindText(new ReadOnlyBinding<String>() {
            @Override
            public String get() {
                return entityToStringRenderer.getString(blockEntity);
            }
        });
        blockDescription.bindText(new ReadOnlyBinding<String>() {
            @Override
            public String get() {
                DisplayNameComponent displayComponent = blockEntity.getComponent(DisplayNameComponent.class);
                return displayComponent != null ? displayComponent.description : "";
            }
        });
    }

    /**
     * Setup all the bindings for the general tower info widgets.
     */
    private void bindGeneralInfoWigets() {
        powerProduction.bindText(new ReadOnlyBinding<String>() {
            @Override
            public String get() {
                return String.valueOf(
                        TowerManager.getTotalCorePower(
                                towerEntity.getComponent(TowerComponent.class)));
            }
        });
        powerUsage.bindText(new ReadOnlyBinding<String>() {
            @Override
            public String get() {
                TowerComponent towerComponent = towerEntity.getComponent(TowerComponent.class);
                return String.valueOf(
                        TowerManager.getEffectorDrain(towerComponent) +
                                TowerManager.getTargeterDrain(towerComponent));
            }
        });

        powerProduction.bindFamily(new ReadOnlyBinding<String>() {
            @Override
            public String get() {
                TowerComponent towerComponent = towerEntity.getComponent(TowerComponent.class);
                return towerComponent == null || TowerManager.hasEnoughPower(towerComponent) ? WHITE_TEXT : RED_TEXT;
            }
        });
    }

    /**
     * Setup the bindings for the core specific widgets
     */
    private void bindCoreWidgets() {
        subscribeSelection(coreList, targeterList, effectorList);
        coreList.setItemRenderer(entityToStringRenderer);
        coreList.bindList(new ReadOnlyBinding<List<EntityRef>>() {
            @Override
            public List<EntityRef> get() {
                if (towerEntity == EntityRef.NULL) {
                    return new ArrayList<>();
                } else {
                    TowerComponent towerComponent = towerEntity.getComponent(TowerComponent.class);
                    return new ArrayList<>(towerComponent.cores);
                }
            }
        });
    }

    /**
     * Set up all the bindings for all the effector specific widgets
     */
    private void bindEffectorWidgets() {
        subscribeSelection(effectorList, targeterList, coreList);
        effectorList.setItemRenderer(entityToStringRenderer);
        effectorList.bindList(new ReadOnlyBinding<List<EntityRef>>() {
            @Override
            public List<EntityRef> get() {
                if (towerEntity == EntityRef.NULL) {
                    return new ArrayList<>();
                } else {
                    TowerComponent towerComponent = towerEntity.getComponent(TowerComponent.class);
                    return new ArrayList<>(towerComponent.effector);
                }
            }
        });
    }

    /**
     * Setup all the binding for the targeter widgets
     */
    private void bindTargeterWidgets() {
        subscribeSelection(targeterList, effectorList, coreList);
        targeterList.setItemRenderer(entityToStringRenderer);
        targeterList.bindList(new ReadOnlyBinding<List<EntityRef>>() {
            @Override
            public List<EntityRef> get() {
                if (towerEntity == EntityRef.NULL) {
                    return new ArrayList<>();
                } else {
                    TowerComponent towerComponent = towerEntity.getComponent(TowerComponent.class);
                    return new ArrayList<>(towerComponent.targeter);
                }
            }
        });
    }

    /**
     * Subscribes a block list widget to being clicked.
     *
     * @param listWidget The widget to subscribe
     * @param otherOne   One of the other list widgets
     * @param otherTwo   One of the other list widgets
     */
    private void subscribeSelection(UIList<EntityRef> listWidget, UIList<EntityRef> otherOne, UIList<EntityRef> otherTwo) {
        listWidget.subscribeSelection((widget, item) -> {
            if (item != null) {
                blockUpgrades.clearUpgrade();
                otherOne.setSelection(null);
                otherTwo.setSelection(null);
                towerBlockSelected(item);
            }
        });
    }


    /**
     * Set the tower to display.
     * Handles refreshing all the widgets and info.
     *
     * @param tower The new tower to set
     */
    public void setTower(EntityRef tower) {
        towerEntity = tower;
    }


    /**
     * Subscriber called when a button on the list of effectors is pushed.
     * Sets the current effector and nulls the current targeter
     */
    private void towerBlockSelected(EntityRef entity) {
        if (entity != null) {
            blockEntity = entity;
        }
    }


    /**
     * Sets the upgrader system used to calculate values for the children
     *
     * @param newSystem The upgrader system to set.
     */
    public void setUpgradingSystem(UpgradingSystem newSystem) {
        blockUpgrades.setUpgradingSystem(newSystem);
    }
}


