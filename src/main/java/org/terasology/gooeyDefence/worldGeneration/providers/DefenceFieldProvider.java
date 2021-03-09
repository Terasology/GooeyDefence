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

import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.FacetProvider;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Produces;
import org.terasology.gooeyDefence.DefenceField;
import org.terasology.gooeyDefence.worldGeneration.facets.DefenceFieldFacet;
import org.terasology.gooeyDefence.worldGeneration.rasterizers.DefenceFieldRasterizer;

/**
 * Fills the {@link DefenceFieldFacet} class with data on the dome.
 *
 * @see DefenceFieldRasterizer
 * @see DefenceFieldFacet
 */
@Produces(DefenceFieldFacet.class)
public class DefenceFieldProvider implements FacetProvider {

    @Override
    public void process(GeneratingRegion region) {

        Border3D border = region.getBorderForFacet(DefenceFieldFacet.class);
        DefenceFieldFacet facet = new DefenceFieldFacet(region.getRegion(), border);
        for (Vector3ic position : region.getRegion()) {
            /* Generate a border if the position is either
             * 1. Part of the main dome, but not within range of an entrance
             * 2. Part of an entrance dome, but outside the main dome
             */
            Vector3i pos = new Vector3i(position);
            int centreDistance = (int) pos.distance(new Vector3i());
            int entranceDistance = (int) DefenceField.distanceToNearestEntrance(pos);
            if (centreDistance == DefenceField.outerRingSize
                    && entranceDistance >= DefenceField.entranceRingSize) {
                facet.setWorld(pos, true);
            } else if (centreDistance >= DefenceField.outerRingSize
                    && entranceDistance == DefenceField.entranceRingSize) {
                facet.setWorld(pos, true);
            }
        }
        region.setRegionFacet(DefenceFieldFacet.class, facet);
    }
}
