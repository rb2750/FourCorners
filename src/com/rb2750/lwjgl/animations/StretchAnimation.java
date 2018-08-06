package com.rb2750.lwjgl.animations;

import com.rb2750.lwjgl.entities.Entity;
import com.rb2750.lwjgl.util.Size;

public class StretchAnimation extends Animation {
    public StretchAnimation() {
        super(0.6);
    }

    public StretchAnimation(int time) {
        super(time);
    }

    @Override
    public Keyframe[] getKeyFrames(Entity entity) {
        return new Keyframe[]{
                new Keyframe(null, null, new Size(100f, 100), false),
                new Keyframe(null, null, new Size(085.0f, 130.0f), false),
                new Keyframe(null, null, new Size(100.0f, 100.0f), false)
        };
    }
}
