package com.rb2750.lwjgl.graphics;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;

public class SpotLight
{
    @Getter
    @Setter
    private PointLight pointLight;
    @Getter
    private Vector3f direction;
    @Getter
    @Setter
    private float cutoff;

    public SpotLight(PointLight pointLight, Vector3f direction, float cutoff)
    {
        this.pointLight = pointLight;
        this.direction = direction.normalize();
        this.cutoff = cutoff;
    }

    public void setDirection(Vector3f direction)
    {
        this.direction = direction.normalize();
    }
}
