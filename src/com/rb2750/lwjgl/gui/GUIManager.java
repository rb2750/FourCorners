package com.rb2750.lwjgl.gui;

import lombok.Getter;
import se.albin.steamcontroller.SteamController;

import java.util.ArrayList;
import java.util.List;

public class GUIManager {
    @Getter
    private List<GUI> displayedGUIs = new ArrayList<>();

    public void displayGUI(GUI gui) {
        if (!displayedGUIs.contains(gui)) displayedGUIs.add(gui);
    }

    public void hideGUI(Class<? extends GUI> gui) {
        for (GUI instance : new ArrayList<>(displayedGUIs)) {
            if (instance.getClass().equals(gui)) {
                displayedGUIs.remove(instance);
            }
        }
    }

    public void update() {
        for (GUI gui : displayedGUIs) {
            gui.draw();
        }
    }

    public void handleInput(SteamController state, SteamController last) {
        for (GUI gui : displayedGUIs) {
            gui.handleInput(state, last);
        }
    }
}
