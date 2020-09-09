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
import org.terasology.gooeyDefence.worldGeneration.providers.SurfaceHeightProvider;
import org.terasology.math.geom.Vector3i;

/**
 * Places the base world block at the surface level.
 *
 * @see SurfaceHeightFacet
 * @see SurfaceHeightProvider
 */
public class WorldSurfaceRasterizer implements WorldRasterizer {

    private Block block;

    @Override
    public void initialize() {
        block = CoreRegistry.get(BlockManager.class).getBlock(DefenceUris.WORLD_BLOCK);
    }

    @Override
    public void generateChunk(CoreChunk chunk, Region chunkRegion) {
        SurfaceHeightFacet surfaceHeightFacet = chunkRegion.getFacet(SurfaceHeightFacet.class);
        for (Vector3i pos : chunkRegion.getRegion()) {
            float height = surfaceHeightFacet.getWorld(pos.x, pos.z);
            if (pos.y < height) {
                chunk.setBlock(ChunkMath.calcRelativeBlockPos(pos), block);
            }
        }
    }
}
