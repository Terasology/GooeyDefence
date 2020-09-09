// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.worldGeneration.facets;

import org.terasology.engine.math.Region3i;
import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.facets.base.SparseBooleanFieldFacet3D;
import org.terasology.gooeyDefence.worldGeneration.providers.DefenceFieldProvider;
import org.terasology.gooeyDefence.worldGeneration.rasterizers.DefenceFieldRasterizer;


/**
 * Facet that provides information on where to place the blocks that make up the main outer dome
 *
 * @see DefenceFieldRasterizer
 * @see DefenceFieldProvider
 */
public class DefenceFieldFacet extends SparseBooleanFieldFacet3D {

    public DefenceFieldFacet(Region3i region, Border3D border) {
        super(region, border);
    }

}
