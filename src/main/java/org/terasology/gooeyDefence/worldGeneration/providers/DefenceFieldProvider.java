// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.worldGeneration.providers;

import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.FacetProvider;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Produces;
import org.terasology.gooeyDefence.DefenceField;
import org.terasology.gooeyDefence.worldGeneration.facets.DefenceFieldFacet;
import org.terasology.gooeyDefence.worldGeneration.rasterizers.DefenceFieldRasterizer;
import org.terasology.math.geom.BaseVector3i;
import org.terasology.math.geom.Vector3i;

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
        for (Vector3i pos : region.getRegion()) {
            /* Generate a border if the position is either
             * 1. Part of the main dome, but not within range of an entrance
             * 2. Part of an entrance dome, but outside the main dome
             */
            int centreDistance = (int) pos.distance(BaseVector3i.ZERO);
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
