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
package org.terasology.gooeyDefence.economy;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.EventPriority;
import org.terasology.engine.entitySystem.event.Priority;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.inventory.events.GiveItemEvent;
import org.terasology.engine.registry.Share;
import org.terasology.gooeyDefence.DefenceUris;

/**
 * Handles the requirements of the economy subsystem.
 * Contains methods to operate on entities in the system
 */
@RegisterSystem
@Share(EconomyManager.class)
public class EconomyManager extends BaseComponentSystem {

    /**
     * Attempts to add money to an entities wallet.
     *
     * @param destination The entity to add the money to
     * @param amount      The amount of money to add
     * @return True if the transaction was successful, false otherwise.
     */
    private static boolean tryAddMoney(EntityRef destination, int amount) {
        WalletComponent component = destination.getComponent(WalletComponent.class);
        if (component != null) {
            component.funds = component.funds + amount;
            return true;
        }
        return false;
    }

    /**
     * Attempts to remove money from an entities wallet.
     * <p>
     * If the transaction would put the entity into the negatives, then the transaction will not occur.
     *
     * @param source The entity to add the money to
     * @param amount The amount of money to remove
     * @return True if the transaction was successful, false otherwise.
     */
    public static boolean tryRemoveMoney(EntityRef source, int amount) {
        WalletComponent component = source.getComponent(WalletComponent.class);
        if (component != null && checkBalance(source, amount)) {
            component.funds = component.funds - amount;
            return true;
        }
        return false;
    }

    /**
     * Checks if an entity has at least a certain level of money
     *
     * @param source    The entity to check
     * @param threshold The minimum level to check for
     * @return True if the entity has enough money
     */
    private static boolean checkBalance(EntityRef source, int threshold) {
        return getBalance(source) >= threshold;
    }

    /**
     * Get the balance of an entities walled
     *
     * @param source The entity to get the balance on
     * @return How much money the enemy has. -1 if the enemy has no wallet
     */
    public static int getBalance(EntityRef source) {
        WalletComponent component = source.getComponent(WalletComponent.class);
        if (component != null) {
            return component.funds;
        } else {
            return -1;
        }
    }

    /**
     * Called when money is being picked up by the player. Converts it into balance in their walled
     * <p>
     * Sent against the item being given
     * Filters on {@link ValueComponent}
     * Priority must be high in order to retrieve the event before the money is placed into the inventory
     *
     * @see GiveItemEvent
     */
    @Priority(EventPriority.PRIORITY_HIGH)
    @ReceiveEvent
    public void onGiveItem(GiveItemEvent event, EntityRef entity, ValueComponent valueComponent) {
        if (entity.getParentPrefab().getName().equals(DefenceUris.MONEY_ITEM)) {
            boolean success = tryAddMoney(event.getTargetEntity(), valueComponent.value);
            event.setHandled(success);
            entity.destroy();
        }
    }
}
