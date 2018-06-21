/*
 * Copyright 2018 MovingBlocks
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
package org.terasology.gooeyDefence.pathfinding;

import org.terasology.flexiblepathfinding.plugins.basic.WalkingPlugin;
import org.terasology.math.Region3i;
import org.terasology.math.geom.Vector3i;
import org.terasology.world.WorldProvider;

public class EnemyWalkingPlugin extends WalkingPlugin {
    public EnemyWalkingPlugin(WorldProvider world, float width, float height) {
        super(world, width, height);
    }

    @Override
    public boolean isReachable(Vector3i to, Vector3i from) {

        /* Check both positions are not the same */
        if (to.equals(from)
                /* We can go diagonally if it's horizontal */
                || to.y == from.y && to.distanceSquared(from) > 2
                /* We can only go directly up or down */
                || to.y != from.y && to.distanceSquared(from) > 1
                /* Check that at least one of the to or from are walkable */
                || !(isWalkable(to) || isWalkable(from))) {
            return false;
        }

        /* Check that all blocks passed through by this movement are penetrable */
        for (Vector3i occupiedBlock : getOccupiedRegionRelative()) {

            /* The start/stop for this block in the occupied region */
            Vector3i occupiedBlockTo = new Vector3i(to).add(occupiedBlock);
            Vector3i occupiedBlockFrom = new Vector3i(from).add(occupiedBlock);

            Region3i movementBounds = Region3i.createBounded(occupiedBlockTo, occupiedBlockFrom);
            for (Vector3i block : movementBounds) {
                if (!world.getBlock(block).isPenetrable()) {
                    return false;
                }
            }
        }
        return true;
    }
}
