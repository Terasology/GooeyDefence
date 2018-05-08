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

import org.terasology.gooeyDefence.worldGeneration.DefenceField;
import org.terasology.gooeyDefence.worldGeneration.facets.DefenceFieldFacet;
import org.terasology.math.geom.BaseVector3i;
import org.terasology.math.geom.Vector3i;
import org.terasology.utilities.procedural.Noise;
import org.terasology.utilities.procedural.WhiteNoise;
import org.terasology.world.generation.*;

@Produces({DefenceFieldFacet.class})
public class DefenceFieldProvider implements FacetProvider {
    private Noise noise;

    @Override
    public void setSeed(long seed) {
        noise = new WhiteNoise(seed);
    }

    @Override
    public void process(GeneratingRegion region) {

        Border3D border = region.getBorderForFacet(DefenceFieldFacet.class);
        DefenceFieldFacet facet = new DefenceFieldFacet(region.getRegion(), border);
        int size = DefenceField.outerRingSize();
        for (Vector3i pos : region.getRegion()) {
            if (Math.floor(pos.distance(BaseVector3i.ZERO)) == size) {
                facet.setWorld(pos, true);
            }
        }
        region.setRegionFacet(DefenceFieldFacet.class, facet);
    }
}
