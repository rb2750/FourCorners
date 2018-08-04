package com.rb2750.lwjgl.graphics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;

@AllArgsConstructor
public class Light
{
    @Getter
    @Setter
    private Vector3f colour;
    @Getter
    @Setter
    private float intensity;
}
