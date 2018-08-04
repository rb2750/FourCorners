package com.rb2750.lwjgl.entities;

import com.rb2750.lwjgl.graphics.Shader;
import com.rb2750.lwjgl.util.Location;
import com.rb2750.lwjgl.util.Size;
import org.joml.Vector3f;

public class Circle extends Entity {
    public Circle(Location location) {
        super(location, new Size(100, 100), Shader.BASIC, new Vector3f(1.0f, 0.0f, 0.0f));

        this.texturePath = "res/textures/red.png";

        createMesh("res/models/circle.obj");

//        normals = calcNormals();
    }

    @Override
    public void update() {
        super.update();
//        rotate(new Vector3f(1f, 0, 0));
    }
}
