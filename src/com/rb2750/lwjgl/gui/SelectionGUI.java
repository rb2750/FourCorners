package com.rb2750.lwjgl.gui;

import com.rb2750.lwjgl.Main;
import com.rb2750.lwjgl.util.Util;
import se.albin.steamcontroller.SteamController;

public class SelectionGUI extends GUI {
    @Override
    void draw() {
        Util.drawCircle((double) Main.getGameWidth() / 2, (double) Main.getGameHeight() / 2, 50);
    }

    @Override
    void handleInput(SteamController state, SteamController last) {

    }
}
