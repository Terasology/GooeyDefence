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
import org.terasology.rendering.nui.widgets.UIButton;
import org.terasology.rendering.nui.widgets.UILabel;
import org.terasology.utilities.Assets;
import org.terasology.world.block.Block;

import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;

public class ShopScreen extends CoreScreenLayer {
    private static final Logger logger = LoggerFactory.getLogger(ShopScreen.class);

    private FlowLayout wareList;
    private UILabel wareName;
    private UILabel wareDescription;
    private ItemIcon wareDisplay;
    private UIButton buyButton;

    private Block selectedBlock;
    private Prefab selectedPrefab;
    private boolean isWareBlock;

    private Consumer<Block> blockListener = ware -> {
    };
    private Consumer<Prefab> prefabListener = ware -> {
    };

    private Texture texture = Assets.getTexture("engine:terrain")
            .orElseGet(() -> Assets.getTexture("engine:default").get());


    @Override
    public void initialise() {
        wareList = find("wareList", FlowLayout.class);

        wareName = find("wareName", UILabel.class);
        wareDescription = find("wareDescription", UILabel.class);
        wareDisplay = find("wareDisplay", ItemIcon.class);
        buyButton = find("buyButton", UIButton.class);

        wareDisplay.setMeshTexture(texture);
        buyButton.subscribe(widget -> attemptItemPurchase());
    }

    @Override
    public void onClosed() {
        wareList.removeAllWidgets();
        wareName.setText("");
        wareDescription.setText("");

        wareDisplay.setMesh(null);
        wareDisplay.setIcon(null);
    }

    /**
     * Adds a number of items to be displayed in the ware list
     *
     * @param items The items to add
     */
    public void addItems(Set<Prefab> items) {
        for (Prefab item : items) {
            ItemComponent itemComponent = item.getComponent(ItemComponent.class);
            ItemIcon itemIcon = new ItemIcon();

            itemIcon.setIcon(itemComponent.icon);

            UIInteractionWrapper wrapper = new UIInteractionWrapper();
            wrapper.setTooltipLines(Collections.singletonList(new TooltipLine(getPrefabName(item))));
            wrapper.setListener(widget -> handlePrefabSelected(item));
            wrapper.setContent(itemIcon);
            wareList.addWidget(itemIcon, null);
        }
    }

    /**
     * Adds a number of blocks to be displayed in the ware list.
     *
     * @param blocks The block to display
     */
    public void addBlocks(Set<Block> blocks) {
        for (Block block : blocks) {
            ItemIcon itemIcon = new ItemIcon();

            itemIcon.setMesh(block.getMeshGenerator().getStandaloneMesh());
            itemIcon.setMeshTexture(texture);

            UIInteractionWrapper wrapper = new UIInteractionWrapper();
            wrapper.setTooltipLines(Collections.singletonList(new TooltipLine(getBlockName(block))));
            wrapper.setListener(widget -> handleBlockSelected(block));
            wrapper.setContent(itemIcon);
            wareList.addWidget(wrapper, null);
        }
    }

    /**
     * Subscribes a listener to the block purchase
     *
     * @param listener The listener to subscribe
     */
    public void subscribeBlockPurchase(Consumer<Block> listener) {
        blockListener = listener;
    }

    /**
     * Subscribes a listener to the prefab purchase
     *
     * @param listener The listener to subscribe
     */
    public void subscribePrefabPurchase(Consumer<Prefab> listener) {
        prefabListener = listener;
    }

    /**
     * Calls the listeners to attempt to purchase the given item
     */
    private void attemptItemPurchase() {
        if (isWareBlock) {
            blockListener.accept(selectedBlock);
        } else {
            prefabListener.accept(selectedPrefab);
        }
    }

    /**
     * Handles the prefab being selected by setting all the information labels and displays to the correct data
     *
     * @param prefab The block selected
     */
    private void handlePrefabSelected(Prefab prefab) {
        isWareBlock = false;
        if (prefab.hasComponent(DisplayNameComponent.class)) {
            DisplayNameComponent component = prefab.getComponent(DisplayNameComponent.class);
            wareName.setText(component.name);
            wareDescription.setText(component.description);
        } else {
            wareName.setText(prefab.getUrn().getResourceName().toString());
        }

        if (prefab.hasComponent(ItemComponent.class)) {
            ItemComponent itemComponent = prefab.getComponent(ItemComponent.class);
            wareDisplay.setIcon(itemComponent.icon);
        }
    }

    /**
     * Handles the block being selected by setting all the information labels and displays to the correct data
     *
     * @param block The block selected
     */
    private void handleBlockSelected(Block block) {
        if (block.getPrefab().isPresent()) {
            handlePrefabSelected(block.getPrefab().get());
        } else {
            wareName.setText(getBlockName(block));
        }
        isWareBlock = true;
        wareDisplay.setMesh(block.getMeshGenerator().getStandaloneMesh());
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