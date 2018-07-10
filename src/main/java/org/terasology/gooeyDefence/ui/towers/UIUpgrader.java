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

import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.gooeyDefence.upgrading.BlockUpgradesComponent;
import org.terasology.gooeyDefence.upgrading.UpgradeInfo;
import org.terasology.gooeyDefence.upgrading.UpgradeList;
import org.terasology.gooeyDefence.upgrading.UpgradingSystem;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2i;
import org.terasology.rendering.nui.Canvas;
import org.terasology.rendering.nui.CoreWidget;
import org.terasology.rendering.nui.databinding.Binding;
import org.terasology.rendering.nui.databinding.DefaultBinding;
import org.terasology.rendering.nui.databinding.ReadOnlyBinding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Displays the upgrades on a component and allows for application of them
 */
public class UIUpgrader extends CoreWidget {
    private UIComponentFields componentFields = new UIComponentFields();
    private UIUpgradePaths upgradePaths = new UIUpgradePaths();
    private Binding<EntityRef> entity = new DefaultBinding<>(EntityRef.NULL);
    private UpgradeInfo currentUpgrade = null;
    private UpgradingSystem upgradingSystem;
    private Binding<BlockUpgradesComponent> upgradesComponent = new ReadOnlyBinding<BlockUpgradesComponent>() {
        @Override
        public BlockUpgradesComponent get() {
            return isEnabled() ? entity.get().getComponent(BlockUpgradesComponent.class) : null;
        }
    };

    /**
     * Constructor to setup all the bindings for the component widgets
     */
    public UIUpgrader() {
        componentFields.bindFields(new ReadOnlyBinding<Map<String, String>>() {
            @Override
            public Map<String, String> get() {
                return isEnabled() ? upgradingSystem.getComponentValues(getTargetComponent()) : new HashMap<>();
            }
        });
        componentFields.bindShowUpgrade(new ReadOnlyBinding<Boolean>() {
            @Override
            public Boolean get() {
                return currentUpgrade != null;
            }
        });
        componentFields.bindUpgrade(new ReadOnlyBinding<UpgradeInfo>() {
            @Override
            public UpgradeInfo get() {
                return currentUpgrade;
            }
        });
        upgradePaths.bindUpgradesComponent(new ReadOnlyBinding<BlockUpgradesComponent>() {
            @Override
            public BlockUpgradesComponent get() {
                return entity.get().getComponent(BlockUpgradesComponent.class);
            }
        });
        upgradePaths.subscribe(this::upgradePressed);
    }

    @Override
    public void onDraw(Canvas canvas) {
        Vector2i canvasSize = canvas.size();

        Vector2i fieldsSize = canvas.calculateRestrictedSize(componentFields, canvasSize);
        Vector2i pathsSize = canvas.calculateRestrictedSize(upgradePaths, canvasSize);
        int width = Math.max(fieldsSize.x, pathsSize.x);

        canvas.drawWidget(componentFields, Rect2i.createFromMinAndSize((width - fieldsSize.x) / 2, 0, canvasSize.x, fieldsSize.y));
        canvas.drawWidget(upgradePaths, Rect2i.createFromMinAndSize((width - pathsSize.x) / 2, fieldsSize.y, canvasSize.x, pathsSize.y));
    }

    @Override
    public Vector2i getPreferredContentSize(Canvas canvas, Vector2i sizeHint) {
        Vector2i fieldsSize = canvas.calculateRestrictedSize(componentFields, sizeHint);
        Vector2i pathsSize = canvas.calculateRestrictedSize(upgradePaths, sizeHint);
        return new Vector2i(Math.max(fieldsSize.x, pathsSize.x), fieldsSize.y + pathsSize.x);
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() && upgradingSystem != null;
    }

    /**
     * Called when an upgrade button is pressed.
     * Handles the initial selection and the application of that upgrade.
     *
     * @param upgrade The upgrade path selected
     */
    private void upgradePressed(UpgradeList upgrade) {
        List<UpgradeInfo> stages = upgrade.getStages();
        /* Stages can never be empty because button is disabled if it is */
        UpgradeInfo upgradeInfo = stages.get(0);

        if (currentUpgrade == upgradeInfo) {
            upgradingSystem.applyUpgrade(getTargetComponent(), upgradeInfo);
            stages.remove(0);
            currentUpgrade = stages.isEmpty() ? null : stages.get(0);
        } else {
            currentUpgrade = upgradeInfo;
        }
    }

    /**
     * @return The component to display and apply upgrades to.
     */
    private Component getTargetComponent() {
        return isEnabled() ? upgradingSystem.getComponentToUpgrade(entity.get(), upgradesComponent.get()) : null;
    }

    /**
     * Set the upgrading system used in this widget.
     *
     * @param upgradingSystem The upgrading system to use
     */
    public void setUpgradingSystem(UpgradingSystem upgradingSystem) {
        this.upgradingSystem = upgradingSystem;
    }

    /**
     * Bind which entity to show.
     *
     * @param entityBinding The new binding to apply
     */
    public void bindEntity(Binding<EntityRef> entityBinding) {
        entity = entityBinding;
    }

    /**
     * Bind which upgrade component to use.
     * By default uses the one present on the entity (If it exists)
     *
     * @param upgradesBinding The new binding to apply
     */
    public void bindUpgradesComponent(Binding<BlockUpgradesComponent> upgradesBinding) {
        upgradesComponent = upgradesBinding;
    }
}
