package com.rb2750.lwjgl.gui;

import com.rb2750.lwjgl.input.controllers.Controller;
import com.rb2750.lwjgl.world.World;

abstract class GUI {
    abstract void draw(World world);

    abstract void hide(World world);

    abstract void handleInput(Controller state, Controller last);
}
