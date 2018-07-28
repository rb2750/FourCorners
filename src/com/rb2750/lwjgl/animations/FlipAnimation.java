package com.rb2750.lwjgl.animations;

public class FlipAnimation extends Animation {
    public FlipAnimation() {
        super(0.15);
    }

    @Override
    public Keyframe[] getKeyFrames() {
        return new Keyframe[] {
                new Keyframe(null, 90 , null, false),
                new Keyframe(null, 180, null, false),
                new Keyframe(null, 270, null, false),
                new Keyframe(null, 360, null, false)
        };
    }
}
