package com.rb2750.lwjgl.graphics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;

@AllArgsConstructor
public class PointLight
{
    @Getter
    @Setter
    private Light base;
    @Getter
    @Setter
    private Attenution atten;
    @Getter
    @Setter
    private Vector3f position;
    @Getter
    @Setter
    private float range;
}
