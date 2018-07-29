package com.rb2750.lwjgl.animations;

import com.rb2750.lwjgl.util.Location;
import com.rb2750.lwjgl.util.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.joml.Vector3f;

@AllArgsConstructor
public class Keyframe {
    @Getter
    Location position;

    @Getter
    Vector3f rotation;

    @Getter
    Size size;

    Boolean pauseFrame = false;
}
