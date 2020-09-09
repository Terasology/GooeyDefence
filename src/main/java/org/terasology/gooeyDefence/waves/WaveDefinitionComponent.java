// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.waves;

import org.terasology.engine.entitySystem.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Component which allows for easy definition of the waves on prefabs
 *
 * @see WaveInfo
 */
public class WaveDefinitionComponent implements Component {
    /**
     * The waves to load into the wave generator
     */
    public List<WaveInfo> waves = new ArrayList<>();

}
