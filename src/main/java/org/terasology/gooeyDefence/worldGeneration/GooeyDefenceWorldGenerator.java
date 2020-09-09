// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.worldGeneration;

import org.terasology.engine.core.SimpleUri;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.generation.BaseFacetedWorldGenerator;
import org.terasology.engine.world.generation.WorldBuilder;
import org.terasology.engine.world.generator.RegisterWorldGenerator;
import org.terasology.engine.world.generator.plugin.WorldGeneratorPluginLibrary;
import org.terasology.gooeyDefence.worldGeneration.facets.DefenceFieldFacet;
import org.terasology.gooeyDefence.worldGeneration.facets.RandomFillingFacet;
import org.terasology.gooeyDefence.worldGeneration.providers.DefenceFieldProvider;
import org.terasology.gooeyDefence.worldGeneration.providers.RandomFillingProvider;
import org.terasology.gooeyDefence.worldGeneration.providers.SurfaceHeightProvider;
import org.terasology.gooeyDefence.worldGeneration.rasterizers.DefenceFieldRasterizer;
import org.terasology.gooeyDefence.worldGeneration.rasterizers.RandomFillingRasterizer;
import org.terasology.gooeyDefence.worldGeneration.rasterizers.ShrineRasterizer;
import org.terasology.gooeyDefence.worldGeneration.rasterizers.WorldSurfaceRasterizer;

/**
 * Generates the GooeyDefence world. Also provides details used in the world generator listing
 *
 * @see DefenceFieldFacet
 * @see RandomFillingFacet
 */
@RegisterWorldGenerator(id = "gooeyDefenceField", displayName = "Gooey Defence", description = "The world generator " +
        "for the Gooey Defence gameplay module")
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
                .addRasterizer(new RandomFillingRasterizer())
                .addRasterizer(new DefenceFieldRasterizer())
                .addRasterizer(new ShrineRasterizer());
    }
}
