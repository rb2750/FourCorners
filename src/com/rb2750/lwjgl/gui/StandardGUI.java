package com.rb2750.lwjgl.gui;

import com.rb2750.lwjgl.Main;
import com.rb2750.lwjgl.entities.Tile;
import com.rb2750.lwjgl.graphics.DisplayObject;
import com.rb2750.lwjgl.gui.fonts.fontcreator.GUIText;
import com.rb2750.lwjgl.input.controllers.*;
import com.rb2750.lwjgl.util.Location;
import com.rb2750.lwjgl.util.Size;
import com.rb2750.lwjgl.world.World;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class StandardGUI extends GUI {
    private static final float buttonSpacing = 30f;
    private List<DisplayObject> objects = new ArrayList<>();
    private List<GUIText> text = new ArrayList<>();
    private List<DisplayObject> buttons = new ArrayList<>();
    private Tile basePlate;
    private Vector2f basePlateLocation = new Vector2f(100, 200);
    private Size basePlateSize = null;
    private Size buttonSize = new Size(0, 70);
    private float sideSpacing = 0f;
    private GUIOption[] options;
    private int selectedOption = 0;

    public StandardGUI(GUIOption... options) {
        this.options = options;
    }

    @Override
    void draw(World world) {
        if (basePlateSize == null) basePlateSize = new Size(Main.getGameWidth() / 5f, Main.getGameHeight() / 2f, 1);
        sideSpacing = basePlateSize.getWidth() / 8f;
        buttonSize.setWidth(basePlateSize.getWidth() - (sideSpacing * 2));

        if (basePlate == null) createBasePlate(world);

        updateBasePlateLocation(world);

        if (buttons.isEmpty()) {
            for (GUIOption option : options) addOption(world, option.option);

            float buttonHeight = 0;

            for (DisplayObject button : buttons) buttonHeight += button.getSize().getHeight();

            basePlateSize.setHeight((sideSpacing * 2) + buttonHeight + (buttonSpacing * (buttons.size() - 1)));

            basePlate.size = basePlateSize;
            updateBasePlateLocation(world);
            updateButtonLocations(world);
        }

        for (int i = 0; i < buttons.size(); i++) {
            DisplayObject button = buttons.get(i);
            if (i == buttons.size() - 1 - selectedOption) {
                buttons.get(i).setBaseColour(new Vector4f(50, 205, 50, 255));
            } else buttons.get(i).setBaseColour(new Vector4f(139, 139, 139, 255));
            button.setBorderColour(new Vector4f(button.getBaseColour().x - 40, button.getBaseColour().y - 40, button.getBaseColour().z - 40, 255));
        }
    }

    private void updateBasePlateLocation(World world) {
        basePlateLocation = new Vector2f(Main.getGameWidth() / 2f, Main.getGameHeight() / 2f);
        basePlateLocation.sub(new Vector2f(basePlateSize.getWidth(), basePlateSize.getHeight()).mul(0.5f));
        basePlate.teleport(new Location(world, basePlateLocation));
    }

    @Override
    void hide(World world) {
        for (DisplayObject object : objects) world.removeDisplayObject(object);

        for (GUIText obj : text) obj.remove();
    }

    private void updateButtonLocations(World world) {
        float buttonHeight = 0;
        int buttonNumber = 0;

        for (DisplayObject button : buttons) {
//            button.size = buttonSize;
            button.teleport(new Location(world, new Vector2f(basePlateLocation).add(sideSpacing, sideSpacing + buttonHeight + (buttonSpacing * buttonNumber))));
            text.get(buttonNumber).setPosition(button.getLocation().asVector().add(0, 5)/*sub(0, button.getSize().getHeight() / 2f)*/.mul(1f / Main.getGameWidth(), 1f / Main.getGameHeight()));
            buttonHeight += button.getSize().getHeight();
            buttonNumber += 1;
        }
    }

    private void createBasePlate(World world) {
        basePlate = new Tile(new Location(world, basePlateLocation.x, basePlateLocation.y));
        basePlate.setBorderSize(0.008f);
        basePlate.setBorderColour(new Vector4f(139, 139, 139, 255));
        basePlate.setBaseColour(new Vector4f(198, 198, 198, 255));
        basePlate.setLayer(250);
        basePlate.size = basePlateSize;
        world.addDisplayObject(basePlate);
        objects.add(basePlate);
    }

    private void addOption(World world, String option) {
        Tile button = new Tile(new Location(world, new Vector2f(0, 0)));
        button.size = buttonSize;
        button.setBorderSize(0.015f);
        button.setBaseColour(new Vector4f(139, 139, 139, 255));
        button.setBorderColour(new Vector4f(85, 85, 85, 255));
        button.setLayer(300);
        world.addDisplayObject(button);
        objects.add(button);
        buttons.add(button);

        text.add(new GUIText(option, 2, Main.instance.getFont(), new Vector2f(0.0f, 0.0f), button.size.getWidth() / Main.getGameWidth(), true));
    }

    private void close() {
        Main.instance.getGuiManager().hideGUI(Main.instance.getPlayer().getWorld());
    }

    @Override
    public void handleControllerInput(Controller state, Controller last) {
        if (state.isHomeHeld() && !last.isHomeHeld() || state.isBHeld() && !last.isBHeld())
            close();

        if (last.getAnalogStick().y() == 0 && !buttons.isEmpty()) {
            if (state.getAnalogStick().y() > 0) selectedOption -= 1;
            if (state.getAnalogStick().y() < 0) selectedOption += 1;
            if (selectedOption < 0) selectedOption = buttons.size() - 1;
            selectedOption %= buttons.size();
        }

        if (state.isAHeld() && !last.isAHeld()) {
            Runnable onClick = options[selectedOption].onClick;
            if (onClick != null) onClick.run();
            close();
        }
    }

    @Override
    public void handleKeyboardInput(Keyboard keyboard) {

    }

    @Override
    public void handleMouseInput(Mouse mouse) {

    }

    public static class GUIOption {
        String option;
        @Accessors(chain = true)
        @Setter
        Runnable onClick;

        public GUIOption(String option) {
            this.option = option;
        }
    }
}
