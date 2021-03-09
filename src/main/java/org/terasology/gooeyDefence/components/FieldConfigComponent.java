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
package org.terasology.gooeyDefence.components;

import org.terasology.engine.entitySystem.Component;
import org.terasology.gooeyDefence.DefenceField;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to set values for {@link DefenceField} static class.
 * It allows for other modules to override the values set in here for customisation
 *
 * @see DefenceField
 */
public class FieldConfigComponent implements Component {
    public int entranceCount;
    public int shrineRingSize;
    public int outerRingSize;
    public int entranceRingSize;
    public final List<List<List<Integer>>> shrineData = new ArrayList<>();
}
