package com.rb2750.lwjgl.entities;


import com.rb2750.lwjgl.graphics.*;
import com.rb2750.lwjgl.util.Location;
import com.rb2750.lwjgl.util.Size;

public class Tile extends Entity {


    public Tile(Location location) {
        super(location, new Size(100, 100), Shader.GENERAL);

        vertices = new float[]{
                0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                1.0f, 1.0f, 0.0f,
                1.0f, 0.0f, 0.0f
        };

        indices = new byte[]{
                0, 1, 2,
                2, 3, 0
        };

        tcs = new float[]{
                0, 1,
                0, 0,
                1, 0,
                1, 1
        };
        this.texturePath = "res/textures/red.png";
        createMesh();
    }

    @Override
    public void update(Camera camera) {
        super.update(camera);
    }
//    @Override
//    public void renderEntity(Camera camera) {
//        drawSquare(getLocation().getX(), getLocation().getY(), getSize().getWidth(), getSize().getHeight());
//    }
}
