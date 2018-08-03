package com.rb2750.lwjgl.animations;

import com.rb2750.lwjgl.entities.Entity;
import com.rb2750.lwjgl.util.Size;

public class SquatAnimation extends Animation {
    public SquatAnimation() {
        super(0.5f);
    }

    public SquatAnimation(int time) {
        super(time);
    }

    @Override
    public Keyframe[] getKeyFrames(Entity entity) {
        return new Keyframe[]{
                new Keyframe(null, null, new Size(100, 100), false),
                new Keyframe(null, null, new Size(175, 75), true),
                new Keyframe(null, null, new Size(100, 100), false)
        };
    }
}
