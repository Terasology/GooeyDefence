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

public enum SelectionMethod {
    FIRST {
        @Override
        public String toString() {
            return "First";
        }
    },
    WEAK {
        @Override
        public String toString() {
            return "Weakest";
        }
    },
    STRONG {
        @Override
        public String toString() {
            return "Strongest";
        }
    },
    RANDOM {
        @Override
        public String toString() {
            return "Random";
        }
    }
}
