package com.rb2750.lwjgl.animations;

import com.rb2750.lwjgl.entities.Entity;
import com.rb2750.lwjgl.util.Size;

public class SquashAnimation extends Animation {
    public SquashAnimation() {
        super(0.75);
    }
    public SquashAnimation(int time) {
        super(time);
    }

    @Override
    public Keyframe[] getKeyFrames(Entity entity) {
        return new Keyframe[] {
                new Keyframe(null, null, new Size(100.0f,100.0f), false),
                new Keyframe(null, null, new Size(200.0f,050.0f), false),
                new Keyframe(null, null, new Size(070.0f,120.0f), false),
                new Keyframe(null, null, new Size(100.0f,100.0f), false)
        };
    }
}
