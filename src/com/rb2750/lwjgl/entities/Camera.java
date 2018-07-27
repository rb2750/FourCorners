package com.rb2750.lwjgl.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joml.Vector3f;

@NoArgsConstructor
public class Camera
{
    @Getter
    @Setter
    private Vector3f position = new Vector3f();
    @Getter
    private float pitch;
    @Getter
    private float yaw;
    @Getter
    private float roll;
}
