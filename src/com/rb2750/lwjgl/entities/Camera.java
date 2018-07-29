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
    @Setter
    private float pitch;
    @Getter
    @Setter
    private float yaw;
    @Getter
    @Setter
    private float roll;

    public void invertPitch()
    {
        this.pitch = -pitch;
    }

    public void Move(Vector3f movementVector) {position.add(movementVector);}
}
