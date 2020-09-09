// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.economy;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.world.block.items.AddToBlockBasedItem;

/**
 * Indicates that the given entity has a value. Used when an entity is being turned into money, or as backup for {@link
 * PurchasableComponent}
 */
@AddToBlockBasedItem
public class ValueComponent implements Component {

    /**
     * How much money one instance of this entity is worth
     */
    public int value;
}
