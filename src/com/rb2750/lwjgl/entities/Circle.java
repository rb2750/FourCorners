package com.rb2750.lwjgl.entities;

import com.rb2750.lwjgl.graphics.DisplayObject;
import com.rb2750.lwjgl.graphics.Shader;
import com.rb2750.lwjgl.util.Location;
import com.rb2750.lwjgl.util.Size;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Circle extends DisplayObject {
    public Circle(Location location) {
        super(location, new Size(100, 100), Shader.BASIC_COLOUR, new Vector4f(0.0f, 0.0f, 1.0f, 1.0f));

        this.texturePath = "res/textures/red.png";
        createMesh("res/models/circle.obj");
    }
}
