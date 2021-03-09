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
package org.terasology.gooeyDefence.visuals.components;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.gooeyDefence.visuals.InWorldRenderer;

import java.util.HashMap;
import java.util.Map;

/**
 * Marks that an entity has one or more child particle entities.
 * Also provides a reference to them.
 *
 * @see InWorldRenderer
 */
public class ChildrenParticleComponent implements Component {
    /**
     * The particles attached to the entity.
     * The key used for this map is the prefab of the entity
     */
    public Map<String, EntityRef> particleEntities = new HashMap<>();

}
