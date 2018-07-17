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

import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.logic.inventory.ItemComponent;
import org.terasology.rendering.assets.texture.Texture;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.rendering.nui.layers.ingame.inventory.ItemIcon;
import org.terasology.rendering.nui.layouts.FlowLayout;
import org.terasology.utilities.Assets;
import org.terasology.world.block.Block;

import java.util.Set;

public class ShopScreen extends CoreScreenLayer {

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

            itemIcon.setIcon(itemComponent.icon);
            wareList.addWidget(itemIcon, null);
        }
    }

    public void setBlocks(Set<Block> blocks) {
        for (Block block : blocks) {
            ItemIcon itemIcon = new ItemIcon();

            itemIcon.setMesh(block.getMeshGenerator().getStandaloneMesh());
            itemIcon.setMeshTexture(texture);
            wareList.addWidget(itemIcon, null);
        }
    }
}

//            itemIcon.setTooltipLines(Collections.singletonList(new TooltipLine("")));