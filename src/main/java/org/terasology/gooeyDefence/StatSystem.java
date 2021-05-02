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
package org.terasology.gooeyDefence;

import org.terasology.gestalt.assets.management.AssetManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.players.LocalPlayer;
import org.terasology.engine.registry.In;
import org.terasology.engine.registry.Share;
import org.terasology.gooeyDefence.economy.EconomyManager;
import org.terasology.gooeyDefence.events.OnFieldActivated;
import org.terasology.gooeyDefence.health.HealthComponent;

import java.util.Optional;

/**
 * Keeps track of various stats within the game at a global level.
 * This is dynamic information. Static information is provided by the {@link DefenceField} class
 *
 * @see DefenceField
 */
@RegisterSystem
@Share(StatSystem.class)
public class StatSystem extends BaseComponentSystem {
    private int waveNumber;
    private int maxHealth;

    @In
    private AssetManager assetManager;
    @In
    private LocalPlayer localPlayer;
    private EntityRef player = EntityRef.NULL;

    @Override
    public void postBegin() {
        Optional<Prefab> optional = assetManager.getAsset(DefenceUris.SHRINE, Prefab.class);
        maxHealth = optional.map(prefab -> prefab.getComponent(HealthComponent.class))
                .map(component -> component.health)
                .orElse(0);
    }

    /**
     * Used to get the local player's character
     * <p>
     * Sent when the field is activated.
     *
     * @see OnFieldActivated
     */
    @ReceiveEvent
    public void onFieldActivated(OnFieldActivated event, EntityRef entity) {
        player = localPlayer.getCharacterEntity();
    }

    /**
     * Progress the counter to the next wave.
     * It is impossible to go back a wave, hence there is no decrement option.
     */
    public void incrementWave() {
        waveNumber++;
    }

    /**
     * @return The current wave for the game.
     */
    public int getWaveNumber() {
        return waveNumber;
    }

    /**
     * @return The current amount of health the shrine has
     */
    public int getShrineHealth() {
        if (DefenceField.fieldActivated) {
            return DefenceField.getShrineEntity().getComponent(HealthComponent.class).health;
        } else {
            return 0;
        }
    }

    /**
     * @return The maximum amount of health the shrine had
     */
    public int getMaxHealth() {
        return maxHealth;
    }

    /**
     * @return How much money the player has.
     */
    public int getPlayerMoney() {
        return EconomyManager.getBalance(player);
    }
}
