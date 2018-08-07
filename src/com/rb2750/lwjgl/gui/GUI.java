package com.rb2750.lwjgl.gui;

import com.rb2750.lwjgl.input.InputListener;
import com.rb2750.lwjgl.world.World;

public abstract class GUI implements InputListener {
    abstract void draw(World world);

    abstract void hide(World world);
}
