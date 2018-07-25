package com.rb2750.lwjgl.animations;

import com.rb2750.lwjgl.entities.Entity;
import lombok.Getter;
import lombok.Setter;

public abstract class Animation {
    @Getter
    @Setter
    private int remainingTime;

    public Animation() {
        this.remainingTime = getFrames();
    }

    public abstract int getFrames();

    public abstract boolean doAnimation(Entity entity);

    public abstract void onComplete(Entity entity);

    public abstract void onFinish(Entity entity);

    public abstract int getFlags();
}

