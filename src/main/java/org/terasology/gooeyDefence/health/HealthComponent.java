// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.health;

import org.terasology.gestalt.entitysystem.component.Component;

/**
 * Adds a very primitive health system to an entity.
 *
 * @see HealthSystem
 */
public class HealthComponent implements Component<HealthComponent> {
    public int health;

    @Override
    public void copy(HealthComponent other) {
        this.health = other.health;
    }
}
