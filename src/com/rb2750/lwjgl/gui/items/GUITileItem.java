package com.rb2750.lwjgl.gui.items;

import com.rb2750.lwjgl.util.Util;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class GUITileItem extends GUIItem {
    @Getter
    private double r;
    @Getter
    private double g;
    @Getter
    private double b;
    @Getter
    private double a;

    @Override
    void draw(double x, double y, double size) {
        Util.glColor(r, g, b, a);
        Util.drawSquare(x, y, size, size);
    }
}
