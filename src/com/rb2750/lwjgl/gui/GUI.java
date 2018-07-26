package com.rb2750.lwjgl.gui;

import se.albin.steamcontroller.SteamController;

abstract class GUI {
    abstract void draw();

    abstract void handleInput(SteamController state, SteamController last);
}
