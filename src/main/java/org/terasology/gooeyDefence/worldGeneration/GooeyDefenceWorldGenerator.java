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
package org.terasology.gooeyDefence.worldGeneration;

import org.terasology.engine.SimpleUri;
import org.terasology.gooeyDefence.worldGeneration.providers.DefenceFieldProvider;
import org.terasology.gooeyDefence.worldGeneration.providers.RandomFillingProvider;
import org.terasology.gooeyDefence.worldGeneration.providers.SurfaceHeightProvider;
import org.terasology.gooeyDefence.worldGeneration.rasterizers.DefenceFieldRasterizer;
import org.terasology.gooeyDefence.worldGeneration.rasterizers.RandomFillingRasterizer;
import org.terasology.gooeyDefence.worldGeneration.rasterizers.ShrineRasterizer;
import org.terasology.gooeyDefence.worldGeneration.rasterizers.WorldSurfaceRasterizer;
import org.terasology.registry.In;
import org.terasology.world.generation.BaseFacetedWorldGenerator;
import org.terasology.world.generation.WorldBuilder;
import org.terasology.world.generator.RegisterWorldGenerator;
import org.terasology.world.generator.plugin.WorldGeneratorPluginLibrary;

@RegisterWorldGenerator(id = "gooeyDefenceField", displayName = "Gooey Defence", description = "The world generator for the Gooey Defence gameplay module")
public class GooeyDefenceWorldGenerator extends BaseFacetedWorldGenerator {

    @In
    private WorldGeneratorPluginLibrary worldGeneratorPluginLibrary;

    public GooeyDefenceWorldGenerator(SimpleUri uri) {
        super(uri);
    }

    @Override
    protected WorldBuilder createWorld() {
        return new WorldBuilder(worldGeneratorPluginLibrary)
                .addProvider(new SurfaceHeightProvider())
                .addProvider(new DefenceFieldProvider())
                .addProvider(new RandomFillingProvider())
                .addRasterizer(new WorldSurfaceRasterizer())
                .addRasterizer(new DefenceFieldRasterizer())
                .addRasterizer(new RandomFillingRasterizer())
                .addRasterizer(new ShrineRasterizer());
    }
}
