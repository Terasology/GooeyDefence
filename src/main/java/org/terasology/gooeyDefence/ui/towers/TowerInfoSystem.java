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

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.gooeyDefence.components.towers.TowerComponent;
import org.terasology.gooeyDefence.components.towers.TowerMultiBlockComponent;
import org.terasology.gooeyDefence.upgrading.UpgradingSystem;
import org.terasology.logic.common.ActivateEvent;
import org.terasology.registry.In;
import org.terasology.rendering.nui.NUIManager;

/**
 * Handles setting up and showing the tower screen when a tower is interacted with
 *
 * @see TowerInfoScreen
 */
@RegisterSystem
public class TowerInfoSystem extends BaseComponentSystem {

    @In
    private NUIManager nuiManager;
    @In
    private UpgradingSystem upgradingSystem;

    /**
     * Called when a tower block is interacted with
     * <p>
     * Filters on {@link TowerMultiBlockComponent}
     *
     * @see ActivateEvent
     */
    @ReceiveEvent
    public void onActivate(ActivateEvent event, EntityRef entity, TowerMultiBlockComponent component) {
        TowerInfoScreen infoScreen = (TowerInfoScreen) nuiManager.pushScreen("GooeyDefence:TowerInfoScreen");
        infoScreen.setUpgradingSystem(upgradingSystem);
        EntityRef tower = component.getTowerEntity();
        infoScreen.setTower(tower.getComponent(TowerComponent.class));
    }
}
