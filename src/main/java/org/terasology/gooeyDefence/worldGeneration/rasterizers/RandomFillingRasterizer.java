// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.worldGeneration.rasterizers;

import org.terasology.engine.math.ChunkMath;
import org.terasology.engine.registry.CoreRegistry;
import org.terasology.engine.world.block.Block;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.engine.world.chunks.CoreChunk;
import org.terasology.engine.world.generation.Region;
import org.terasology.engine.world.generation.WorldRasterizer;
import org.terasology.engine.world.generation.facets.SurfaceHeightFacet;
import org.terasology.gooeyDefence.DefenceUris;
import org.terasology.gooeyDefence.worldGeneration.facets.RandomFillingFacet;
import org.terasology.gooeyDefence.worldGeneration.providers.RandomFillingProvider;
import org.terasology.math.geom.Vector3i;

/**
 * Places blocks according to the values given in
 * {@link org.terasology.gooeyDefence.worldGeneration.providers.RandomFillingProvider}.
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
        SurfaceHeightFacet surfaceFacet = chunkRegion.getFacet(SurfaceHeightFacet.class);
        for (Vector3i pos : chunkRegion.getRegion()) {
            if (randomFacet.getWorld(pos.x, pos.z) && surfaceFacet.getWorld(pos.x, pos.z) == pos.y) {
                chunk.setBlock(ChunkMath.calcRelativeBlockPos(pos), block);
            }
        }
    }
}
