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

import org.terasology.entitySystem.Component;
import org.terasology.gooeyDefence.upgrading.UpgradingSystem;
import org.terasology.math.geom.Vector2i;
import org.terasology.rendering.assets.font.Font;
import org.terasology.rendering.nui.Canvas;
import org.terasology.rendering.nui.CoreWidget;
import org.terasology.rendering.nui.TextLineBuilder;
import org.terasology.rendering.nui.databinding.Binding;
import org.terasology.rendering.nui.databinding.DefaultBinding;

import java.util.List;
import java.util.Map;

public class UIBlockStats extends CoreWidget {
    private UpgradingSystem upgradingSystem;
    private Binding<Component> component = new DefaultBinding<>();

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawText(buildText());
    }

    private String buildText() {
        StringBuilder result = new StringBuilder();
        Map<String, String> fields = upgradingSystem.getComponentValues(component.get());
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            result.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append('\n');
        }
        return result.toString();
    }

    @Override
    public Vector2i getPreferredContentSize(Canvas canvas, Vector2i sizeHint) {
        Font font = canvas.getCurrentStyle().getFont();
        List<String> lines = TextLineBuilder.getLines(font, buildText(), sizeHint.x);
        return font.getSize(lines);
    }


    public void bindComponent(Binding<Component> componentBinding) {
        component = componentBinding;
    }

    public void setUpgradingSystem(UpgradingSystem upgradingSystem) {
        this.upgradingSystem = upgradingSystem;
    }
}
