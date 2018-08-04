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
package org.terasology.gooeyDefence.towers;

/**
 * Represents how many times an effect should be applied to a given target
 */
public enum EffectCount {
    /**
     * Effect should be applied on every single shot.
     * The effector will be called for every shot whilst the enemy is within range
     */
    PER_SHOT,
    /**
     * The effect should only be applied once to the target.
     * The effector will only be called each time an enemy is targeted.
     */
    CONTINUOUS
}
