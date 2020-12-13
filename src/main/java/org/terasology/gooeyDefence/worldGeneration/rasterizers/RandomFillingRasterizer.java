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
package org.terasology.gooeyDefence.worldGeneration.rasterizers;

import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.terasology.gooeyDefence.DefenceUris;
import org.terasology.gooeyDefence.worldGeneration.facets.RandomFillingFacet;
import org.terasology.gooeyDefence.worldGeneration.providers.RandomFillingProvider;
import org.terasology.math.ChunkMath;
import org.terasology.registry.CoreRegistry;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;
import org.terasology.world.block.BlockRegions;
import org.terasology.world.chunks.CoreChunk;
import org.terasology.world.generation.Region;
import org.terasology.world.generation.WorldRasterizer;
import org.terasology.world.generation.facets.ElevationFacet;

/**
 * Places blocks according to the values given in {@link org.terasology.gooeyDefence.worldGeneration.providers.RandomFillingProvider}.
 * The block used is the basic building block.
 *
 * @see RandomFillingFacet
 * @see RandomFillingProvider
 */
public class RandomFillingRasterizer implements WorldRasterizer {
    private Block block;

    @Override
    public void initialize() {
        block = CoreRegistry.get(BlockManager.class).getBlock(DefenceUris.PLAIN_WORLD_BLOCK);
    }

    @Override
    public void generateChunk(CoreChunk chunk, Region chunkRegion) {
        RandomFillingFacet randomFacet = chunkRegion.getFacet(RandomFillingFacet.class);
        ElevationFacet surfaceFacet = chunkRegion.getFacet(ElevationFacet.class);
        for (Vector3ic pos : BlockRegions.iterableInPlace(chunkRegion.getRegion())) {
            if (randomFacet.getWorld(pos.x(), pos.z()) && surfaceFacet.getWorld(pos.x(), pos.z()) == pos.y()) {
                chunk.setBlock(ChunkMath.calcRelativeBlockPos(pos, new Vector3i()), block);
            }
        }
    }
}
