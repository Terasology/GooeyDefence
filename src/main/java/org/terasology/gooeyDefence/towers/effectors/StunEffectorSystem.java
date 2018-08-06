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
package org.terasology.gooeyDefence.towers.effectors;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.gooeyDefence.DefenceField;
import org.terasology.gooeyDefence.DefenceUris;
import org.terasology.gooeyDefence.components.GooeyComponent;
import org.terasology.gooeyDefence.movement.components.BlankPathComponent;
import org.terasology.gooeyDefence.movement.components.PathComponent;
import org.terasology.gooeyDefence.towers.events.ApplyEffectEvent;
import org.terasology.gooeyDefence.visuals.InWorldRenderer;
import org.terasology.logic.delay.DelayManager;
import org.terasology.logic.delay.DelayedActionTriggeredEvent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3f;
import org.terasology.registry.In;
import org.terasology.utilities.random.FastRandom;
import org.terasology.utilities.random.Random;

import java.util.HashMap;
import java.util.Map;

/**
 * Briefly pauses an enemy.
 * Does this by swapping the path component for the blank one.
 */
@RegisterSystem
public class StunEffectorSystem extends BaseComponentSystem {
    private static final String REMOVE_STUN_ID = "removeStun";
    private Map<EntityRef, PathComponent> pathStorage = new HashMap<>();

    @In
    private DelayManager delayManager;
    @In
    private Random random = new FastRandom();

    @In
    private InWorldRenderer inWorldRenderer;

    /**
     * Applies the stun effect to a target
     * <p>
     * Filters on {@link StunEffectorComponent}
     * Sent against the effector block
     *
     * @see ApplyEffectEvent
     */
    @ReceiveEvent
    public void onApplyEffect(ApplyEffectEvent event, EntityRef entity, StunEffectorComponent component) {
        EntityRef target = event.getTarget();
        if (!pathStorage.containsKey(target) && canStun(event.getDamageMultiplier())) {

            PathComponent pathComponent = DefenceField.getComponentExtending(target, PathComponent.class);
            pathStorage.put(target, pathComponent);
            target.removeComponent(pathComponent.getClass());

            Vector3f position = target.getComponent(LocationComponent.class).getWorldPosition();
            target.addComponent(new BlankPathComponent(position));
            delayManager.addDelayedAction(target, REMOVE_STUN_ID, component.stunDuration);
            inWorldRenderer.addParticleEffect(target, DefenceUris.STUN_PARTICLES);
        }
    }

    private boolean canStun(float damageMultiplier) {
        float stunResult = random.nextFloat();
        return stunResult <= 0.4 * damageMultiplier;
    }

    /**
     * Removes the stun from the enemy
     * <p>
     * Filters on {@link GooeyComponent}
     * Sent against the enemy
     *
     * @see DelayedActionTriggeredEvent
     */
    @ReceiveEvent(components = GooeyComponent.class)
    public void onDelayedActionTriggered(DelayedActionTriggeredEvent event, EntityRef entity) {
        if (event.getActionId().equals(REMOVE_STUN_ID)) {
            PathComponent pathComponent = pathStorage.remove(entity);
            entity.removeComponent(DefenceField.getComponentExtending(entity, PathComponent.class).getClass());
            entity.addComponent(pathComponent);
            inWorldRenderer.removeParticleEffect(entity, DefenceUris.STUN_PARTICLES);
        }
    }
}
