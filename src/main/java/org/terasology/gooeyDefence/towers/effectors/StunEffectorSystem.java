// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.towers.effectors;

import org.joml.Vector3f;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.delay.DelayManager;
import org.terasology.engine.logic.delay.DelayedActionTriggeredEvent;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.registry.In;
import org.terasology.engine.utilities.random.FastRandom;
import org.terasology.engine.utilities.random.Random;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.gooeyDefence.DefenceField;
import org.terasology.gooeyDefence.DefenceUris;
import org.terasology.gooeyDefence.components.GooeyComponent;
import org.terasology.gooeyDefence.movement.components.BlankPathComponent;
import org.terasology.gooeyDefence.movement.components.PathComponent;
import org.terasology.gooeyDefence.towers.TowerManager;
import org.terasology.gooeyDefence.towers.components.TowerTargeter;
import org.terasology.gooeyDefence.towers.events.ApplyEffectEvent;
import org.terasology.gooeyDefence.visuals.InWorldRenderer;

import java.util.HashMap;
import java.util.Map;

/**
 * Briefly pauses an enemy.
 * Does this by swapping the path component for the blank one.
 *
 * @see StunEffectorComponent
 * @see BlankPathComponent
 * @see TowerManager
 */
@RegisterSystem
public class StunEffectorSystem extends BaseComponentSystem {
    /**
     * The id to use for the removal of the stun.
     */
    private static final String REMOVE_STUN_ID = "removeStun";
    /**
     * A list of the path components to re-apply to the enemy once the stun wears off.
     */
    private final Map<EntityRef, PathComponent> pathStorage = new HashMap<>();

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

            Vector3f position = target.getComponent(LocationComponent.class).getWorldPosition(new Vector3f());
            target.addComponent(new BlankPathComponent(position));
            delayManager.addDelayedAction(target, REMOVE_STUN_ID, component.stunDuration);
            inWorldRenderer.addParticleEffect(target, DefenceUris.STUN_PARTICLES);
        }
    }

    /**
     * Runs a check to see if the stun should be applied
     * This is checked against the stun chance and the damage multiplier
     *
     * @param damageMultiplier The multiplier to use
     * @return True if the enemy should be stunned, false otherwise.
     * @see TowerTargeter#getMultiplier()
     */
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
