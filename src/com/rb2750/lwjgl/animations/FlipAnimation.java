package com.rb2750.lwjgl.animations;

import com.rb2750.lwjgl.entities.Entity;
import com.rb2750.lwjgl.util.AnimationFlag;
import com.rb2750.lwjgl.util.Direction;

public class FlipAnimation extends Animation {
    public FlipAnimation() {
        super(1000);
    }

    @Override
    public Keyframe[] getKeyFrames() {
        return new Keyframe[] {
                new Keyframe(null, 90, null),
                new Keyframe(null, 180, null),
                new Keyframe(null, 270, null),
                new Keyframe(null, 360, null)
        };
    }
}
