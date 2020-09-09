// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.visuals.components;

import org.terasology.engine.entitySystem.Component;

/**
 * Used to flag bullets as having a splash effect on them. This will cause them to produce an expanding sphere effect
 * when they reach their goal
 */
public class SplashBulletComponent implements Component {
    public float splashRange = 1f;

    /**
     * Required for serialisation.
     */
    public SplashBulletComponent() {

    }

    public SplashBulletComponent(float splashRange) {
        this.splashRange = splashRange;
    }


}
