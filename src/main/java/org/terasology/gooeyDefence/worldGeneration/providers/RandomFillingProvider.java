// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.worldGeneration.providers;

import org.terasology.engine.utilities.procedural.Noise;
import org.terasology.engine.utilities.procedural.WhiteNoise;
import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.FacetProvider;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Produces;
import org.terasology.gooeyDefence.DefenceField;
import org.terasology.gooeyDefence.worldGeneration.facets.RandomFillingFacet;
import org.terasology.gooeyDefence.worldGeneration.rasterizers.RandomFillingRasterizer;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector3i;

/**
 * Fills the {@link RandomFillingFacet} with random blocks inside the dome. Leaves a free space around the central
 * shrine and entrances, as dictated by {@link DefenceField}
 *
 * @see RandomFillingRasterizer
 * @see RandomFillingFacet
 */
@Produces(RandomFillingFacet.class)
public class RandomFillingProvider implements FacetProvider {
    private static final float SPAWN_CHANCE = 0.3f;
    private Noise noise;

    /**
     * Checks whether a random block can be spawned based on three rules:
     * <p>
     * 1. Inside the main dome 2. Outside the inner shrine 3. Outside an entrance area
     * <p>
     * Each valid position has a {@link #SPAWN_CHANCE} chance to spawn
     *
     * @param pos The position to query
     * @param noise The noise generator to use
     * @return true if a block should be spawned there. False otherwise
     */
    public static boolean shouldSpawnBlock(BaseVector2i pos, Noise noise) {
        double distance = pos.distance(BaseVector2i.ZERO);
        return distance > DefenceField.shrineRingSize
                && distance < DefenceField.outerRingSize

                && !DefenceField.inRangeOfEntrance(new Vector3i(pos.x(), 0, pos.y()))

                && (noise.noise(pos.x(), pos.y()) + 1) / 2 < SPAWN_CHANCE;
    }

    @Override
    public void setSeed(long seed) {
        noise = new WhiteNoise(seed);
    }

    @Override
    public void process(GeneratingRegion region) {
        Border3D border = region.getBorderForFacet(RandomFillingFacet.class);
        RandomFillingFacet facet = new RandomFillingFacet(region.getRegion(), border);

        Rect2i processRegion = facet.getWorldRegion();
        for (BaseVector2i pos : processRegion.contents()) {
            if (shouldSpawnBlock(pos, noise)) {
                facet.setWorld(pos.x(), pos.y(), true);
            }
            region.setRegionFacet(RandomFillingFacet.class, facet);
        }
    }
}
