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

import org.terasology.gooeyDefence.worldGeneration.DefenceField;
import org.terasology.gooeyDefence.worldGeneration.facets.RandomFillingFacet;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.Rect2i;
import org.terasology.utilities.procedural.Noise;
import org.terasology.utilities.procedural.WhiteNoise;
import org.terasology.world.generation.Border3D;
import org.terasology.world.generation.Facet;
import org.terasology.world.generation.FacetProvider;
import org.terasology.world.generation.GeneratingRegion;
import org.terasology.world.generation.Produces;
import org.terasology.world.generation.Requires;
import org.terasology.world.generation.facets.SurfaceHeightFacet;

@Produces({RandomFillingFacet.class})
@Requires(@Facet(SurfaceHeightFacet.class))
public class RandomFillingProvider implements FacetProvider {
    private Noise noise;

    @Override
    public void setSeed(long seed) {
        noise = new WhiteNoise(seed);
    }

    @Override
    public void process(GeneratingRegion region) {
        Border3D border = region.getBorderForFacet(RandomFillingFacet.class);
        RandomFillingFacet facet = new RandomFillingFacet(region.getRegion(), border);
        SurfaceHeightFacet surfaceFacet = region.getRegionFacet(SurfaceHeightFacet.class);

        Rect2i processRegion = facet.getWorldRegion();
        for (BaseVector2i pos : processRegion.contents()) {
            if (noise.noise(pos.x(), pos.y()) > 0.2 && pos.distance(BaseVector2i.ZERO) > DefenceField.shrineRingSize()) {
                facet.setWorld(pos.x(), pos.y(), true);
            }
        }
        region.setRegionFacet(RandomFillingFacet.class, facet);
    }
}
