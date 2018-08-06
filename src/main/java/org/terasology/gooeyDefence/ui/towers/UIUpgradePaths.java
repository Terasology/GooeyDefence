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

import org.terasology.gooeyDefence.upgrading.BlockUpgradesComponent;
import org.terasology.gooeyDefence.upgrading.UpgradeList;
import org.terasology.math.geom.Vector2i;
import org.terasology.rendering.nui.Canvas;
import org.terasology.rendering.nui.CoreWidget;
import org.terasology.rendering.nui.databinding.Binding;
import org.terasology.rendering.nui.databinding.DefaultBinding;
import org.terasology.rendering.nui.databinding.ReadOnlyBinding;
import org.terasology.rendering.nui.layouts.FlowLayout;
import org.terasology.rendering.nui.widgets.UIButton;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class UIUpgradePaths extends CoreWidget {
    private FlowLayout upgrades = new FlowLayout();
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

    public void rebuildUpgradeButtons() {
        upgrades.removeAllWidgets();
        for (UpgradeList upgradeList : upgradeLists) {
            UIButton upgradeButton = new UIButton();
            upgradeButton.setText(upgradeList.upgradeName);
            upgradeButton.subscribe(widget -> upgradeButtonPressed(upgradeList));
            upgradeButton.bindEnabled(new ReadOnlyBinding<Boolean>() {
                @Override
                public Boolean get() {
                    return !upgradeList.stages.isEmpty();
                }
            });
            upgrades.addWidget(upgradeButton, null);
        }
    }

    public void subscribe(Consumer<UpgradeList> newListener) {
        listener = newListener;
    }

    private void upgradeButtonPressed(UpgradeList upgrade) {
        listener.accept(upgrade);
    }

    @Override
    public Vector2i getPreferredContentSize(Canvas canvas, Vector2i sizeHint) {
        return upgrades.getPreferredContentSize(canvas, sizeHint);
    }

    public void bindUpgradesComponent(Binding<BlockUpgradesComponent> newComponent) {
        this.upgradesComponent = newComponent;
    }
}
