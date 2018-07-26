package com.rb2750.lwjgl.animations;

import com.rb2750.lwjgl.util.Location;
import com.rb2750.lwjgl.util.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Keyframe {
    @Getter
    Location position;

    @Getter
    float rotation;

    @Getter
    Size size;
}
