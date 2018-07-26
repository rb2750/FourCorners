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
                new Keyframe(null, 0, new Size(100,100)),
                new Keyframe(null, 0, new Size(200,-75)), //TODO: No reason for this to be negative
                new Keyframe(null, 0, new Size(100,100))
        };
    }
}
