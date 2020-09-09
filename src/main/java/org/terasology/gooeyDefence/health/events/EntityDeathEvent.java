// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.health.events;

import org.terasology.engine.entitySystem.event.Event;
import org.terasology.gooeyDefence.health.HealthSystem;

/**
 * Event sent when an entity reaches zero health. Sent against the dead entity.
 *
 * @see HealthSystem
 * @see DamageEntityEvent
 */
public class EntityDeathEvent implements Event {
}
