// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.towers.effectors;

import org.terasology.gooeyDefence.towers.EffectCount;
import org.terasology.gooeyDefence.towers.EffectDuration;
import org.terasology.gooeyDefence.towers.components.TowerEffector;

/**
 * Test effector. Simply increases the size of the enemy in order to help identify it for debugging.
 *
 * @see VisualEffectorSystem
 * @see TowerEffector
 */
public class VisualEffectorComponent extends TowerEffector {
    @Override
    public EffectCount getEffectCount() {
        return EffectCount.CONTINUOUS;
    }

    @Override
    public EffectDuration getEffectDuration() {
        return EffectDuration.LASTING;
    }
}
