// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.components;

import org.terasology.gestalt.entitysystem.component.EmptyComponent;

/**
 * Marks that a block can be destroyed.
 * <p>
 * All blocks without this are blocked from being destroyed.
 */
public class DestructibleBlockComponent extends EmptyComponent<DestructibleBlockComponent> {
}
