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

import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.registry.CoreRegistry;
import org.terasology.engine.world.BlockEntityRegistry;
import org.terasology.gooeyDefence.components.FieldConfigComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A class that provides Static information about the Defence Field.
 * Dynamic information is given by {@link StatSystem}
 *
 * @see StatSystem
 */
public final class DefenceField {
    /**
     * The number of entrances the field has.
     */
    public static int entranceCount;
    /**
     * The radius of the main outer dome.
     * Given in blocks.
     */
    public static int outerRingSize;
    /**
     * The radius of the clear ring around the shrine.
     * Given in blocks.
     */
    public static int shrineRingSize;
    /**
     * The radius of the clear zone around the entrance.
     * Given in blocks.
     */
    public static int entranceRingSize;

    /**
     * A boolean that controls if the field is active or not.
     * Many systems use this to control if they are active or not
     */
    public static boolean fieldActivated;

    /**
     * The data for the shrine's shape.
     * A 1 indicates a block should be placed, and a 0 indicates an empty space
     *
     * @see FieldConfigComponent#shrineData
     */
    public static Vector3i[] shrineData;
    /**
     * The location of the centre of the field.
     * <p>
     * This is the point enemies will path to so the enemies must be able reach it. (ie, no solid blocks there)
     */
    public static final Vector3ic FIELD_CENTRE = new Vector3i(0, 0, 0);
    /**
     * The location of each of the entrances.
     * <p>
     * This is automatically generated from the value of {@link #entranceCount}
     */
    private static Vector3i[] entrances;
    /**
     * The entity representing the main shrine.
     * <p>
     * This entity is also used to send events, when no appropriate alternative is available.
     * It is set to the block entity of a block in the shrine.
     */
    private static EntityRef shrineEntity = EntityRef.NULL;

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
        return id < entrances.length && id >= 0 ? entrances[id] : new Vector3i();
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
     * @param pos The position to check
     * @return True, if the position is inside a clear zone around any entrance. False otherwise
     */
    public static boolean inRangeOfEntrance(Vector3ic pos) {
        return distanceToNearestEntrance(pos) < entranceRingSize;
    }

    /**
     * @param pos The position to check
     * @return The distance between the position and the nearest entrance.
     */
    public static double distanceToNearestEntrance(Vector3ic pos) {
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
