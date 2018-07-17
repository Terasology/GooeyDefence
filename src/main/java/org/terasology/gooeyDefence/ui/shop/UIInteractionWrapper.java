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
import org.terasology.rendering.nui.Canvas;
import org.terasology.rendering.nui.CoreWidget;
import org.terasology.rendering.nui.UIWidget;

import java.util.Iterator;

public class UIInteractionWrapper extends CoreWidget {
    private UIWidget content;

    public void setContent(UIWidget content) {
        this.content = content;
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawWidget(content);
    }

    @Override
    public Vector2i getPreferredContentSize(Canvas canvas, Vector2i sizeHint) {
        return canvas.calculatePreferredSize(content);
    }

    @Override
    public Iterator<UIWidget> iterator() {
        return content.iterator();
    }

    @Override
    public Vector2i getMaxContentSize(Canvas canvas) {
        return content.getMaxContentSize(canvas);
    }
}
