package com.rb2750.lwjgl.animations;

import com.rb2750.lwjgl.entities.Entity;
import com.rb2750.lwjgl.util.AnimationFlag;
import com.rb2750.lwjgl.util.Size;

public class SquashAnimation extends Animation {
//    private float amount;
//
//    public SquashAnimation(float amount) {
//        this.amount = amount * amount;
//    }

    @Override
    public int getFrames() {
        return 30;
    }

    private Size startSize = null;

    @Override
    public boolean doAnimation(Entity entity) {
        if (startSize == null) startSize = entity.getSize();
        float xMove = (25 / (getFrames() / 2));
        if (getRemainingTime() > getFrames() / 2) {
            entity.setSize(new Size(entity.getSize().getWidth() + xMove, entity.getSize().getHeight() - (25 / (getFrames() / 2))));
            entity.getLocation().subtract(xMove / 2f, 0);
        } else {
            entity.setSize(new Size(entity.getSize().getWidth() - xMove, entity.getSize().getHeight() + (25 / (getFrames() / 2))));
            entity.getLocation().add(xMove / 2f, 0);
        }
        return true;
    }

    @Override
    public void onFinish(Entity entity) {
        entity.setSize(startSize);
//        entity.setSize(new Size(100, 100));
    }

    @Override
    public void onComplete(Entity entity) {
        entity.setSize(new Size(100, 100));
    }

    @Override
    public int getFlags() {
        return AnimationFlag.SIZE | AnimationFlag.MOVEMENT;
    }
}
