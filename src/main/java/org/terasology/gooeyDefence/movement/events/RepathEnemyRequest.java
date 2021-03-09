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
package org.terasology.gooeyDefence.movement.events;

import org.terasology.engine.entitySystem.event.Event;
import org.terasology.gooeyDefence.movement.PathfindingManager;
import org.terasology.gooeyDefence.movement.components.CustomPathComponent;

/**
 * Event is sent when an enemy is no longer on an entrance path.
 * Sent against the entity wishing to be repathed
 * <p>
 * Calls the {@link PathfindingManager} to re-path this enemy towards the shrine
 *
 * @see CustomPathComponent
 */
public class RepathEnemyRequest implements Event {
}
