package com.rb2750.lwjgl.gui;

import com.rb2750.lwjgl.input.InputManager;
import com.rb2750.lwjgl.world.World;
import lombok.Getter;

public class GUIManager {
    @Getter
    private GUI displayedGUI;

    public void displayGUI(GUI gui) {
        if (displayedGUI == null) {
            InputManager.registerInputListener(gui);
            displayedGUI = gui;
        }
    }

    public void hideGUI(World world) {
        if (displayedGUI == null) return;
        displayedGUI.hide(world);
        InputManager.unregisterInputListener(displayedGUI);
        displayedGUI = null;
    }

    public void render(World world) {
        if (displayedGUI != null) displayedGUI.draw(world);
    }
}
