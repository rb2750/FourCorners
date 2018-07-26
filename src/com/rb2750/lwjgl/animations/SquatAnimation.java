package com.rb2750.lwjgl.animations;

import com.rb2750.lwjgl.entities.Entity;
import com.rb2750.lwjgl.util.AnimationFlag;
import com.rb2750.lwjgl.util.Size;
import com.rb2750.lwjgl.util.Util;

import java.awt.geom.Rectangle2D;

public class SquatAnimation extends Animation {
    public SquatAnimation() {
        super(1);
    }
    public SquatAnimation(int time) {
        super(time);
    }

    @Override
    public Keyframe[] getKeyFrames() {
        return new Keyframe[] {
                new Keyframe(null, 0, new Size(1.0f,1.0f)),
                new Keyframe(null, 0, new Size(1.5f,0.5f)),
                new Keyframe(null, 0, new Size(1.0f,1.0f))
        };
    }
}
