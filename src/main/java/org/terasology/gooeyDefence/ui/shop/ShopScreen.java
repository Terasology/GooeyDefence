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
package org.terasology.gooeyDefence.ui.shop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.logic.common.DisplayNameComponent;
import org.terasology.logic.inventory.ItemComponent;
import org.terasology.rendering.assets.texture.Texture;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.rendering.nui.layers.ingame.inventory.ItemIcon;
import org.terasology.rendering.nui.layouts.FlowLayout;
import org.terasology.rendering.nui.widgets.TooltipLine;
import org.terasology.utilities.Assets;
import org.terasology.world.block.Block;

import java.util.Collections;
import java.util.Set;

public class ShopScreen extends CoreScreenLayer {
    private static final Logger logger = LoggerFactory.getLogger(ShopScreen.class);

    private FlowLayout wareList;

    private Texture texture = Assets.getTexture("engine:terrain")
            .orElseGet(() -> Assets.getTexture("engine:default").get());

    @Override
    public void initialise() {
        wareList = find("wareList", FlowLayout.class);
    }


    public void setItems(Set<Prefab> items) {
        for (Prefab item : items) {
            ItemComponent itemComponent = item.getComponent(ItemComponent.class);
            ItemIcon itemIcon = new ItemIcon();
            String itemName = getPrefabName(item);

            itemIcon.setIcon(itemComponent.icon);

            UIInteractionWrapper wrapper = new UIInteractionWrapper();
            wrapper.setTooltipLines(Collections.singletonList(new TooltipLine(itemName)));
            wrapper.setListener(widget -> wareClicked(itemName));
            wrapper.setContent(itemIcon);
            wareList.addWidget(itemIcon, null);
        }
    }

    public void setBlocks(Set<Block> blocks) {
        for (Block block : blocks) {
            ItemIcon itemIcon = new ItemIcon();
            String blockName = getBlockName(block);

            itemIcon.setMesh(block.getMeshGenerator().getStandaloneMesh());
            itemIcon.setMeshTexture(texture);

            UIInteractionWrapper wrapper = new UIInteractionWrapper();
            wrapper.setTooltipLines(Collections.singletonList(new TooltipLine(blockName)));
            wrapper.setListener(widget -> wareClicked(blockName));
            wrapper.setContent(itemIcon);
            wareList.addWidget(wrapper, null);
        }
    }

    public void wareClicked(String itemName) {
        logger.info(itemName);
    }


    /**
     * Gets the name of a prefab.
     * This is the human readable variant of it.
     *
     * @param prefab The prefab to get the name of
     * @return The string name of the prefab
     */
    private String getPrefabName(Prefab prefab) {
        return prefab.hasComponent(DisplayNameComponent.class)
                ? prefab.getComponent(DisplayNameComponent.class).name
                : prefab.getUrn().getResourceName().toString();
    }

    /**
     * Gets the name of a block.
     * This is the human readable variant of it.
     *
     * @param block The block to get the name of
     * @return The string name of the block
     */
    private String getBlockName(Block block) {
        String displayName = block.getDisplayName();
        return !displayName.equals("Untitled Block") ?
                displayName :
                block.getURI()
                        .getBlockFamilyDefinitionUrn()
                        .getResourceName()
                        .toString();
    }
}