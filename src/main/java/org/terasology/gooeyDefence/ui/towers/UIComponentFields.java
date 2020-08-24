// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.gooeyDefence.ui.towers;

import org.joml.Vector2i;
import org.terasology.gooeyDefence.ui.componentParsers.BaseParser;
import org.terasology.nui.Canvas;
import org.terasology.nui.Color;
import org.terasology.nui.CoreWidget;
import org.terasology.nui.HorizontalAlign;
import org.terasology.nui.VerticalAlign;
import org.terasology.nui.asset.font.Font;
import org.terasology.nui.databinding.Binding;
import org.terasology.nui.databinding.DefaultBinding;
import org.terasology.nui.util.RectUtility;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays the fields, values and current upgrade for a component.
 *
 * @see TowerInfoSystem
 * @see BaseParser
 * @see UIUpgrader
 */
public class UIComponentFields extends CoreWidget {
    /**
     * The spacing between the columns of values.
     */
    private static final int SPACING = 10;
    private Binding<List<String>> fields = new DefaultBinding<>(new ArrayList<>());
    private Binding<List<String>> values = new DefaultBinding<>(new ArrayList<>());
    private Binding<List<String>> upgrades = new DefaultBinding<>(new ArrayList<>());
    private Binding<Boolean> showUpgrade = new DefaultBinding<>(false);

    @Override
    public void onDraw(Canvas canvas) {
        Font font = canvas.getCurrentStyle().getFont();
        List<String> list = fields.get();

        if (list != null) {
            int offset = (canvas.size().x - getPreferredContentSize(canvas, canvas.size()).x) / 2;
            Vector2i listSize = font.getSize(list);
            canvas.drawTextRaw(String.join("\n", list),
                    font,
                    Color.WHITE,
                    RectUtility.createFromMinAndSize(new Vector2i(offset, 0), listSize));

            list = values.get();
            offset += listSize.x + SPACING;
            listSize = font.getSize(list);
            canvas.drawTextRaw(String.join("\n", values.get()),
                    font,
                    Color.WHITE,
                    RectUtility.createFromMinAndSize(new Vector2i(offset, 0), listSize),
                    HorizontalAlign.CENTER,
                    VerticalAlign.TOP);

            if (showUpgrade.get()) {
                list = upgrades.get();
                offset += listSize.x + SPACING;
                listSize = font.getSize(list);
                canvas.drawTextRaw(String.join("\n", list),
                        font,
                        Color.GREEN,
                        RectUtility.createFromMinAndSize(new Vector2i(offset, 0), listSize),
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

            Vector2i fieldSize = font.getSize(fieldList);
            Vector2i valueSize = font.getSize(values.get());

            Vector2i upgradeSize = new Vector2i();
            List<String> upgradeList = upgrades.get();
            if (upgradeList != null) {
                upgradeSize = font.getSize(upgradeList);
            }

            int width = fieldSize.x + valueSize.x + upgradeSize.x + 2 * SPACING;
            int height = Math.max(Math.max(fieldSize.y, valueSize.y), upgradeSize.y);
            return new Vector2i(width, height);
        }
        return new Vector2i();
    }

    /**
     * @param fieldBinding The binding to use for the field names
     */
    public void bindFields(Binding<List<String>> fieldBinding) {
        fields = fieldBinding;
    }

    /**
     * @param valueBinding The binding to use for component values.
     */
    public void bindValues(Binding<List<String>> valueBinding) {
        values = valueBinding;
    }

    /**
     * @param upgradeBinding The binding to use for upgrade values.
     */
    public void bindUpgrade(Binding<List<String>> upgradeBinding) {
        upgrades = upgradeBinding;
    }

    /**
     * @param showBinding The binding to use for if the upgrades should be displayed.
     */
    public void bindShowUpgrade(Binding<Boolean> showBinding) {
        showUpgrade = showBinding;
    }

}
