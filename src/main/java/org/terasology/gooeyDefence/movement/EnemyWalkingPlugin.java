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
package org.terasology.gooeyDefence.movement;

import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.terasology.flexiblepathfinding.plugins.basic.WalkingPlugin;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.BlockRegion;

/**
 * Plugin that defines how the standard enemies will walk.
 * This plugin allows enemies to move horizontally and vertically
 *
 * @see WalkingPlugin
 */
public class EnemyWalkingPlugin extends WalkingPlugin {
    public EnemyWalkingPlugin(WorldProvider world, float width, float height) {
        super(world, width, height);
    }

    @Override
    public boolean isReachable(Vector3ic to, Vector3ic from) {
        return !to.equals(from)
                && (isHorizontallyReachable(to, from) || isVerticallyReachable(to, from))
                && isWalkable(to, from)
                && areAllBlocksPenetrable(to, from);
    }

    /**
     * Checks that all the blocks the enemy will pass through are penetrable.
     *
     * @param to   The ending position
     * @param from The starting position
     * @return True if all the blocks are penetrable.
     */
    private boolean areAllBlocksPenetrable(Vector3ic to, Vector3ic from) {
        for (Vector3ic relativePosition : getOccupiedRegionRelative()) {

            /* The start/stop for this block in the occupied region */
            Vector3i occupiedRegionStart = new Vector3i(to).add(relativePosition);
            Vector3i occupiedRegionEnd = new Vector3i(from).add(relativePosition);

            BlockRegion movementBounds = new BlockRegion(occupiedRegionStart).union(occupiedRegionEnd);
            for (Vector3ic block : movementBounds) {
                if (!world.getBlock(block).isPenetrable()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * When travelling horizontally, the enemy can travel along all 8 horizontal directions.
     * That is, the enemy can travel diagonally as well as cardinally.
     *
     * @param to   The ending position
     * @param from The starting position
     * @return True if the movement is horizontally possible.
     */
    private boolean isHorizontallyReachable(Vector3ic to, Vector3ic from) {
        return isMovementHorizontal(to, from) && to.distanceSquared(from) <= 2;
    }

    /**
     * When travelling vertically, the enemy can only go directly up or down.
     * That is, they cannot travel diagonally
     *
     * @param to   The ending position
     * @param from The starting position
     * @return True if the movement is vertically possible.
     */
    private boolean isVerticallyReachable(Vector3ic to, Vector3ic from) {
        return !isMovementHorizontal(to, from) && to.distanceSquared(from) <= 1;
    }

    /**
     * Check if the movement is walkable.
     * <p>
     *
     * @param to   The ending position
     * @param from The starting position
     * @return True if the the movement is walkable
     */
    private boolean isWalkable(Vector3ic to, Vector3ic from) {
        return isWalkable(to) || isWalkable(from);
    }

    /**
     * Checks if the movement is strictly horizontal
     *
     * @param to   The ending position
     * @param from The starting position
     * @return True if both positions are at the same height.
     */
    private boolean isMovementHorizontal(Vector3ic to, Vector3ic from) {
        return to.y() == from.y();
    }
}
