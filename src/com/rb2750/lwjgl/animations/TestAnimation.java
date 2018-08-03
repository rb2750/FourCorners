package com.rb2750.lwjgl.animations;

import com.rb2750.lwjgl.entities.Entity;
import com.rb2750.lwjgl.util.Size;

public class TestAnimation extends Animation {
    public TestAnimation() {
        super(3f);
    }

    @Override
    public Keyframe[] getKeyFrames(Entity entity) {
        return new Keyframe[]{
                new Keyframe(null, null, new Size(100, 100), false),
                new Keyframe(null, null, new Size(200, 100), false),
                new Keyframe(null, null, new Size(100, 100), false),
        };
    }
}
