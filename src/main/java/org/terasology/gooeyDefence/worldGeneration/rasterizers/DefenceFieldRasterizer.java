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
import org.terasology.gooeyDefence.DefenceField;
import org.terasology.gooeyDefence.DefenceUris;
import org.terasology.gooeyDefence.worldGeneration.facets.DefenceFieldFacet;
import org.terasology.gooeyDefence.worldGeneration.providers.DefenceFieldProvider;
import org.terasology.math.geom.Vector3i;

import java.util.Map;

/**
 * Places blocks according to the values set in {@link DefenceFieldProvider}. Any block close to the entrance, including
 * those in the mini dome, are set to a different block than the rest of the dome.
 *
 * @see DefenceFieldProvider
 * @see DefenceFieldFacet
 */
public class DefenceFieldRasterizer implements WorldRasterizer {
    private Block block;
    private Block altBlock;

    @Override
    public void initialize() {
        block = CoreRegistry.get(BlockManager.class).getBlock(DefenceUris.WORLD_BLOCK);
        altBlock = CoreRegistry.get(BlockManager.class).getBlock(DefenceUris.ALT_WORLD_BLOCK);
    }

    @Override
    public void generateChunk(CoreChunk chunk, Region chunkRegion) {
        DefenceFieldFacet fieldFacet = chunkRegion.getFacet(DefenceFieldFacet.class);
        for (Map.Entry<Vector3i, Boolean> entry : fieldFacet.getWorldEntries().entrySet()) {
            if (entry.getValue()) {
                Vector3i pos = entry.getKey();
                if ((int) DefenceField.distanceToNearestEntrance(pos) < DefenceField.entranceRingSize + 2) {
                    chunk.setBlock(ChunkMath.calcRelativeBlockPos(pos), altBlock);
                } else {
                    chunk.setBlock(ChunkMath.calcRelativeBlockPos(pos), block);
                }
            }
        }
    }
}

