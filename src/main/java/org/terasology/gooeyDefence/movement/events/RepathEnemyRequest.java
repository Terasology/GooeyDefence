// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.movement.events;

import org.terasology.engine.entitySystem.event.Event;
import org.terasology.gooeyDefence.movement.PathfindingManager;
import org.terasology.gooeyDefence.movement.components.CustomPathComponent;

/**
 * Event is sent when an enemy is no longer on an entrance path. Sent against the entity wishing to be repathed
 * <p>
 * Calls the {@link PathfindingManager} to re-path this enemy towards the shrine
 *
 * @see CustomPathComponent
 */
public class RepathEnemyRequest implements Event {
}
