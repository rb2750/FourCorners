package com.rb2750.lwjgl.entities;


import com.rb2750.lwjgl.graphics.*;
import com.rb2750.lwjgl.util.Location;
import com.rb2750.lwjgl.util.Size;
import org.dyn4j.geometry.Geometry;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Cube extends Entity {
    public Cube(Location location) {
        super(location, new Size(100, 100), Shader.BASIC_TEX, new Vector4f(255.0f, 0.0f, 0.0f, 255.0f));

        vertices = new float[]{
                0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                1.0f, 1.0f, 0.0f,
                1.0f, 0.0f, 0.0f
        };

        indices = new int[]{
                0, 1, 2,
                2, 3, 0
        };

        tcs = new float[]{
                0, 1,
                0, 0,
                1, 0,
                1, 1
        };

        normals = calcNormals();
        texturePath = "res/textures/red.png";

        createMesh();

        body.addFixture(Geometry.createSquare(getSize().getWidth()));
    }

    //    @Override
//    public void renderEntity(Camera camera) {
////        rotate(5);
//        drawCube(getLocation().getX(), getLocation().getY(), 500, getSize().getWidth(), getSize().getHeight(), getSize().getHeight());
//    }
}
