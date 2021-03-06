package com.rb2750.lwjgl.animations;

import com.rb2750.lwjgl.entities.Entity;
import com.rb2750.lwjgl.util.Size;
import org.joml.Vector3f;

public class TestAnimation extends Animation {
    public TestAnimation() {
        super(3f);
    }

    @Override
    public Keyframe[] getKeyFrames(Entity entity) {
        return new Keyframe[]{
                new Keyframe(null, null, new Size(100f, 100), false),
                new Keyframe(null, null, new Size(085.0f, 115.0f), false),
                new Keyframe(null, null, new Size(100.0f, 100.0f), false)
        };
    }
}
