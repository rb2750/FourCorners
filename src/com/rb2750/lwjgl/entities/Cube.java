package com.rb2750.lwjgl.entities;


import com.rb2750.lwjgl.graphics.*;
import com.rb2750.lwjgl.util.Location;
import com.rb2750.lwjgl.util.Size;

public class Cube extends Entity {
    public Cube(Location location) {
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
        texturePath = "res/textures/red.png";

        createMesh();
    }

    @Override
    public void update(Camera camera) {
        //if (mesh != null) return;
        if(mesh == null) {

        }

        super.update(camera);
    }

    //    @Override
//    public void renderEntity(Camera camera) {
////        rotate(5);
//        drawCube(getLocation().getX(), getLocation().getY(), 500, getSize().getWidth(), getSize().getHeight(), getSize().getHeight());
//    }
}
