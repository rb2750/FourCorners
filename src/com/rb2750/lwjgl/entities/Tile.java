package com.rb2750.lwjgl.entities;

import com.rb2750.lwjgl.graphics.Shader;
import com.rb2750.lwjgl.util.Location;
import com.rb2750.lwjgl.util.Size;
import org.joml.Vector4f;

public class Tile extends Entity {
    public Tile() {
        super(new Size(100, 100), Shader.BASIC_COLOUR, new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));

        vertices = new float[]{
                0f, 1f, 0f,
                0f, 0f, 0f,
                1f, 0f, 0f,
                1f, 1f, 0f,

                0f, 1f, 1f,
                0f, 0f, 1f,
                1f, 0f, 1f,
                1f, 1f, 1f,

                1f, 1f, 0f,
                1f, 0f, 0f,
                1f, 0f, 1f,
                1f, 1f, 1f,

                0f, 1f, 0f,
                0f, 0f, 0f,
                0f, 0f, 1f,
                0f, 1f, 1f,

                0f, 1f, 1f,
                0f, 1f, 0f,
                1f, 1f, 0f,
                1f, 1f, 1f,

                0f, 0f, 1f,
                0f, 0f, 0f,
                1f, 0f, 0f,
                1f, 0f, 1f
        };

        indices = new int[]{
                0, 1, 3,
                3, 1, 2,
                4, 5, 7,
                7, 5, 6,
                8, 9, 11,
                11, 9, 10,
                12, 13, 15,
                15, 13, 14,
                16, 17, 19,
                19, 17, 18,
                20, 21, 23,
                23, 21, 22

        };

        tcs = new float[]{
                0, 0,
                0, 1,
                1, 1,
                1, 0,
                0, 0,
                0, 1,
                1, 1,
                1, 0,
                0, 0,
                0, 1,
                1, 1,
                1, 0,
                0, 0,
                0, 1,
                1, 1,
                1, 0,
                0, 0,
                0, 1,
                1, 1,
                1, 0,
                0, 0,
                0, 1,
                1, 1,
                1, 0
        };

        normals = calcNormals();

        this.texturePath = "res/textures/red.png";
        createMesh();
    }

    public Tile(Location location) {
        this();
        this.location = location;
    }

//    @Override
//    public void renderEntity(Camera camera) {
//        drawSquare(getLocation().getX(), getLocation().getY(), getSize().getWidth(), getSize().getHeight());
//    }
}
