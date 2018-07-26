package com.rb2750.lwjgl.entities;


import com.rb2750.lwjgl.util.Location;
import com.rb2750.lwjgl.util.Size;

import static com.rb2750.lwjgl.util.Util.drawSquare;
import static org.lwjgl.opengl.GL11.glColor3d;

public class Tile extends Entity {
    public Tile(Location location) {
        super(location, new Size(100, 100));
    }

    @Override
    public void renderEntity() {
        glColor3d(1, 0, 0);
        drawSquare(getLocation().getX(), getLocation().getY(), getSize().getWidth(), getSize().getHeight());
    }
}
