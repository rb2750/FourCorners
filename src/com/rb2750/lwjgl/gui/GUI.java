package com.rb2750.lwjgl.gui;

import se.albin.steamcontroller.SteamController;

public abstract class GUI {
    public abstract void draw();

    public abstract void handleInput(SteamController state, SteamController last);
}
