package com.rb2750.lwjgl.gui;

import com.rb2750.lwjgl.Main;
import com.rb2750.lwjgl.entities.*;
import com.rb2750.lwjgl.graphics.DisplayObject;
import com.rb2750.lwjgl.input.controllers.Controller;
import com.rb2750.lwjgl.util.Location;
import com.rb2750.lwjgl.util.Size;
import com.rb2750.lwjgl.world.World;
import org.joml.*;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

public class SelectionGUI extends GUI {
    private CircularSector[] sectors = new CircularSector[4];
    private Vector2f selectorLocation = new Vector2f();
    private float circleWidth = 600f;
    private float circleHeight = 600f;
    private Vector2f circleLocation = new Vector2f((float) Main.getGameWidth() / 2f, (float) Main.getGameHeight() / 2f);
    private Circle selector;
    private Vector4f selectColor = new Vector4f(60f / 255f, 179f / 255f, 113f / 255f, 1f);
    private Vector4f defaultColor = new Vector4f(220f / 255f, 220f / 255f, 220f / 255f, 1f);
    private DisplayObject[] objects = new DisplayObject[]{new BouncyTile(), new Tile(), new Tile().setBaseColour(new Vector4f(0, 1, 1, 1)), new Tile().setBaseColour(new Vector4f(0, 0, 1, 1))};
    private List<DisplayObject> misc = new ArrayList<>();
    private int selectedSection = -1;

    @Override
    void draw(World world) {
        if (sectors[0] == null) {
            addSector(world, new Vector2f(circleLocation).add(-100f, -100), 180, 0);
            addSector(world, new Vector2f(circleLocation).add(-100f, 0), 90, 1);
            addSector(world, new Vector2f(circleLocation), 0, 2);
            addSector(world, new Vector2f(circleLocation).add(0, -100), 270, 3);

            for (int i = 0; i < objects.length; i++) {
                DisplayObject object = objects[i];

                displayObject(world, object, -45 + 90 * i);
            }
        }

        if (selector == null) {
            selector = new Circle(new Location(world, circleLocation.x, circleLocation.y));
            selector.size = new Size(10, 10);
            selector.setLayer(2000);
            world.addDisplayObject(selector);
        }
        selector.teleport(new Location(world, selectorLocation));


        for (int i = 0; i < sectors.length; i++) {
            CircularSector sector = sectors[i];
            if (i == selectedSection) sector.setBaseColour(new Vector4f(defaultColor).sub(0.3f, 0.3f, 0.3f, 0f));
            else if (i == getTouchedSection()) sector.setBaseColour(selectColor);
            else sector.setBaseColour(defaultColor);
        }
    }

    private int getTouchedSection() {
        if (selectorLocation.x < circleLocation.x) if (selectorLocation.y > circleLocation.y) return 0;
        else return 3;
        else if (selectorLocation.y > circleLocation.y) return 1;
        else return 2;
    }

    private void displayObject(World world, DisplayObject object, int angle) {
        Location circleLoc = new Location(world, circleLocation);

        float r = circleWidth / 2;
        Vector2f base = new Vector2f((float) (r * Math.sin(Math.toRadians(angle))), (float) (r * Math.cos(Math.toRadians(angle))));
        base.mul(0.6f);
        base.x -= object.size.getWidth() / 2;
        base.y -= object.size.getHeight() / 2;

        object.teleport(circleLoc.clone().add(base.x, base.y));
        Circle circle = new Circle(object.getLocation().clone().add(object.size.getWidth() / 2f, object.size.getHeight() / 2f));
        circle.size = object.size.clone().subtract(new Size(12, 12));
        circle.setBaseColour(new Vector4f(defaultColor).sub(0.1f, 0.1f, 0.1f, 0));
        circle.setLayer(120);
        object.setLayer(130);
        world.addDisplayObject(object);
        world.addDisplayObject(circle);
        misc.add(circle);
    }

    private void addSector(World world, Vector2f location, int zrot, int index) {
        CircularSector sector = new CircularSector(new Location(world, location.x, location.y), defaultColor);
        sector.size = new Size(circleWidth / 2, circleHeight / 2);
        sector.rotate(new Vector3f(0, 0, zrot));
        sector.setLayer(100);
        sectors[index] = sector;
        world.addDisplayObject(sector);
    }

    @Override
    void hide(World world) {
        for (CircularSector sector : sectors) {
            world.removeDisplayObject(sector);
        }

        for (int i = 0; i < objects.length; i++) {
            DisplayObject object = objects[i];
            world.removeDisplayObject(object);
        }

        for (DisplayObject displayObject : misc) {
            world.removeDisplayObject(displayObject);
        }

        if (selector != null) world.removeDisplayObject(selector);
    }

    @Override
    void handleInput(Controller state, Controller last) {
        float selectorX = (float) ((circleWidth / 2) * state.getAnalogLeft().x() + circleLocation.x);
        float selectorY = (float) ((circleHeight / 2) * state.getAnalogLeft().y() + circleLocation.y);

        selectorLocation = new Vector2f(selectorX, selectorY);

        if (state.isLeftPadPressed()) {
            int section = getTouchedSection();
            if (section == selectedSection) return;
            Main.instance.getPlayer().getWorld().removeDisplayObject(Main.selectedObject);
            Main.selectedObject = ((Entity) objects[section]).clone();
            Main.selectedObject.setLayer(-45f);
            Main.instance.getPlayer().getWorld().addDisplayObject(Main.selectedObject);
            selectedSection = section;
        }
    }
}
