package com.rb2750.lwjgl.entities;


import com.rb2750.lwjgl.util.Location;
import com.rb2750.lwjgl.util.Size;

import static com.rb2750.lwjgl.util.Util.drawCube;

public class Cube extends Entity {
    public Cube(Location location) {
        super(location,new Size(100, 100));
    }

    @Override
    public void renderEntity() {
//        rotate(5);
        drawCube(getLocation().getX(), getLocation().getY(), 500, getSize().getWidth(), getSize().getHeight(), getSize().getHeight());
    }
}
