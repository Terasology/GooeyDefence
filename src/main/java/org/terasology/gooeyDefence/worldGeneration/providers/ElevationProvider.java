/*
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.gooeyDefence.worldGeneration.providers;

import org.joml.Vector2ic;
import org.terasology.engine.world.block.BlockAreac;
import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.FacetProvider;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Produces;
import org.terasology.engine.world.generation.facets.ElevationFacet;
import org.terasology.gooeyDefence.worldGeneration.rasterizers.WorldSurfaceRasterizer;

/**
 * Fills the {@link ElevationFacet} with a surface height of 0 globally.
 *
 * @see ElevationFacet
 * @see WorldSurfaceRasterizer
 */
@Produces(ElevationFacet.class)
public class ElevationProvider implements FacetProvider {
    @Override
    public void setSeed(long seed) {

    }

    @Override
    public void process(GeneratingRegion region) {
        Border3D border = region.getBorderForFacet(ElevationFacet.class);
        ElevationFacet facet = new ElevationFacet(region.getRegion(), border);

        BlockAreac processRegion = facet.getWorldArea();
        for (Vector2ic position : processRegion) {
            facet.setWorld(position, 0f);
        }
        region.setRegionFacet(ElevationFacet.class, facet);
    }
}
