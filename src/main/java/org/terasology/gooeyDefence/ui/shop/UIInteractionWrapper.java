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
package org.terasology.gooeyDefence.ui.shop;

import org.terasology.math.geom.Vector2i;
import org.terasology.rendering.nui.BaseInteractionListener;
import org.terasology.rendering.nui.Canvas;
import org.terasology.rendering.nui.CoreWidget;
import org.terasology.rendering.nui.InteractionListener;
import org.terasology.rendering.nui.UIWidget;
import org.terasology.rendering.nui.databinding.DefaultBinding;
import org.terasology.rendering.nui.events.NUIMouseClickEvent;
import org.terasology.rendering.nui.skin.UISkin;
import org.terasology.rendering.nui.widgets.ActivateEventListener;
import org.terasology.rendering.nui.widgets.TooltipLine;
import org.terasology.rendering.nui.widgets.TooltipLineRenderer;
import org.terasology.rendering.nui.widgets.UIList;
import org.terasology.utilities.Assets;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper widget used in the shop screen.
 * Displays a separate widget whilst capturing user interaction with it.
 * <p>
 * All size values etc etc are determined by the content widget.
 */
public class UIInteractionWrapper extends CoreWidget {
    private UIWidget content;
    private ActivateEventListener listener;
    private final UIList<TooltipLine> tooltip;

    private final InteractionListener interactionListener = new BaseInteractionListener() {
        @Override
        public boolean onMouseClick(NUIMouseClickEvent event) {
            if (listener != null) {
                listener.onActivated(UIInteractionWrapper.this);
            }
            return true;
        }
    };

    public UIInteractionWrapper() {
        tooltip = new UIList<>();
        tooltip.setInteractive(false);
        tooltip.setSelectable(false);
        final UISkin defaultSkin = Assets.getSkin("core:itemTooltip").get();
        tooltip.setSkin(defaultSkin);
        tooltip.setItemRenderer(new TooltipLineRenderer(defaultSkin));
        tooltip.bindList(new DefaultBinding<>(new ArrayList<>()));
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawWidget(content);
        canvas.addInteractionRegion(interactionListener);
    }

    @Override
    public Vector2i getPreferredContentSize(Canvas canvas, Vector2i sizeHint) {
        return canvas.calculatePreferredSize(content);
    }

    @Override
    public Vector2i getMaxContentSize(Canvas canvas) {
        return content.getMaxContentSize(canvas);
    }

    @Override
    public UIWidget getTooltip() {
        if (tooltip.getList().size() > 0) {
            return tooltip;
        } else {
            return null;
        }
    }

    /**
     * @param content The widget to display.
     */
    public void setContent(UIWidget content) {
        this.content = content;
    }

    /**
     * @param lines The tooltip lines to display
     */
    public void setTooltipLines(List<TooltipLine> lines) {
        tooltip.setList(lines);
    }


    /**
     * @param listener The listener to use when this widget is clicked.
     */
    public void setListener(ActivateEventListener listener) {
        this.listener = listener;
    }
}
