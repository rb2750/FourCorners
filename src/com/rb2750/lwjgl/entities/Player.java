package com.rb2750.lwjgl.entities;

import com.rb2750.lwjgl.util.Location;
import com.rb2750.lwjgl.util.Size;
import lombok.Getter;
import lombok.Setter;

import static com.rb2750.lwjgl.util.Util.drawCube;

public class Player extends Entity {

    public Player(Location location) {
        super(location, new Size(100, 100));
        setGravity(true);
    }

    @Override
    public void renderEntity() {
        drawCube(getLocation().getX(), getLocation().getY(), 0.5f, getSize().getWidth(), getSize().getHeight(), getSize().getWidth());
    }
}
