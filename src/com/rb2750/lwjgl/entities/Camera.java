package com.rb2750.lwjgl.entities;

import com.rb2750.lwjgl.maths.Vector3;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class Camera
{
    @Getter
    @Setter
    private Vector3 position = new Vector3();
    @Getter
    private float pitch;
    @Getter
    private float yaw;
    @Getter
    private float roll;
}
