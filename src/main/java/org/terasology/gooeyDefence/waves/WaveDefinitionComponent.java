// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.waves;

import com.google.common.collect.Lists;
import org.terasology.gestalt.entitysystem.component.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Component which allows for easy definition of the waves on prefabs
 * @see WaveInfo
 */
public class WaveDefinitionComponent implements Component<WaveDefinitionComponent> {
    /**
     * The waves to load into the wave generator
     */
    public List<WaveInfo> waves = new ArrayList<>();

    @Override
    public void copy(WaveDefinitionComponent other) {
        this.waves = Lists.newArrayList(other.waves);
    }
}
