package com.rb2750.lwjgl.gui;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.joml.Vector2f;

import static org.lwjgl.opengl.GL11.*;

@AllArgsConstructor
public class GUITexture
{
    @Getter
    private int texture;
    @Getter
    private Vector2f position;
    @Getter
    private Vector2f scale;

    public void cleanUp()
    {
        glDeleteTextures(texture);
    }
}
