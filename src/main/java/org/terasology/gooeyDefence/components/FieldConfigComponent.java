// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.components;

import org.terasology.engine.entitySystem.Component;
import org.terasology.gooeyDefence.DefenceField;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to set values for {@link DefenceField} static class. It allows for other modules to override the values set in
 * here for customisation
 *
 * @see DefenceField
 */
public class FieldConfigComponent implements Component {
    public final List<List<List<Integer>>> shrineData = new ArrayList<>();
    public int entranceCount;
    public int shrineRingSize;
    public int outerRingSize;
    public int entranceRingSize;
}
