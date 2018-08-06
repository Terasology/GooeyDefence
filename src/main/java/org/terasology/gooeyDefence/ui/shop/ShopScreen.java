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

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.gooeyDefence.economy.ShopManager;
import org.terasology.logic.common.DisplayNameComponent;
import org.terasology.logic.inventory.ItemComponent;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.registry.In;
import org.terasology.rendering.assets.texture.Texture;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.rendering.nui.databinding.ReadOnlyBinding;
import org.terasology.rendering.nui.layers.ingame.inventory.InventoryGrid;
import org.terasology.rendering.nui.layers.ingame.inventory.ItemIcon;
import org.terasology.rendering.nui.layouts.FlowLayout;
import org.terasology.rendering.nui.layouts.relative.RelativeLayout;
import org.terasology.rendering.nui.widgets.TooltipLine;
import org.terasology.rendering.nui.widgets.UIButton;
import org.terasology.rendering.nui.widgets.UILabel;
import org.terasology.utilities.Assets;
import org.terasology.world.block.Block;

import java.util.Collections;
import java.util.Set;

/**
 * Screen that displays both the shop interface and the player inventory simultaneously.
 */
public class ShopScreen extends CoreScreenLayer {

    private FlowLayout wareList;
    private UILabel wareName;
    private ItemIcon wareDisplay;
    private UILabel wareDescription;
    private UILabel wareCost;

    private Block selectedBlock;
    private Prefab selectedPrefab;


    private Texture texture = Assets.getTexture("engine:terrain")
            .orElseGet(() -> Assets.getTexture("engine:default").get());

    @In
    private LocalPlayer localPlayer;
    @In
    private ShopManager shopManager;

    @Override
    public void onOpened() {
        addItems(shopManager.getAllItems());
        addBlocks(shopManager.getAllBlocks());
    }

    @Override
    public void initialise() {
        InventoryGrid inventory = find("inventory", InventoryGrid.class);

        wareList = find("wareList", FlowLayout.class);

        wareName = find("wareName", UILabel.class);
        wareDisplay = find("wareDisplay", ItemIcon.class);
        wareDescription = find("wareDescription", UILabel.class);
        wareCost = find("wareCost", UILabel.class);
        RelativeLayout wareInfoLayout = find("wareInfoLayout", RelativeLayout.class);
        UIButton buyButton = find("buyButton", UIButton.class);


        /* No null check is performed, as if a value is null then something has gone wrong and we should crash anyway */
        wareInfoLayout.bindVisible(new ReadOnlyBinding<Boolean>() {
            @Override
            public Boolean get() {
                return selectedPrefab != null || selectedBlock != null;
            }
        });
        buyButton.subscribe(widget -> attemptItemPurchase());
        inventory.bindTargetEntity(new ReadOnlyBinding<EntityRef>() {
            @Override
            public EntityRef get() {
                return localPlayer.getCharacterEntity();
            }
        });
        inventory.setCellOffset(10);
        wareDisplay.setMeshTexture(texture);
    }

    @Override
    public void onClosed() {
        wareList.removeAllWidgets();
        wareName.setText("");
        wareDescription.setText("");

        wareDisplay.setMesh(null);
        wareDisplay.setIcon(null);
    }

    @Override
    public boolean isModal() {
        return false;
    }

    /**
     * Adds a number of items to be displayed in the ware list
     *
     * @param items The items to add
     */
    private void addItems(Set<Prefab> items) {
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
    private void addBlocks(Set<Block> blocks) {
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
     * Calls on the shop manager to attempt to purchase the selected item.
     */
    private void attemptItemPurchase() {
        if (selectedBlock != null) {
            shopManager.purchase(selectedBlock);
        } else if (selectedPrefab != null) {
            shopManager.purchase(selectedPrefab);
        }
    }

    /**
     * Handles the prefab being selected by setting all the information labels and displays to the correct data
     *
     * @param prefab The block selected
     */
    private void handlePrefabSelected(Prefab prefab) {
        selectedPrefab = prefab;
        selectedBlock = null;

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
        wareCost.setText("Cost: " + ShopManager.getWareCost(prefab));
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

        selectedBlock = block;
        selectedPrefab = null;
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
        return !displayName.equals("Untitled Block")
                ? displayName
                : block.getURI()
                .getBlockFamilyDefinitionUrn()
                .getResourceName()
                .toString();
    }
}
