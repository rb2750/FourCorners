package com.rb2750.lwjgl.entities;

import com.rb2750.lwjgl.util.Location;
import org.joml.Vector4f;

public class OneWayTile extends Tile {
    public OneWayTile() {
        super();
        setBaseColour(new Vector4f(255, 120, 120, 255));
    }

    public OneWayTile(Location location) {
        super(location);
    }

    @Override
    public boolean onInteract(Entity x, Entity y) {
        return y == null && x != null && x.acceleration.x < 0;
    }
}
