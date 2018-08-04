package com.rb2750.lwjgl.entities;

import com.rb2750.lwjgl.graphics.DisplayObject;
import com.rb2750.lwjgl.graphics.Shader;
import com.rb2750.lwjgl.util.Location;
import com.rb2750.lwjgl.util.Size;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class CircularSector extends DisplayObject {
    public CircularSector(Location location, Vector4f color) {
        super(location, new Size(100, 100), Shader.BASIC_COLOUR, color);
        texturePath = "res/textures/red.png";

        createMesh("res/models/circular_sector.obj");
    }
}
