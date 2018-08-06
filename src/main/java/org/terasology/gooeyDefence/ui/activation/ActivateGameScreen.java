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
package org.terasology.gooeyDefence.ui.activation;

import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.rendering.nui.layouts.relative.RelativeLayout;
import org.terasology.rendering.nui.widgets.ActivateEventListener;
import org.terasology.rendering.nui.widgets.UIButton;

public class ActivateGameScreen extends CoreScreenLayer {
    private UIButton beginButton;
    private RelativeLayout newGameLayout;
    private RelativeLayout resumeGameLayout;

    @Override
    public void initialise() {
        beginButton = find("beginButton", UIButton.class);

        newGameLayout = find("newGameLayout", RelativeLayout.class);
        resumeGameLayout = find("resumeGameLayout", RelativeLayout.class);
    }

    public void subscribeToBegin(ActivateEventListener listener) {
        beginButton.subscribe(listener);
    }

    public void setNewGame(boolean isNewGame) {
        newGameLayout.setVisible(isNewGame);
        resumeGameLayout.setVisible(!isNewGame);
    }

}
