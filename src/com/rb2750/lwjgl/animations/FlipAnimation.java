package com.rb2750.lwjgl.animations;

import com.rb2750.lwjgl.entities.Entity;
import org.joml.Vector3f;

public class FlipAnimation extends Animation {
    public FlipAnimation() {
        super(0.5f);
    }

    @Override
    public Keyframe[] getKeyFrames(Entity entity) {
        int facing = entity == null ? 1 : entity.getFacing();

        return new Keyframe[]{
                new Keyframe(null, new Vector3f(0, 0, 90 * facing), null, false),
                new Keyframe(null, new Vector3f(0, 0, 180 * facing), null, false),
                new Keyframe(null, new Vector3f(0, 0, 270 * facing), null, false),
                new Keyframe(null, new Vector3f(0, 0, 360 * facing), null, false)
        };
    }
}
