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
package org.terasology.gooeyDefence.worldGeneration.facets;

import org.terasology.gooeyDefence.worldGeneration.providers.DefenceFieldProvider;
import org.terasology.gooeyDefence.worldGeneration.rasterizers.DefenceFieldRasterizer;
import org.terasology.world.block.BlockRegion;
import org.terasology.world.generation.Border3D;
import org.terasology.world.generation.facets.base.SparseBooleanFieldFacet3D;


/**
 * Facet that provides information on where to place the blocks that make up the main outer dome
 *
 * @see DefenceFieldRasterizer
 * @see DefenceFieldProvider
 */
public class DefenceFieldFacet extends SparseBooleanFieldFacet3D {

    public DefenceFieldFacet(BlockRegion region, Border3D border) {
        super(region, border);
    }

}
