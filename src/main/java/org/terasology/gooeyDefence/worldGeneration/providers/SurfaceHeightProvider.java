// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.worldGeneration.providers;

import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.FacetProvider;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Produces;
import org.terasology.engine.world.generation.facets.SurfaceHeightFacet;
import org.terasology.gooeyDefence.worldGeneration.rasterizers.WorldSurfaceRasterizer;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.Rect2i;

/**
 * Fills the {@link SurfaceHeightFacet} with a surface height of 0 globally.
 *
 * @see SurfaceHeightFacet
 * @see WorldSurfaceRasterizer
 */
@Produces(SurfaceHeightFacet.class)
public class SurfaceHeightProvider implements FacetProvider {
    @Override
    public void setSeed(long seed) {

    }

    @Override
    public void process(GeneratingRegion region) {
        Border3D border = region.getBorderForFacet(SurfaceHeightFacet.class);
        SurfaceHeightFacet facet = new SurfaceHeightFacet(region.getRegion(), border);

        Rect2i processRegion = facet.getWorldRegion();
        for (BaseVector2i position : processRegion.contents()) {
            facet.setWorld(position, 0f);
        }
        region.setRegionFacet(SurfaceHeightFacet.class, facet);
    }
}
