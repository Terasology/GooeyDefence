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
import org.terasology.math.geom.Vector3i;

/**
 * Builds a shrine at the origin of the world.
 *
 * @see DefenceField#shrineData
 */
public class ShrineRasterizer implements WorldRasterizer {

    private Block block;

    @Override
    public void initialize() {
        block = CoreRegistry.get(BlockManager.class).getBlock(DefenceUris.SHRINE);
    }

    @Override
    public void generateChunk(CoreChunk chunk, Region chunkRegion) {
        Vector3i zero = new Vector3i(0, 0, 0);
        if (chunkRegion.getRegion().encompasses(zero)) {
            //TODO: Find a better way to create the shrine that isn't StructureTemplates.
            for (Vector3i pos : DefenceField.shrineData) {
                chunk.setBlock(ChunkMath.calcRelativeBlockPos(pos), block);
            }
        }
    }
}
