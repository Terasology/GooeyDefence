// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.ui.towers;

import org.joml.Vector2i;
import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.gooeyDefence.upgrading.BlockUpgradesComponent;
import org.terasology.gooeyDefence.upgrading.UpgradeInfo;
import org.terasology.gooeyDefence.upgrading.UpgradeList;
import org.terasology.gooeyDefence.upgrading.UpgradingSystem;
import org.terasology.nui.Canvas;
import org.terasology.nui.CoreWidget;
import org.terasology.nui.databinding.Binding;
import org.terasology.nui.databinding.DefaultBinding;
import org.terasology.nui.databinding.ReadOnlyBinding;
import org.terasology.nui.util.RectUtility;

import java.util.List;

/**
 * Displays the upgrades on a component and allows for application of them
 *
 * @see UIComponentFields
 * @see UIUpgrader
 */
public class UIUpgrader extends CoreWidget {
    private final UIComponentFields componentFields = new UIComponentFields();
    private final UIUpgradePaths upgradePaths = new UIUpgradePaths();
    private Binding<EntityRef> entity = new DefaultBinding<>(EntityRef.NULL);
    private UpgradeInfo currentUpgrade;
    private UpgradingSystem upgradingSystem;
    private final Binding<BlockUpgradesComponent> upgradesComponent = new ReadOnlyBinding<BlockUpgradesComponent>() {
        @Override
        public BlockUpgradesComponent get() {
            return isEnabled() ? entity.get().getComponent(BlockUpgradesComponent.class) : null;
        }
    };

    /**
     * Constructor to setup all the bindings for the component widgets
     */
    public UIUpgrader() {

        componentFields.bindFields(new ReadOnlyBinding<List<String>>() {
            @Override
            public List<String> get() {
                return upgradingSystem.getComponentFields(getTargetComponent());
            }
        });
        componentFields.bindValues(new ReadOnlyBinding<List<String>>() {
            @Override
            public List<String> get() {
                return upgradingSystem.getComponentValues(getTargetComponent());
            }
        });
        componentFields.bindUpgrade(new ReadOnlyBinding<List<String>>() {
            @Override
            public List<String> get() {
                return upgradingSystem.getComponentUpgrades(getTargetComponent(), currentUpgrade);
            }
        });
        componentFields.bindShowUpgrade(new ReadOnlyBinding<Boolean>() {
            @Override
            public Boolean get() {
                return currentUpgrade != null;
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

        canvas.drawWidget(componentFields, RectUtility.createFromMinAndSize(0, 0, canvasSize.x, fieldsSize.y));
        canvas.drawWidget(upgradePaths, RectUtility.createFromMinAndSize((width - pathsSize.x) / 2, fieldsSize.y, canvasSize.x, pathsSize.y));
    }

    @Override
    public Vector2i getPreferredContentSize(Canvas canvas, Vector2i sizeHint) {
        Vector2i fieldsSize = canvas.calculateRestrictedSize(componentFields, sizeHint);
        Vector2i pathsSize = canvas.calculateRestrictedSize(upgradePaths, sizeHint);
        return new Vector2i(Math.max(fieldsSize.x, pathsSize.x), fieldsSize.y + pathsSize.y);
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
        List<UpgradeInfo> stages = upgrade.stages;
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
     * Clears the upgrade selected.
     */
    public void clearUpgrade() {
        currentUpgrade = null;
    }
}
