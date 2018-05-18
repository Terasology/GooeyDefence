/*
 * Copyright 2017 MovingBlocks
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
package org.terasology.gooeyDefence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.flexiblepathfinding.PathfinderSystem;
import org.terasology.logic.common.ActivateEvent;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.In;
import org.terasology.registry.Share;
import org.terasology.world.OnChangedBlock;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A class that provides dynamic information about the Defence Field.
 * Static information is given by {@link DefenceField}
 *
 * @see DefenceField
 */
@Share(DefenceWorldProvider.class)
@RegisterSystem
public class DefenceWorldProvider extends BaseComponentSystem {
    private static final Logger logger = LoggerFactory.getLogger(DefenceWorldProvider.class);

    private List<List<Vector3i>> paths = new ArrayList<>(Collections.nCopies(DefenceField.entranceCount(), null));

    @In
    private PathfinderSystem pathfinderSystem;
    @In
    private BlockManager blockManager;

    private EntityRef shrineEntity;

    private boolean fieldActivated;

    private Block airBlock;

    @Override
    public void initialise() {
        airBlock = blockManager.getBlock(BlockManager.AIR_ID);
    }

    @ReceiveEvent
    public void onChangedBlock(OnChangedBlock event, EntityRef entity) {
        if (event.getNewType() == airBlock || event.getOldType() == airBlock) {
            calculatePaths();
        }
    }

    @ReceiveEvent
    public void onActivate(ActivateEvent event, EntityRef entity) {
        if (!fieldActivated) {
            shrineEntity = entity;
            setupWorld();
        }
    }

    /**
     * Initialises the defence field
     */
    public void setupWorld() {
        logger.info("Setting up the world");
        fieldActivated = true;
        calculatePaths();
    }

    /**
     * Calculate the path from an entrance to the centre
     *
     * @param id The entrance to calculate from
     */
    public void calculatePath(int id) {
        pathfinderSystem.requestPath(
                DefenceField.entrancePos(id), DefenceField.fieldCentre(), (List<Vector3i> path, Vector3i target) -> {
                    logger.info(path.toString());
                    paths.set(id, path);
                });
    }

    /**
     * Calculate paths from all the entrances to the centre.
     */
    public void calculatePaths() {
        for (int id = 0; id < DefenceField.entranceCount(); id++) {
            calculatePath(id);
        }
    }

    /**
     * @return All paths from entrance to centre
     */
    public List<List<Vector3i>> getPaths() {
        return paths;
    }
}
