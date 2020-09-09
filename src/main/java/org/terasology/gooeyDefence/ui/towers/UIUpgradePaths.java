// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.ui.towers;

import org.joml.Vector2i;
import org.terasology.gooeyDefence.upgrading.BlockUpgradesComponent;
import org.terasology.gooeyDefence.upgrading.UpgradeInfo;
import org.terasology.gooeyDefence.upgrading.UpgradeList;
import org.terasology.gooeyDefence.upgrading.UpgradingSystem;
import org.terasology.nui.Canvas;
import org.terasology.nui.CoreWidget;
import org.terasology.nui.databinding.Binding;
import org.terasology.nui.databinding.DefaultBinding;
import org.terasology.nui.databinding.ReadOnlyBinding;
import org.terasology.nui.layouts.FlowLayout;
import org.terasology.nui.widgets.UIButton;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Displays a list of all upgrade paths for the entity. Allows for the option to select an upgrade.
 *
 * @see TowerInfoSystem
 * @see UpgradingSystem
 */
public class UIUpgradePaths extends CoreWidget {
    private final FlowLayout upgrades = new FlowLayout();
    private Consumer<UpgradeList> listener;
    private List<UpgradeList> upgradeLists;
    private Binding<BlockUpgradesComponent> upgradesComponent = new DefaultBinding<>();

    @Override
    public void onDraw(Canvas canvas) {
        if (upgradesComponent.get() != null) {
            List<UpgradeList> newUpgrades = upgradesComponent.get().upgrades;
            if (upgradeLists != newUpgrades) {
                upgradeLists = newUpgrades;
                rebuildUpgradeButtons();
            }
        } else {
            upgradeLists = new ArrayList<>();
            rebuildUpgradeButtons();
        }
        canvas.drawWidget(upgrades);
    }

    @Override
    public Vector2i getPreferredContentSize(Canvas canvas, Vector2i sizeHint) {
        return upgrades.getPreferredContentSize(canvas, sizeHint);
    }

    /**
     * Reset all upgrade buttons and recreates them based on the new upgrade info set
     *
     * @see UpgradeList
     */
    private void rebuildUpgradeButtons() {
        upgrades.removeAllWidgets();
        for (UpgradeList upgradeList : upgradeLists) {
            UIButton upgradeButton = new UIButton();
            upgradeButton.setText(upgradeList.upgradeName);
            upgradeButton.subscribe(widget -> listener.accept(upgradeList));
            upgradeButton.bindEnabled(new ReadOnlyBinding<Boolean>() {
                @Override
                public Boolean get() {
                    return !upgradeList.stages.isEmpty();
                }
            });
            upgrades.addWidget(upgradeButton, null);
        }
    }

    /**
     * Sets the listener to be called when an upgrade path is selected
     *
     * @param newListener The new listener to use
     * @see UpgradeInfo
     */
    public void subscribe(Consumer<UpgradeList> newListener) {
        listener = newListener;
    }

    /**
     * Set a new binding to get the upgrades from
     *
     * @param newComponent The binding to use
     * @see BlockUpgradesComponent
     */
    public void bindUpgradesComponent(Binding<BlockUpgradesComponent> newComponent) {
        this.upgradesComponent = newComponent;
    }
}
