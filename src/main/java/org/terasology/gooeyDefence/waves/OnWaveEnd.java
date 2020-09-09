// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.waves;

import org.terasology.engine.entitySystem.event.Event;
import org.terasology.gooeyDefence.DefenceField;

/**
 * Event sent out when an event ends
 * <p>
 * Sent against the {@link DefenceField#getShrineEntity() Shrine Entity}
 */
public class OnWaveEnd implements Event {
}
