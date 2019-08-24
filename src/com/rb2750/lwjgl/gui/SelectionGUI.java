package com.rb2750.lwjgl.gui;

import com.rb2750.lwjgl.Main;
import com.rb2750.lwjgl.entities.*;
import com.rb2750.lwjgl.graphics.DisplayObject;
import com.rb2750.lwjgl.input.controllers.*;
import com.rb2750.lwjgl.util.Location;
import com.rb2750.lwjgl.util.Size;
import com.rb2750.lwjgl.world.World;
import org.joml.*;

import java.lang.Math;
import java.util.*;

public class SelectionGUI extends GUI {
    private CircularSector[] sectors = new CircularSector[4];
    private Vector2f selectorLocation = new Vector2f(Float.MAX_VALUE, Float.MAX_VALUE);
    private float circleRadius = 250;
    private Vector2f circleLocation = new Vector2f((float) Main.getGameWidth() / 2f, (float) Main.getGameHeight() / 2f);
    private Circle selector;
    private Vector4f selectColor = new Vector4f(60, 179, 113, 255);
    private Vector4f defaultColor = new Vector4f(220, 220, 220, 255);
    private List<Class<? extends DisplayObject>> objects = Arrays.asList(BouncyTile.class, Tile.class, StartingPoint.class, OneWayTile.class);
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

            for (int i = 0; i < objects.size(); i++) {
                Class<? extends DisplayObject> object = objects.get(i);

                try {
                    displayObject(world, object.newInstance(), -45 + 90 * i);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        if (selector == null) {
            selector = new Circle(new Location(world, circleLocation.x, circleLocation.y));
            selector.setBaseColour(new Vector4f(150, 150, 150, 200));
            selector.absoluteLocation = true;
            selector.size = new Size(15, 15);
            selector.layer = 2000;
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
        base.x -= object.size.width / 2;
        base.y -= object.size.height / 2;

        object.teleport(circleLoc.clone().add(base.x, base.y));
        Circle circle = new Circle(object.getLocation().clone().add(object.size.width / 2f, object.size.height / 2f));
        circle.size = object.size.clone().subtract(new Size(12, 12));
        circle.setBaseColour(new Vector4f(defaultColor).sub(20, 20, 20, 0));
        circle.layer = 120;
        circle.absoluteLocation = true;
        object.layer = 130;
        object.absoluteLocation = true;
        world.addDisplayObject(object);
        world.addDisplayObject(circle);
        misc.add(circle);
        misc.add(object);
    }

    private void addSector(World world, Vector2f location, int zrot, int index) {
        CircularSector sector = new CircularSector(new Location(world, location.x, location.y), defaultColor);
        sector.size = new Size(circleRadius, circleRadius);
        sector.absoluteLocation = true;
        sector.rotate(new Vector3f(0, 0, zrot));
        sector.layer = 100;
        sectors[index] = sector;
        world.addDisplayObject(sector);
    }

    @Override
    void hide(World world) {
        for (CircularSector sector : sectors) {
            world.removeDisplayObject(sector);
        }

        for (DisplayObject displayObject : misc) {
            world.removeDisplayObject(displayObject);
        }

        if (selector != null) world.removeDisplayObject(selector);
    }

    private void select() {
        int section = getTouchedSection();
        if (section == selectedSection) return;
        Main.instance.getPlayer().getWorld().removeDisplayObject(Main.selectedObject);
        try {
            Main.selectedObject = ((Entity) objects.get(section).newInstance());
            Main.selectedObject.teleport(Main.instance.getPlayer().getLocation().clone().setX(Integer.MAX_VALUE).setY(Integer.MAX_VALUE));
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        Main.selectedObject.layer = -40f;
        Main.selectedObject.getBaseColour().w = 100;
        Main.instance.getPlayer().getWorld().addDisplayObject(Main.selectedObject);
        selectedSection = section;
    }

    private void hide() {
        Main.instance.getGuiManager().hideGUI(Main.instance.getPlayer().getWorld());
    }

    @Override
    public void handleControllerInput(Controller state, Controller last) {
        if (state.isBHeld() && !last.isBHeld() || !state.isLeftPadTouched()) hide();

        float selectorX = (float) (circleRadius * state.getAnalogLeft().x() + circleLocation.x);
        float selectorY = (float) (circleRadius * state.getAnalogLeft().y() + circleLocation.y);

        selectorLocation = new Vector2f(selectorX, selectorY);
        mouseSection = -1;

        if (state.isLeftPadPressed()) select();
    }

    @Override
    public void handleKeyboardInput(Keyboard keyboard) {

    }

    private long lastScroll;

    @Override
    public void handleMouseInput(Mouse mouse) {
        if (mouse.getScrollDy() > 0) {
            mouseSection += 1;
            lastScroll = System.currentTimeMillis();
        }
        if (mouse.getScrollDy() < 0) {
            lastScroll = System.currentTimeMillis();
            mouseSection -= 1;
            if (mouseSection < 0) mouseSection = 3;
        }
        mouseSection %= 4;

        if (mouse.isMiddleMouseDown()) select();

        if (lastScroll > 0 && System.currentTimeMillis() - lastScroll > 1200) {
            hide();
        }
    }
}
