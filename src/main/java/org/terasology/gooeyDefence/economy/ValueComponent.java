// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.economy;

import org.terasology.engine.world.block.items.AddToBlockBasedItem;
import org.terasology.gestalt.entitysystem.component.Component;

/**
 * Indicates that the given entity has a value.
 * Used when an entity is being turned into money, or as backup for {@link PurchasableComponent}
 */
@AddToBlockBasedItem
public class ValueComponent implements Component<ValueComponent> {

    /**
     * How much money one instance of this entity is worth
     */
    public int value;

    @Override
    public void copy(ValueComponent other) {
        this.value = other.value;
    }
}
