package com.rb2750.lwjgl.entities;


import com.rb2750.lwjgl.graphics.Shader;
import com.rb2750.lwjgl.graphics.Texture;
import com.rb2750.lwjgl.graphics.VertexArray;
import com.rb2750.lwjgl.util.Location;
import com.rb2750.lwjgl.util.Size;

import static com.rb2750.lwjgl.util.Util.drawSquare;

public class Tile extends Entity {
    public Tile(Location location) {
        super(location, new Size(100, 100), Shader.GENERAL);

        float[] vertices = new float[] {
                -1.0f, -1.0f, 0.0f,
                -1.0f,  1.0f, 0.0f,
                1.0f,  1.0f, 0.0f,
                1.0f, -1.0f, 0.0f
        };

        byte[] indices = new byte[] {
                0, 1, 2,
                2, 3, 0
        };

        float[] tcs = new float[] {
                0, 1,
                0, 0,
                1, 0,
                1, 1
        };

        mesh = new VertexArray(vertices, indices, tcs);

        texture = new Texture("res/textures/red.png");
    }

    @Override
    public void renderEntity(Camera camera) {
        drawSquare(getLocation().getX(), getLocation().getY(), getSize().getWidth(), getSize().getHeight());
    }
}
