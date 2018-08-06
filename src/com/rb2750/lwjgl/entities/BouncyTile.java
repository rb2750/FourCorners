package com.rb2750.lwjgl.entities;

import com.rb2750.lwjgl.animations.StretchAnimation;
import com.rb2750.lwjgl.util.Location;
import org.joml.Vector4f;

public class BouncyTile extends Tile {
    public BouncyTile() {
        super();
        setBaseColour(new Vector4f(255, 0, 255, 255));
    }

    public BouncyTile(Location location) {
        super(location);
    }

    @Override
    public void onInteract(Entity x, Entity y) {
        if (y != null) {
            if (y.getAcceleration().y < -7) {
                if (y.getAcceleration().y < -25) y.addAnimation(new StretchAnimation());
                y.getAcceleration().y *= -0.75;
            }
        }
    }
}
