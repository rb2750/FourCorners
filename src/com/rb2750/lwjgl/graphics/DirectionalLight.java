package com.rb2750.lwjgl.graphics;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;

public class DirectionalLight
{
    @Getter
    @Setter
    private Light base;
    @Getter
    private Vector3f direction;

    public DirectionalLight(Light base, Vector3f direction)
    {
        this.base = base;
        this.direction = direction.normalize();
    }

    public void setDirection(Vector3f direction)
    {
        this.direction = direction.normalize();
    }
}
