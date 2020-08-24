// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.ui.hud;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.gooeyDefence.DefenceUris;
import org.terasology.gooeyDefence.StatSystem;
import org.terasology.gooeyDefence.waves.OnWaveEnd;
import org.terasology.math.geom.Rect2f;
import org.terasology.nui.databinding.ReadOnlyBinding;
import org.terasology.nui.widgets.UIIconBar;
import org.terasology.registry.In;
import org.terasology.rendering.nui.NUIManager;
import org.terasology.rendering.nui.layers.hud.HealthHud;

/**
 * Manages displaying and setting all the hud elements.
 *
 * @see DefenceHud
 */
@RegisterSystem
public class DefenceHudManager extends BaseComponentSystem {
    @In
    private NUIManager nuiManager;

    @In
    private StatSystem statSystem;

    private DefenceHud defenceHud;

    @Override
    public void postBegin() {
        defenceHud = nuiManager.getHUD().addHUDElement(DefenceUris.DEFENCE_HUD, DefenceHud.class, Rect2f.createFromMinAndSize(0, 0, 1, 1));
        HealthHud healthHud = nuiManager.getHUD().getHUDElement(DefenceUris.HEALTH_HUD, HealthHud.class);
        UIIconBar healthBar = healthHud.find("healthBar", UIIconBar.class);

        defenceHud.updateCurrentWave();

        healthBar.bindMaxValue(new ReadOnlyBinding<Float>() {
            @Override
            public Float get() {
                return (float) statSystem.getMaxHealth();
            }
        });
        healthBar.bindValue(new ReadOnlyBinding<Float>() {
            @Override
            public Float get() {
                return (float) statSystem.getShrineHealth();
            }
        });
    }

    /**
     * Updates the wave displayed in the HUD
     * <p>
     * Called when a wave is ended
     *
     * @see OnWaveEnd
     */
    @ReceiveEvent
    public void onWaveEnd(OnWaveEnd event, EntityRef entity) {
        defenceHud.updateCurrentWave();
    }
}
