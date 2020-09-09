// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.towers.events;

import org.terasology.engine.entitySystem.event.Event;
import org.terasology.gooeyDefence.towers.TowerBuildSystem;
import org.terasology.gooeyDefence.towers.TowerManager;
import org.terasology.gooeyDefence.towers.components.TowerComponent;

/**
 * Event sent when a tower is destroyed. Sent against the destroyed tower.
 *
 * @see TowerComponent
 * @see TowerBuildSystem
 * @see TowerManager
 */
public class TowerDestroyedEvent implements Event {
}
