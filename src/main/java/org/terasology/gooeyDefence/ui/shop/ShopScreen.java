// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.ui.shop;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.logic.common.DisplayNameComponent;
import org.terasology.engine.logic.inventory.ItemComponent;
import org.terasology.engine.logic.players.LocalPlayer;
import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.assets.texture.Texture;
import org.terasology.engine.rendering.nui.CoreScreenLayer;
import org.terasology.rendering.nui.layers.ingame.inventory.InventoryGrid;
import org.terasology.rendering.nui.layers.ingame.inventory.InventoryScreen;
import org.terasology.rendering.nui.layers.ingame.inventory.ItemIcon;
import org.terasology.engine.utilities.Assets;
import org.terasology.engine.world.block.Block;
import org.terasology.gooeyDefence.economy.ShopManager;
import org.terasology.nui.WidgetUtil;
import org.terasology.nui.databinding.ReadOnlyBinding;
import org.terasology.nui.layouts.FlowLayout;
import org.terasology.nui.layouts.relative.RelativeLayout;
import org.terasology.nui.widgets.TooltipLine;
import org.terasology.nui.widgets.UILabel;

import java.util.Collections;
import java.util.Set;

/**
 * Screen that displays both the shop interface and the player inventory simultaneously.
 * <p>
 * Works by overriding the inventory screen on the asset level.
 * This allows it to utilise the inventory opening logic & to be displayed concurrently.
 *
 * @see InventoryScreen
 */
public class ShopScreen extends CoreScreenLayer {

    private final Texture texture = Assets.getTexture("engine:terrain")
            .orElseGet(() -> Assets.getTexture("engine:default").get());
    private FlowLayout wareList;
    private UILabel wareName;
    private ItemIcon wareDisplay;
    private UILabel wareDescription;
    private UILabel wareCost;
    private Block selectedBlock;
    private Prefab selectedPrefab;
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


        WidgetUtil.trySubscribe(this, "buyButton", widget -> attemptItemPurchase());

        /* No null check is performed, as if a value is null then something has gone wrong and we should crash anyway */
        wareInfoLayout.bindVisible(new ReadOnlyBinding<Boolean>() {
            @Override
            public Boolean get() {
                return selectedPrefab != null || selectedBlock != null;
            }
        });
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
