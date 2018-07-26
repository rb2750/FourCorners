package com.rb2750.lwjgl.animations;

import com.rb2750.lwjgl.entities.Entity;
import com.rb2750.lwjgl.util.AnimationFlag;
import com.rb2750.lwjgl.util.Direction;

public class FlipAnimation extends Animation {
    @Override
    public int getFrames() {
        return 25;
    }

    @Override
    public boolean doAnimation(Entity entity) {
        if (entity.getFacing() == Direction.LEFT) entity.rotate(360f / (double) getFrames());
        else if (entity.getFacing() == Direction.RIGHT) entity.rotate(-360f / (double) getFrames());
        return true;
    }

    @Override
    public void onComplete(Entity entity) {
        entity.setRotation(0);
    }

    @Override
    public void onFinish(Entity entity) {
        entity.setRotation(0);
    }

    @Override
    public int getFlags() {
        return AnimationFlag.ROTATION;
    }
}
