// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.worldGeneration.facets;

import org.terasology.engine.math.Region3i;
import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.facets.base.BaseBooleanFieldFacet2D;
import org.terasology.gooeyDefence.worldGeneration.providers.RandomFillingProvider;
import org.terasology.gooeyDefence.worldGeneration.rasterizers.RandomFillingRasterizer;

/**
 * Facet that fills the centre of the dome with a random assortment of blocks
 *
 * @see RandomFillingProvider
 * @see RandomFillingRasterizer
 */
public class RandomFillingFacet extends BaseBooleanFieldFacet2D {
    public RandomFillingFacet(Region3i region, Border3D border) {
        super(region, border);
    }
}
