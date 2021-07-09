// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.economy;

import org.terasology.engine.world.block.items.AddToBlockBasedItem;
import org.terasology.gestalt.entitysystem.component.Component;

/**
 * Indicates that the item can be bought and thus will be available in the shop
 */
@AddToBlockBasedItem
public class PurchasableComponent implements Component<PurchasableComponent> {
    /**
     * The cost of buying the item.
     * If left blank on the prefab, the value component will be used (if it exists).
     */
    public int cost = -1;
}
