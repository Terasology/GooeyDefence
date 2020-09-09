// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.ui.activation;

import org.terasology.engine.rendering.nui.CoreScreenLayer;
import org.terasology.nui.WidgetUtil;
import org.terasology.nui.layouts.relative.RelativeLayout;
import org.terasology.nui.widgets.ActivateEventListener;

/**
 * Screen shown at the start of the game.
 * <p>
 * Allows the world time to load before pathfinding is called. Also allows for information to be shown to the player.
 *
 * @see ActivateScreenSystem
 */
public class ActivateGameScreen extends CoreScreenLayer {
    private RelativeLayout newGameLayout;
    private RelativeLayout resumeGameLayout;

    @Override
    public void initialise() {
        newGameLayout = find("newGameLayout", RelativeLayout.class);
        resumeGameLayout = find("resumeGameLayout", RelativeLayout.class);
    }

    /**
     * @param listener Listener to call when the begin button is pressed
     */
    public void subscribeToBegin(ActivateEventListener listener) {
        WidgetUtil.trySubscribe(this, "beginButton", listener);
    }

    /**
     * @param isNewGame True if this screen is being shown in a new game or a game loaded from save.
     */
    public void setNewGame(boolean isNewGame) {
        newGameLayout.setVisible(isNewGame);
        resumeGameLayout.setVisible(!isNewGame);
    }

}
