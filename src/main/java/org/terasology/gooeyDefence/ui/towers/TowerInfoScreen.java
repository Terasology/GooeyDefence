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
import org.terasology.gooeyDefence.TowerManager;
import org.terasology.gooeyDefence.components.towers.TowerComponent;
import org.terasology.gooeyDefence.towerBlocks.SelectionMethod;
import org.terasology.gooeyDefence.towerBlocks.base.TowerTargeter;
import org.terasology.gooeyDefence.upgrading.UpgradingSystem;
import org.terasology.logic.common.DisplayNameComponent;
import org.terasology.math.geom.Vector2i;
import org.terasology.rendering.nui.Canvas;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.rendering.nui.databinding.ReadOnlyBinding;
import org.terasology.rendering.nui.itemRendering.StringTextRenderer;
import org.terasology.rendering.nui.layouts.FlowLayout;
import org.terasology.rendering.nui.layouts.relative.RelativeLayout;
import org.terasology.rendering.nui.widgets.UIButton;
import org.terasology.rendering.nui.widgets.UILabel;
import org.terasology.rendering.nui.widgets.UIList;

import java.util.ArrayList;
import java.util.List;

/**
 * Screen for displaying a myriad of stats and options about a tower.
 */
public class TowerInfoScreen extends CoreScreenLayer {
    private static final Logger logger = LoggerFactory.getLogger(TowerInfoScreen.class);
    /**
     * The family to use to display text as white
     */
    private static final String WHITE_TEXT = "whiteText";
    /**
     * The family to use to display test as red
     */
    private static final String RED_TEXT = "redText";
    /**
     * The ID of the list of cores. Also the value of `blockType` if the block is a core
     */
    private static final String CORE_ID = "coreList";
    /**
     * The ID of the list of targeters. Also the value of `blockType` if the block is a targeter
     */
    private static final String TARGTER_ID = "targeterList";
    /**
     * The ID of the list of effectors. Also the value of `blockType` if the block is an effector
     */
    private static final String EFFECTOR_ID = "effectorList";

    /* Widgets that deal with the general block information */
    private RelativeLayout blockInfoPanel;
    private UILabel blockName;
    private UILabel blockDescription;

    /* Widget that deals with the block stats and upgrading */
    private UIUpgrader blockUpgrades;

    /* Widgets that deal with the options for the block */
    private UIButton[] targetSelectionButtons = new UIButton[4];
    private FlowLayout selectionModeLayout;

    /* Widgets that deal with the general information about the tower */
    private UILabel powerProductionLabel;
    private UILabel powerProduction;
    private UILabel powerUsage;

    /* Widgets that display the list of blocks */
    private UIList<EntityRef> coreList;
    private UIList<EntityRef> effectorList;
    private UIList<EntityRef> targeterList;

    /* Elements of the tower and block selected */
    private TowerComponent towerComponent = null;
    private EntityRef blockEntity = EntityRef.NULL;
    private String blockType = null;

    /* Bindings and other reused anonymous classes */
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
        towerComponent = null;

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

        selectionModeLayout = find("selectionModeLayout", FlowLayout.class);
        targetSelectionButtons[0] = find("firstSelectionButton", UIButton.class);
        targetSelectionButtons[1] = find("weakSelectionButton", UIButton.class);
        targetSelectionButtons[2] = find("strongSelectionButton", UIButton.class);
        targetSelectionButtons[3] = find("randomSelectionButton", UIButton.class);

        powerProductionLabel = find("powerProductionLabel", UILabel.class);
        powerProduction = find("powerProduction", UILabel.class);
        powerUsage = find("powerUsage", UILabel.class);

        coreList = find(CORE_ID, UIList.class);
        effectorList = find(EFFECTOR_ID, UIList.class);
        targeterList = find(TARGTER_ID, UIList.class);
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
                                towerComponent));
            }
        });
        powerUsage.bindText(new ReadOnlyBinding<String>() {
            @Override
            public String get() {
                return String.valueOf(
                        TowerManager.getEffectorDrain(towerComponent) +
                                TowerManager.getTargeterDrain(towerComponent));
            }
        });

        powerProduction.bindFamily(new ReadOnlyBinding<String>() {
            @Override
            public String get() {
                return towerComponent == null || TowerManager.hasEnoughPower(towerComponent) ? WHITE_TEXT : RED_TEXT;
            }
        });

        powerProductionLabel.bindFamily(new ReadOnlyBinding<String>() {
            @Override
            public String get() {
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
                return towerComponent == null ? new ArrayList<>() : new ArrayList<>(towerComponent.cores);
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
                return towerComponent == null ? new ArrayList<>() : new ArrayList<>(towerComponent.effector);
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
                return towerComponent == null ? new ArrayList<>() : new ArrayList<>(towerComponent.targeter);
            }
        });

        selectionModeLayout.bindVisible(new ReadOnlyBinding<Boolean>() {
            @Override
            public Boolean get() {
                return blockType.equals(TARGTER_ID);
            }
        });
        targetSelectionButtons[0].subscribe(widget -> targetingOptionSelected(SelectionMethod.FIRST));
        targetSelectionButtons[1].subscribe(widget -> targetingOptionSelected(SelectionMethod.WEAK));
        targetSelectionButtons[2].subscribe(widget -> targetingOptionSelected(SelectionMethod.STRONG));
        targetSelectionButtons[3].subscribe(widget -> targetingOptionSelected(SelectionMethod.RANDOM));
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
                towerBlockSelected(item, listWidget.getId());
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
        towerComponent = tower.getComponent(TowerComponent.class);
    }

    /**
     * Handles a selection button being pressed and set's the value on the block.
     * The block will always be a targeter because the buttons are hidden otherwise.
     *
     * @param selectionMethod The selection method chosen
     */
    private void targetingOptionSelected(SelectionMethod selectionMethod) {
        TowerTargeter targeter = DefenceField.getComponentExtending(blockEntity, TowerTargeter.class);
        targeter.setSelectionMethod(selectionMethod);
    }


    /**
     * Subscriber called when a button on the list of effectors is pushed.
     * Sets the current effector and nulls the current targeter
     */
    private void towerBlockSelected(EntityRef entity, String listID) {
        if (entity != null) {
            blockEntity = entity;
            blockType = listID;
        }
    }


    /**
     * Sets the upgrader system used to calculate values for the children
     *
     * @param newSystem The upgrader system to set.
     */
    /* package-private */
    void setUpgradingSystem(UpgradingSystem newSystem) {
        blockUpgrades.setUpgradingSystem(newSystem);
    }
}


