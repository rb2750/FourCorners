package com.rb2750.lwjgl.entities;


import com.rb2750.lwjgl.util.Location;
import com.rb2750.lwjgl.util.Size;

import static com.rb2750.lwjgl.util.Util.drawSquare;

public class Tile extends Entity {
    public Tile(Location location) {
        super(location, new Size(100, 100));
    }

    @Override
    public void renderEntity() {
        drawSquare(getLocation().getX(), getLocation().getY(), getSize().getWidth(), getSize().getHeight());
    }
}
