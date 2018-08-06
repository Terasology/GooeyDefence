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

import org.terasology.gooeyDefence.towers.components.TowerTargeter;

/**
 * Options for how a targeter should select an enemy
 * <p>
 * This does not apply to all targeters, only ones that select a single enemy at some point
 *
 * @see TowerTargeter
 */
public enum SelectionMethod {
    /**
     * Select the enemy closest to the shrine.
     */
    FIRST {
        @Override
        public String toString() {
            return "First";
        }
    },
    /**
     * Selects the enemy with the least health.
     */
    WEAK {
        @Override
        public String toString() {
            return "Weakest";
        }
    },
    /**
     * Selects the enemy with the most health.
     */
    STRONG {
        @Override
        public String toString() {
            return "Strongest";
        }
    },
    /**
     * Selects a random valid enemy.
     */
    RANDOM {
        @Override
        public String toString() {
            return "Random";
        }
    }
}
