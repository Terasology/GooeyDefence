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
import org.terasology.gooeyDefence.upgrading.BlockUpgradesComponent;
import org.terasology.gooeyDefence.upgrading.UpgradeList;
import org.terasology.math.geom.Vector2i;
import org.terasology.rendering.nui.Canvas;
import org.terasology.rendering.nui.CoreWidget;
import org.terasology.rendering.nui.layouts.FlowLayout;
import org.terasology.rendering.nui.widgets.UIButton;

public class UIUpgrades extends CoreWidget {
    private static final Logger logger = LoggerFactory.getLogger(UIUpgrades.class);
    private FlowLayout upgrades = new FlowLayout();

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawWidget(upgrades);
    }

    public void setUpgrades(BlockUpgradesComponent upgradesComponent) {
        for (UpgradeList upgradeList : upgradesComponent.getUpgrades()) {
            UIButton upgradeButton = new UIButton(upgradeList.getUpgradeName());
            upgradeButton.subscribe(widget -> upgradeButtonPressed(upgradeList));
            upgrades.addWidget(upgradeButton, null);
        }
    }

    public void upgradeButtonPressed(UpgradeList upgrade) {
        logger.info(upgrade.getUpgradeName());
    }

    @Override
    public Vector2i getPreferredContentSize(Canvas canvas, Vector2i sizeHint) {
        return upgrades.getPreferredContentSize(canvas, sizeHint);
    }
}
