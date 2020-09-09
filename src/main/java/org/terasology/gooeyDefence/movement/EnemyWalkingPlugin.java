// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.movement;

import org.terasology.engine.math.Region3i;
import org.terasology.engine.world.WorldProvider;
import org.terasology.flexiblepathfinding.plugins.basic.WalkingPlugin;
import org.terasology.math.geom.Vector3i;

/**
 * Plugin that defines how the standard enemies will walk. This plugin allows enemies to move horizontally and
 * vertically
 *
 * @see WalkingPlugin
 */
public class EnemyWalkingPlugin extends WalkingPlugin {
    public EnemyWalkingPlugin(WorldProvider world, float width, float height) {
        super(world, width, height);
    }

    @Override
    public boolean isReachable(Vector3i to, Vector3i from) {
        return !to.equals(from)
                && (isHorizontallyReachable(to, from) || isVerticallyReachable(to, from))
                && isWalkable(to, from)
                && areAllBlocksPenetrable(to, from);
    }

    /**
     * Checks that all the blocks the enemy will pass through are penetrable.
     *
     * @param to The ending position
     * @param from The starting position
     * @return True if all the blocks are penetrable.
     */
    private boolean areAllBlocksPenetrable(Vector3i to, Vector3i from) {
        for (Vector3i relativePosition : getOccupiedRegionRelative()) {

            /* The start/stop for this block in the occupied region */
            Vector3i occupiedRegionStart = new Vector3i(to).add(relativePosition);
            Vector3i occupiedRegionEnd = new Vector3i(from).add(relativePosition);

            Region3i movementBounds = Region3i.createBounded(occupiedRegionStart, occupiedRegionEnd);
            for (Vector3i block : movementBounds) {
                if (!world.getBlock(block).isPenetrable()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * When travelling horizontally, the enemy can travel along all 8 horizontal directions. That is, the enemy can
     * travel diagonally as well as cardinally.
     *
     * @param to The ending position
     * @param from The starting position
     * @return True if the movement is horizontally possible.
     */
    private boolean isHorizontallyReachable(Vector3i to, Vector3i from) {
        return isMovementHorizontal(to, from) && to.distanceSquared(from) <= 2;
    }

    /**
     * When travelling vertically, the enemy can only go directly up or down. That is, they cannot travel diagonally
     *
     * @param to The ending position
     * @param from The starting position
     * @return True if the movement is vertically possible.
     */
    private boolean isVerticallyReachable(Vector3i to, Vector3i from) {
        return !isMovementHorizontal(to, from) && to.distanceSquared(from) <= 1;
    }

    /**
     * Check if the movement is walkable.
     * <p>
     *
     * @param to The ending position
     * @param from The starting position
     * @return True if the the movement is walkable
     */
    private boolean isWalkable(Vector3i to, Vector3i from) {
        return isWalkable(to) || isWalkable(from);
    }

    /**
     * Checks if the movement is strictly horizontal
     *
     * @param to The ending position
     * @param from The starting position
     * @return True if both positions are at the same height.
     */
    private boolean isMovementHorizontal(Vector3i to, Vector3i from) {
        return to.y == from.y;
    }
}
