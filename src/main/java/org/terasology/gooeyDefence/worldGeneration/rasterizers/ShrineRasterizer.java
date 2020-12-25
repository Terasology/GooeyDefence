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
import org.terasology.gooeyDefence.DefenceField;
import org.terasology.gooeyDefence.DefenceUris;
import org.terasology.math.ChunkMath;
import org.terasology.math.JomlUtil;
import org.terasology.registry.CoreRegistry;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;
import org.terasology.world.chunks.CoreChunk;
import org.terasology.world.generation.Region;
import org.terasology.world.generation.WorldRasterizer;

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
        if (chunkRegion.getRegion().contains(zero)) {
            //TODO: Find a better way to create the shrine that isn't StructureTemplates.
            for (Vector3i pos : DefenceField.shrineData) {
                chunk.setBlock(ChunkMath.calcRelativeBlockPos(JomlUtil.from(pos)), block);
            }
        }
    }
}
