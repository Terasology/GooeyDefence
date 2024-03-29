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
package org.terasology.gooeyDefence.waves;

import org.terasology.gestalt.entitysystem.event.Event;
import org.terasology.gooeyDefence.DefenceField;

/**
 * Event sent out when an event ends
 * <p>
 * Sent against the {@link DefenceField#getShrineEntity() Shrine Entity}
 */
public class OnWaveEnd implements Event {
}
