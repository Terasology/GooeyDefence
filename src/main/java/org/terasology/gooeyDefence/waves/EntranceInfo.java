// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.waves;

import org.terasology.reflection.MappedContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Contains information for spawning enemies at an entrance.
 * <p>
 * The fields {@link #delayCount} & {@link #delay} and the prefab versions, {@link #prefabCount} & {@link #prefab} allow
 * for easy creation of waves if all delays and/or prefabs are the same values.
 * <p>
 * Using both the short version and the full lists is not recommended and will result in un-defined behaviour.
 *
 * @see WaveInfo
 */
@MappedContainer
public class EntranceInfo {
    /**
     * An ordered list of the delay between each wave Each delay corresponds to the time until the matching entity will
     * be created.
     * <p>
     * Ex. <code>delays = [1, 0.5, 0]</code> Here once the wave is started there will be a 1 second delay, then the
     * first entity spawned Then a 0.5 second delay and the second entity spawned Then no delay, meaning the third and
     * last entity will be spawned instantly after the second.
     */
    public List<Float> delays = new ArrayList<>();
    /**
     * The ordered list of prefabs to use to spawn the entities. Each entry in this list must correspond to a delay in
     * {@link #delays} and vice versa. For no delay use a value of <code>0f</code>
     */
    public List<String> prefabs = new ArrayList<>();

    /**
     * The number of delay entries to create
     */
    private int delayCount;
    /**
     * The value to use
     */
    private float delay;

    /**
     * The number of prefab entries to create
     */
    private int prefabCount;
    /**
     * The prefab to use.
     */
    private String prefab;

    /**
     * Plain public constructor for serialisation.
     */
    public EntranceInfo() {
        buildLists();
    }

    /**
     * Copy an entrance info into a new instance
     *
     * @param copy The entrance info to clone
     */
    public EntranceInfo(EntranceInfo copy) {
        this();
        copy.buildLists();
        delays.addAll(copy.delays);
        prefabs.addAll(copy.prefabs);
    }

    /**
     * If the list of prefabs and/or delays are empty then use the values from the short forms.
     */
    private void buildLists() {
        if (prefabCount > 0 && prefabs.isEmpty()) {
            prefabs = Collections.nCopies(prefabCount, prefab);
        }
        if (delayCount > 0 && delays.isEmpty()) {
            delays = Collections.nCopies(delayCount, delay);
        }
    }

}
