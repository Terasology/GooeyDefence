// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.components;

import org.terasology.engine.world.block.ForceBlockActive;
import org.terasology.gestalt.entitysystem.component.Component;

/**
 * Component for the central shrine entity.
 * Used as a filter or flag component.
 */
@ForceBlockActive
public class ShrineComponent implements Component<ShrineComponent> {
    @Override
    public void copy(ShrineComponent other) {

    }
}
