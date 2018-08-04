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

import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.gooeyDefence.components.FieldConfigComponent;
import org.terasology.math.geom.BaseVector3i;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.CoreRegistry;
import org.terasology.world.BlockEntityRegistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * A class that provides Static information about the Defence Field.
 * Dynamic information is given by {@link DefenceWorldManager}
 *
 * @see DefenceWorldManager
 */
public final class DefenceField {
    public static int entranceCount;
    public static int shrineRingSize;
    public static int outerRingSize;
    public static int entranceRingSize;
    /**
     * The data for the shrine's shape.
     * A 1 indicates a block should be placed, and a 0 indicates an empty space
     */
    public static Vector3i[] shrineData;
    public static Vector3i fieldCentre = new Vector3i(0, 0, 0);
    private static Vector3i[] entrances;

    private static EntityRef shrineEntity = EntityRef.NULL;
    private static boolean fieldActivated;

    /**
     * Private constructor as class is a utility class and should not be instantiated.
     */
    private DefenceField() {
    }

    public static void loadFieldValues(FieldConfigComponent config) {
        entranceCount = config.entranceCount;
        shrineRingSize = config.shrineRingSize;
        outerRingSize = config.outerRingSize;
        entranceRingSize = config.entranceRingSize;

        entrances = calculateEntrances(entranceCount);
        shrineData = convertToVectors(config.shrineData);
    }

    /**
     * Converts the human readable shrine data to a list of positions.
     * Only intended to be used once to initialise a field.
     *
     * @param rawData The human readable version of the data.
     * @return An array of Vector3i containing the location of each one.
     */
    private static Vector3i[] convertToVectors(List<List<List<Integer>>> rawData) {
        List<Vector3i> positions = new ArrayList<>();

        for (int y = 0; y < rawData.size(); y++) {
            for (int x = 0; x < rawData.get(y).size(); x++) {
                for (int z = 0; z < rawData.get(y).get(x).size(); z++) {
                    if (rawData.get(y).get(x).get(z) == 1) {
                        positions.add(new Vector3i(x, y, z));
                    }
                }
            }
        }

        return positions.toArray(new Vector3i[0]);
    }

    /**
     * Calculates the position of each entrance along the rim of the dome.
     * Only intended to be used once to initialise a field.
     *
     * @param count The number of entrances
     * @return An array containing the locations of the entrances.
     */
    private static Vector3i[] calculateEntrances(int count) {
        Vector3i[] result = new Vector3i[count];
        double stepSize = (2 * Math.PI) / count;
        for (int i = 0; i < count; i++) {
            result[i] = new Vector3i(
                    (int) (Math.cos(stepSize * i) * outerRingSize),
                    0,
                    (int) (Math.sin(stepSize * i) * outerRingSize)
            );
        }
        return result;
    }

    /**
     * @param id The id of the entrance to get
     * @return The position of the entrance
     */
    public static Vector3i entrancePos(int id) {
        return id < entrances.length && id >= 0 ? entrances[id] : Vector3i.zero();
    }

    /**
     * Get the shrine entity from the shrine.
     * Caches the result, recollecting it when it's not existing
     *
     * @return The shrine entity, or the null entity if it can't be found
     */
    public static EntityRef getShrineEntity() {
        if (!shrineEntity.exists()) {
            shrineEntity = CoreRegistry.get(BlockEntityRegistry.class).getBlockEntityAt(
                    shrineData.length > 0 ? shrineData[0] : null);
        }
        return shrineEntity;
    }

    /**
     * @return A boolean indicating if the field is activated or not.
     */
    public static boolean isFieldActivated() {
        return fieldActivated;
    }

    /**
     * Activate the field.
     * There is no mechanism for setting the value to false because this shouldn't be possible or needed.
     */
    public static void setFieldActivated() {
        DefenceField.fieldActivated = true;
    }

    /**
     * @param pos The position to check
     * @return True, if the position is inside a clear zone around any entrance. False otherwise
     */
    public static boolean inRangeOfEntrance(BaseVector3i pos) {
        return distanceToNearestEntrance(pos) < entranceRingSize;
    }

    /**
     * @param pos The position to check
     * @return The distance between the position and the nearest entrance.
     */
    public static double distanceToNearestEntrance(BaseVector3i pos) {
        return Arrays.stream(entrances).mapToDouble(pos::distance).min().orElse(-1);
    }

    /**
     * Helper method for getting a component given one of its superclasses
     *
     * @param entity     The entity to search on
     * @param superClass The superclass of the component to filter for
     * @param <Y>        The type of the superclass
     * @return The component that extends the superclass
     */
    public static <Y> Y getComponentExtending(EntityRef entity, Class<Y> superClass) {
        if (!entity.exists()) {
            throw new IllegalArgumentException("Component extending " + superClass.getSimpleName() + " requested from a null entity");
        }
        for (Component component : entity.iterateComponents()) {
            if (superClass.isInstance(component)) {
                return superClass.cast(component);
            }
        }
        throw new IllegalArgumentException("Entity didn't have any component extending " + superClass.getSimpleName());
    }

    /**
     * Checks if the entity has a component extending a given type.
     *
     * @param entity     The entity to check on
     * @param superClass The class that should be extended
     * @param <Y>        The type of the superclass
     * @return True, if a component on the entity extends the given class
     */
    public static <Y> boolean hasComponentExtending(EntityRef entity, Class<Y> superClass) {
        if (!entity.exists()) {
            return false;
        }
        for (Component component : entity.iterateComponents()) {
            if (superClass.isInstance(component)) {
                return true;
            }
        }
        return false;
    }
}
