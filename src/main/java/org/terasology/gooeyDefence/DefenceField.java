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

public class DefenceField {
    private static Vector3i[] entrances = new Vector3i[]{
            /* Entrance One */
            new Vector3i(
                    outerRingSize(),
                    0,
                    0),
            /* Entrance Two */
            new Vector3i(
                    (int) (Math.cos(Math.toRadians(120)) * outerRingSize()),
                    0,
                    (int) (Math.sin(Math.toRadians(120)) * outerRingSize())),
            /* Entrance Three */
            new Vector3i(
                    (int) (Math.cos(Math.toRadians(240)) * outerRingSize()),
                    0,
                    (int) (Math.sin(Math.toRadians(240)) * outerRingSize()))
    };

    public static int entranceCount() {
        return entrances.length;
    }

    public static Vector3i fieldCentre() {
        return new Vector3i(0, 0, 0);
    }

    public static int shrineRingSize() {
        return 5;
    }

    public static int outerRingSize() {
        return 60;
    }

    public static int entranceRingSize() {
        return 4;
    }

    public static Vector3i entrancePos(int id) {
        return id < entrances.length && id >= 0 ? entrances[id] : null;
    }

    public static boolean inRangeOfEntrance(BaseVector3i pos) {
        return distanceToNearestEntrance(pos) < entranceRingSize();
    }

    public static double distanceToNearestEntrance(BaseVector3i pos) {
        return Arrays.stream(entrances).mapToDouble(pos::distanceSquared).min().orElse(-1);
    }
}
