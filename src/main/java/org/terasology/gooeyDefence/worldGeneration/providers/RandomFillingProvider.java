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

import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.joml.Vector3i;
import org.terasology.gooeyDefence.DefenceField;
import org.terasology.gooeyDefence.worldGeneration.facets.RandomFillingFacet;
import org.terasology.gooeyDefence.worldGeneration.rasterizers.RandomFillingRasterizer;
import org.terasology.utilities.procedural.Noise;
import org.terasology.utilities.procedural.WhiteNoise;
import org.terasology.world.block.BlockAreac;
import org.terasology.world.generation.Border3D;
import org.terasology.world.generation.FacetProvider;
import org.terasology.world.generation.GeneratingRegion;
import org.terasology.world.generation.Produces;

/**
 * Fills the {@link RandomFillingFacet} with random blocks inside the dome.
 * Leaves a free space around the central shrine and entrances, as dictated by {@link DefenceField}
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
     * 1. Inside the main dome
     * 2. Outside the inner shrine
     * 3. Outside an entrance area
     * <p>
     * Each valid position has a {@link #SPAWN_CHANCE} chance to spawn
     *
     * @param pos   The position to query
     * @param noise The noise generator to use
     * @return true if a block should be spawned there. False otherwise
     */
    public static boolean shouldSpawnBlock(Vector2ic pos, Noise noise) {
        double distance = pos.distance(new Vector2i());
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

        BlockAreac processRegion = facet.getWorldArea();
        for (Vector2ic pos : processRegion) {
            if (shouldSpawnBlock(pos, noise)) {
                facet.setWorld(pos.x(), pos.y(), true);
            }
            region.setRegionFacet(RandomFillingFacet.class, facet);
        }
    }
}
