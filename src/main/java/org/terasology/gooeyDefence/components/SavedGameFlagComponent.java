// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.components;

import org.terasology.gestalt.entitysystem.component.Component;

/**
 * Component added the player in order to indicate that the game has been reloaded from save.
 * It's used to perform checks that need to know if this is the initial creation of the world.
 */
public class SavedGameFlagComponent implements Component<SavedGameFlagComponent> {
    @Override
    public void copyFrom(SavedGameFlagComponent other) {

    }
}
