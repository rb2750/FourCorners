package com.rb2750.lwjgl.animations;

import com.rb2750.lwjgl.entities.Entity;
import com.rb2750.lwjgl.util.AnimationFlag;
import com.rb2750.lwjgl.util.Direction;
import com.rb2750.lwjgl.util.Size;

public class SquashAnimation extends Animation {
    public SquashAnimation() {
        super(1000);
    }

    @Override
    public Keyframe[] getKeyFrames() {
        return new Keyframe[] {
                new Keyframe(null, 0, new Size(100.0f,100.0f)),
                new Keyframe(null, 0, new Size(200.0f,050.0f)),
                new Keyframe(null, 0, new Size(070.0f,120.0f)),
                new Keyframe(null, 0, new Size(100.0f,100.0f))
        };
    }
}
