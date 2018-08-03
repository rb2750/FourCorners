package com.rb2750.lwjgl.animations;

import com.rb2750.lwjgl.util.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.joml.Vector2f;
import org.joml.Vector3f;

@AllArgsConstructor
public class Keyframe {
    @Getter
    Vector2f position;

    @Getter
    Vector3f rotation;

    @Getter
    Size size;

    Boolean pauseFrame = false;
}
