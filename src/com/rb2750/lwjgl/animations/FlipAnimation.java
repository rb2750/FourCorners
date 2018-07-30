package com.rb2750.lwjgl.animations;

import org.joml.Vector3f;

public class FlipAnimation extends Animation {
    public FlipAnimation() {
        super(0.5f);
    }

    @Override
    public Keyframe[] getKeyFrames() {
        return new Keyframe[]{
                new Keyframe(null, new Vector3f(0, 0, 90), null, false),
                new Keyframe(null, new Vector3f(0, 0, 360), null, false)
        };
    }
}
