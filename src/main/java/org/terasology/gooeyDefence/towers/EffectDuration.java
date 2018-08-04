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
 * Options for how long the effect should last.
 */
public enum EffectDuration {
    /**
     * Effect does not take any time.
     * The effect will never be called to be removed.
     */
    INSTANT,
    /**
     * Effect lasts for the duration that the enemy is targeted.
     * The effect will be called to be removed once it is no longer targeted.
     */
    LASTING,
    /**
     * Effect lasts for longer than the enemy is targeted.
     * The effect will never be called to be removed, but the implementing system may remove it.
     */
    PERMANENT
}
