// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.components;

import org.terasology.gestalt.entitysystem.component.Component;

/**
 * General purpose catch all component.
 * Intended only to be used temporarily to store fields whilst they haven't got their own component.
 */
public class GooeyComponent implements Component<GooeyComponent> {
    public int damage;

    @Override
    public void copyFrom(GooeyComponent other) {
        this.damage = other.damage;
    }
}
