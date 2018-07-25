/*
 * Copyright 2018 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.gooeyDefence.components;

import org.terasology.entitySystem.Component;

/**
 * Used to flag bullets as having a splash effect on them.
 * This will cause them to produce an expanding sphere effect when they reach their goal
 */
public class SplashBulletComponent implements Component {
    private float splashRange = 1f;

    public SplashBulletComponent() {

    }

    public SplashBulletComponent(float splashRange) {
        this.splashRange = splashRange;
    }


    public float getSplashRange() {
        return splashRange;
    }

}
