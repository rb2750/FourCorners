package com.rb2750.lwjgl.gui;

import com.rb2750.lwjgl.input.InputManager;
import com.rb2750.lwjgl.input.controllers.*;
import com.rb2750.lwjgl.world.World;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class GUIManager {
    @Getter
    private List<GUI> displayedGUIs = new ArrayList<>();

    public void displayGUI(GUI gui) {
        if (!guiExists(gui.getClass())) {
            InputManager.registerInputListener(gui);
            displayedGUIs.add(gui);
        }
    }

    public boolean guiExists(Class<? extends GUI> gui) {
        return getGUI(gui) != null;
    }

    private GUI getGUI(Class<? extends GUI> gui) {
        for (GUI instance : new ArrayList<>(displayedGUIs)) {
            if (instance.getClass().equals(gui)) {
                return instance;
            }
        }
        return null;
    }

    public void hideGUI(World world, Class<? extends GUI> gui) {
        GUI current = getGUI(gui);
        if (current == null) return;
        current.hide(world);
        displayedGUIs.remove(current);
    }

    public void render(World world) {
        for (GUI gui : displayedGUIs) {
            gui.draw(world);
        }
    }
}
