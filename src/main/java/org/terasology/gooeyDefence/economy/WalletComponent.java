// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.economy;

import org.terasology.engine.entitySystem.Component;

/**
 * Stores the entities current funds. Note, this is distinct from the value of an entity
 *
 * @see ValueComponent
 */
public class WalletComponent implements Component {
    /**
     * How much money the entity has.
     */
    public int funds;

}
