package com.rb2750.lwjgl.gui;

import com.rb2750.lwjgl.Main;
import com.rb2750.lwjgl.entities.*;
import com.rb2750.lwjgl.graphics.DisplayObject;
import com.rb2750.lwjgl.input.InputManager;
import com.rb2750.lwjgl.input.controllers.*;
import com.rb2750.lwjgl.util.Location;
import com.rb2750.lwjgl.util.Size;
import com.rb2750.lwjgl.world.World;
import org.joml.*;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

public class SelectionGUI extends GUI {
    private CircularSector[] sectors = new CircularSector[4];
    private Vector2f selectorLocation = new Vector2f(Float.MAX_VALUE, Float.MAX_VALUE);
    private float circleRadius = 250;
    private Vector2f circleLocation = new Vector2f((float) Main.getGameWidth() / 2f, (float) Main.getGameHeight() / 2f);
    private Circle selector;
    private Vector4f selectColor = new Vector4f(60, 179, 113, 255);
    private Vector4f defaultColor = new Vector4f(220, 220, 220, 255);
    private DisplayObject[] objects = new DisplayObject[]{new BouncyTile(), new Tile(), new Tile().setBaseColour(new Vector4f(0, 255, 255, 255)), new Tile().setBaseColour(new Vector4f(0, 0, 255, 255))};
    private List<DisplayObject> misc = new ArrayList<>();
    private static int selectedSection = -1;
    private static int mouseSection = -1;

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
            selector.setBaseColour(new Vector4f(150, 150, 150, 200));
            selector.size = new Size(15, 15);
            selector.setLayer(2000);
            world.addDisplayObject(selector);
        }
        selector.teleport(new Location(world, selectorLocation));


        for (int i = 0; i < sectors.length; i++) {
            CircularSector sector = sectors[i];
            if (i == selectedSection) sector.setBaseColour(new Vector4f(defaultColor).sub(30, 30, 30, 0f));
            else if (i == getTouchedSection()) sector.setBaseColour(selectColor);
            else sector.setBaseColour(defaultColor);
        }
    }

    private int getTouchedSection() {
        if (mouseSection != -1) return mouseSection;

        if (selectorLocation.x < circleLocation.x) if (selectorLocation.y > circleLocation.y) return 0;
        else return 3;
        else if (selectorLocation.y > circleLocation.y) return 1;
        else return 2;
    }

    private void displayObject(World world, DisplayObject object, int angle) {
        Location circleLoc = new Location(world, circleLocation);

        Vector2f base = new Vector2f((float) (circleRadius * Math.sin(Math.toRadians(angle))), (float) (circleRadius * Math.cos(Math.toRadians(angle))));
        base.mul(0.6f);
        base.x -= object.size.getWidth() / 2;
        base.y -= object.size.getHeight() / 2;

        object.teleport(circleLoc.clone().add(base.x, base.y));
        Circle circle = new Circle(object.getLocation().clone().add(object.size.getWidth() / 2f, object.size.getHeight() / 2f));
        circle.size = object.size.clone().subtract(new Size(12, 12));
        circle.setBaseColour(new Vector4f(defaultColor).sub(20, 20, 20, 0));
        circle.setLayer(120);
        object.setLayer(130);
        world.addDisplayObject(object);
        world.addDisplayObject(circle);
        misc.add(circle);
    }

    private void addSector(World world, Vector2f location, int zrot, int index) {
        CircularSector sector = new CircularSector(new Location(world, location.x, location.y), defaultColor);
        sector.size = new Size(circleRadius, circleRadius);
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

        InputManager.unregisterInputListener(this);
    }

    private void select() {
        int section = getTouchedSection();
        if (section == selectedSection) return;
        Main.instance.getPlayer().getWorld().removeDisplayObject(Main.selectedObject);
        Main.selectedObject = ((Entity) objects[section]).clone();
        Main.selectedObject.setLayer(-40f);
        Main.selectedObject.getBaseColour().w = 100;
        Main.instance.getPlayer().getWorld().addDisplayObject(Main.selectedObject);
        selectedSection = section;
    }

    @Override
    public void handleControllerInput(Controller state, Controller last) {
        float selectorX = (float) (circleRadius * state.getAnalogLeft().x() + circleLocation.x);
        float selectorY = (float) (circleRadius * state.getAnalogLeft().y() + circleLocation.y);

        selectorLocation = new Vector2f(selectorX, selectorY);
        mouseSection = -1;

        if (state.isLeftPadPressed()) select();
    }

    @Override
    public void handleKeyboardInput(Keyboard keyboard) {

    }

    @Override
    public void handleMouseInput(Mouse mouse) {
        if (mouse.getScrollDy() > 0) mouseSection += 1;
        if (mouse.getScrollDy() < 0) {
            mouseSection -= 1;
            if (mouseSection < 0) mouseSection = 3;
        }
        mouseSection %= 4;

        if (mouse.isMiddleMouseDown()) select();
    }
}
