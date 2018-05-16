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
package org.terasology.gooeyDefence;

import org.terasology.math.geom.BaseVector3i;
import org.terasology.math.geom.Vector3i;

import java.util.Arrays;


/**
 * A class that provides Static information about the Defence Field.
 * Dynamic information is given by {@link DefenceWorldProvider}
 *
 * @see DefenceWorldProvider
 */
public class DefenceField {
    private static Vector3i[] entrances = calculateEntrances(3);
    /**
     * The data for the shrine's shape.
     * A 1 indicates a block should be placed, and a 0 indicates an empty space
     */
    private static int[][][] shrine = new int[][][]{
            {{0,0,0},
             {0,1,0},
             {0,0,0}},

            {{0,0,0},
             {0,1,0},
             {0,0,0}},

            {{0,1,0},
             {1,1,1},
             {0,1,0}},

            {{1,1,1},
             {1,1,1},
             {1,1,1}},

            {{0,1,0},
             {1,1,1},
             {0,1,0}},

            {{0,0,0},
             {0,1,0},
             {0,0,0}}};

    /**
     * Returns the location of a block in the shrine.
     * This block will always be the same one given the same shrine arrangement.
     * @return The position of a block in the shrine
     */
    public static Vector3i getShrineBlock() {
        for (int y = 0; y < shrine.length; y++) {
            for (int x = 0; x < shrine[y].length; x++) {
                for (int z = 0; z < shrine[y][x].length; z++) {
                    if (shrine[y][x][z] == 1) {
                        return new Vector3i(x, y, z);
                    }
                }
            }
        }
        return null;
    }

    public static int[][][] getShrine() {
        return shrine;
    }

    /**
     * Calculates the position of each entrance along the rim of the dome
     * @param count The number of entrances
     * @return An array containing the locations of the entrances.
     */
    private static Vector3i[] calculateEntrances(int count) {
        Vector3i[] result = new Vector3i[count];
        double stepSize = (2 * Math.PI) / count;
        for (int i = 0; i < count; i++) {
            result[i] = new Vector3i(
                    (int) (Math.cos(stepSize * i) * outerRingSize()),
                    0,
                    (int) (Math.sin(stepSize * i) * outerRingSize())
            );
        }
        return result;
    }

    /**
     * @return The number of entrances in the field
     */
    public static int entranceCount() {
        return entrances.length;
    }

    /**
     * @return The centre of the field.
     */
    public static Vector3i fieldCentre() {
        return new Vector3i(0, 0, 0);
    }

    /**
     * @return The size, in blocks, of the clear zone around the shrine
     */
    public static int shrineRingSize() {
        return 5;
    }

    /**
     * @return The size, in blocks, of the outer wall of the defence field
     */
    public static int outerRingSize() {
        return 60;
    }

    /**
     * @return The size, in blocks, of the clear zone around each entrance
     */
    public static int entranceRingSize() {
        return 4;
    }

    /**
     * @param id The id of the entrance to get
     * @return The position of the entrance
     */
    public static Vector3i entrancePos(int id) {
        return id < entrances.length && id >= 0 ? entrances[id] : null;
    }

    /**
     * @param pos The position to check
     * @return True, if the position is inside a clear zone around any entrance. False otherwise
     */
    public static boolean inRangeOfEntrance(BaseVector3i pos) {
        return distanceToNearestEntrance(pos) < entranceRingSize();
    }

    /**
     * @param pos The position to check
     * @return The distance between the position and the nearest entrance.
     */
    public static double distanceToNearestEntrance(BaseVector3i pos) {
        return Arrays.stream(entrances).mapToDouble(pos::distance).min().orElse(-1);
    }
}
