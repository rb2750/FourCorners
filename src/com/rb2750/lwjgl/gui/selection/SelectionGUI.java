package com.rb2750.lwjgl.gui.selection;

import com.rb2750.lwjgl.Main;
import com.rb2750.lwjgl.gui.GUI;
import com.rb2750.lwjgl.gui.items.GUIItem;
import com.rb2750.lwjgl.gui.items.GUITileItem;
import com.rb2750.lwjgl.util.Point;
import com.rb2750.lwjgl.util.Util;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;
import se.albin.steamcontroller.SteamController;

import java.util.ArrayList;
import java.util.List;

public class SelectionGUI extends GUI {
    @Getter
    private List<GUIItem> items = new ArrayList<>();
    private int circleRadius = 250;
    private Point selectCirclePoint;

    public SelectionGUI() {
        items.add(new GUITileItem(255, 200, 100, 255));
    }

    /**
     * 1 item means 1 section - Centered
     * 2 items means 2 sections - left and right
     * 3 items means 3 sections - left bottom right
     * 4 items means all 4 sections - left bottom top right
     */

    @Override
    public void draw() {
        Util.glColor(220, 220, 220, 100);
        double circleX = (double) Main.getGameWidth() / 2, circleY = (double) Main.getGameHeight() / 2;
        List<Pair<Double, Double>> triangles = Util.drawCircle(circleX, circleY, circleRadius, 1);
        if (selectCirclePoint != null) {
            Util.glColor(251, 100, 100, 255);
            if (items.size() == 1) {
//                if () {
//
//                    Util.glColor(251, 255, 100, 255);
//                }
                System.out.println(isInside(triangles, circleX, circleY, selectCirclePoint.getX(), selectCirclePoint.getY()));
            }
            Util.drawCircle(selectCirclePoint.getX(), selectCirclePoint.getY(), 20);
        }
    }

    private double area(double x1, double y1, double x2, double y2, double x3, double y3) {
        return Math.abs((x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2)) / 2.0);
    }

    private boolean isInside(List<Pair<Double, Double>> triangles, double centerX, double centerY, double x, double y) {
        for (int i = 0; i < triangles.size() - 1; i++) {
            double x1 = centerX;
            double y1 = centerY;
            double x2 = triangles.get(i).getLeft();
            double y2 = triangles.get(i).getRight();
            double x3 = triangles.get(i + 1).getLeft();
            double y3 = triangles.get(i + 1).getRight();

            double A = area(x1, y1, x2, y2, x3, y3);
            double A1 = area(x, y, x2, y2, x3, y3);
            double A2 = area(x1, y1, x, y, x3, y3);
            double A3 = area(x1, y1, x2, y2, x, y);
            if (A == A1 + A2 + A3) return true;
        }
        return false;
    }

    @Override
    public void handleInput(SteamController state, SteamController last) {
        if (state.isLeftPadTouched())
            selectCirclePoint = new Point(circleRadius * state.getLeftTouchPosition().x() + (double) Main.getGameWidth() / 2, circleRadius * state.getLeftTouchPosition().y() + (double) Main.getGameHeight() / 2);
        else selectCirclePoint = null;
    }
}
