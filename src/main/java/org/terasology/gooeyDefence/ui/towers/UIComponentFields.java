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
package org.terasology.gooeyDefence.ui.towers;

import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2i;
import org.terasology.rendering.assets.font.Font;
import org.terasology.rendering.nui.Canvas;
import org.terasology.rendering.nui.Color;
import org.terasology.rendering.nui.CoreWidget;
import org.terasology.rendering.nui.HorizontalAlign;
import org.terasology.rendering.nui.TextLineBuilder;
import org.terasology.rendering.nui.VerticalAlign;
import org.terasology.rendering.nui.databinding.Binding;
import org.terasology.rendering.nui.databinding.DefaultBinding;

import java.util.ArrayList;
import java.util.List;

public class UIComponentFields extends CoreWidget {
    private Binding<List<String>> fields = new DefaultBinding<>(new ArrayList<>());
    private Binding<List<String>> values = new DefaultBinding<>(new ArrayList<>());
    private Binding<List<String>> upgrades = new DefaultBinding<>(new ArrayList<>());
    private Binding<Boolean> showUpgrade = new DefaultBinding<>(false);

    @Override
    public void onDraw(Canvas canvas) {
        List<String> fieldList = fields.get();
        Vector2i textArea = canvas.size();
        textArea.divX(3);
        if (fieldList != null) {
            canvas.drawTextRaw(String.join("\n", fieldList),
                    canvas.getCurrentStyle().getFont(),
                    Color.WHITE,
                    Rect2i.createFromMinAndSize(Vector2i.zero(), textArea));

            canvas.drawTextRaw(String.join("\n", values.get()),
                    canvas.getCurrentStyle().getFont(),
                    Color.WHITE,
                    Rect2i.createFromMinAndSize(new Vector2i(textArea.x, 0), textArea),
                    HorizontalAlign.CENTER,
                    VerticalAlign.TOP);

            if (showUpgrade.get()) {
                canvas.drawTextRaw(String.join("\n", upgrades.get()),
                        canvas.getCurrentStyle().getFont(),
                        Color.GREEN,
                        Rect2i.createFromMinAndSize(new Vector2i(textArea.x * 2, 0), textArea),
                        HorizontalAlign.RIGHT,
                        VerticalAlign.TOP);
            }
        }
    }

    @Override
    public Vector2i getPreferredContentSize(Canvas canvas, Vector2i sizeHint) {
        List<String> fieldList = fields.get();
        if (fieldList != null) {
            Font font = canvas.getCurrentStyle().getFont();

            String fieldString = String.join("\n", fieldList);
            Vector2i fieldSize = font.getSize(TextLineBuilder.getLines(font, fieldString, sizeHint.x / 3));

            String valueString = String.join("\n", values.get());
            Vector2i valueSize = font.getSize(TextLineBuilder.getLines(font, valueString, sizeHint.x / 3));

            Vector2i upgradeSize = Vector2i.zero();
            List<String> upgradeList = upgrades.get();
            if (upgradeList != null) {
                String upgradeString = String.join("\n", fieldList);
                upgradeSize = font.getSize(TextLineBuilder.getLines(font, upgradeString, sizeHint.x));
            }

            int width = fieldSize.x + valueSize.x + upgradeSize.x;
            int height = Math.max(Math.max(fieldSize.y, valueSize.y), upgradeSize.y);
            return new Vector2i(width, height);
        }
        return Vector2i.zero();
    }

    /**
     * Set the binding to use for the field values
     *
     * @param fieldBinding The binding to use
     */
    public void bindFields(Binding<List<String>> fieldBinding) {
        fields = fieldBinding;
    }

    public void bindValues(Binding<List<String>> valueBinding) {
        values = valueBinding;
    }

    public void bindUpgrade(Binding<List<String>> upgradeBinding) {
        upgrades = upgradeBinding;
    }

    public void bindShowUpgrade(Binding<Boolean> showBinding) {
        showUpgrade = showBinding;
    }
}
