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

import org.terasology.gooeyDefence.upgrading.UpgradeInfo;
import org.terasology.math.geom.Vector2i;
import org.terasology.rendering.assets.font.Font;
import org.terasology.rendering.nui.Canvas;
import org.terasology.rendering.nui.CoreWidget;
import org.terasology.rendering.nui.TextLineBuilder;
import org.terasology.rendering.nui.databinding.Binding;
import org.terasology.rendering.nui.databinding.DefaultBinding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UIComponentFields extends CoreWidget {
    private Binding<Map<String, String>> fields = new DefaultBinding<>(new HashMap<>());
    private Binding<UpgradeInfo> upgrade = new DefaultBinding<>();
    private Binding<Boolean> showUpgrade = new DefaultBinding<>(false);

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawText(buildText());
    }

    /**
     * Combines the map of fields into a single string to display
     *
     * @return The contents of the fields as a string
     */
    private String buildText() {
        StringBuilder result = new StringBuilder();
        Map<String, Number> upgradeValues = showUpgrade.get() ? upgrade.get().getValues() : new HashMap<>();

        for (Map.Entry<String, String> entry : fields.get().entrySet()) {
            result.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue());
            if (showUpgrade.get() && upgradeValues.containsKey(entry.getKey())) {
                String value = String.valueOf(upgradeValues.get(entry.getKey()));
                result.append(value.startsWith("-") ? "    " : "    +")
                        .append(value);
            }
            result.append('\n');
        }
        return result.toString();
    }

    @Override
    public Vector2i getPreferredContentSize(Canvas canvas, Vector2i sizeHint) {
        Font font = canvas.getCurrentStyle().getFont();
        List<String> lines = TextLineBuilder.getLines(font, buildText(), sizeHint.x);
        return font.getSize(lines);
    }

    /**
     * Set the binding to use for the field values
     *
     * @param fieldBinding The binding to use
     */
    public void bindFields(Binding<Map<String, String>> fieldBinding) {
        this.fields = fieldBinding;
    }

    public void bindUpgrade(Binding<UpgradeInfo> upgradeBinding) {
        upgrade = upgradeBinding;
    }

    public void bindShowUpgrade(Binding<Boolean> showBinding) {
        showUpgrade = showBinding;
    }
}
