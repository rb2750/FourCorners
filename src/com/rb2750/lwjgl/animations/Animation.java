package com.rb2750.lwjgl.animations;

import com.rb2750.lwjgl.entities.Entity;
import com.rb2750.lwjgl.util.*;
import lombok.Getter;
import org.joml.Vector2f;
import org.joml.Vector3f;

public abstract class Animation {
    private float timePerFrame;
    private int currentFrame;
    @Getter
    private Entity original;
    private boolean paused;
    private float totalTime;
    private long lastFrameTime;

    Animation(double time) {
        this.timePerFrame = (float) time / (float) getKeyFrames(null).length;
    }

    public void doAnimation(Entity entity) {
        Keyframe[] frames = getKeyFrames(entity);

        if (original == null) {
            if (!entity.getAnimations().isEmpty()) original = entity.getAnimations().get(0).getOriginal();
            if (original == null) original = entity.clone();

            lastFrameTime = Util.getTime();
        }

        double dTime = (Util.getTime() - lastFrameTime) / 1000f;

        lastFrameTime = Util.getTime();

        float timeOfCurrFrame;
        if (paused && frames[currentFrame].pauseFrame) {
//            totalTime -= dTime;
            return;
        }

        currentFrame = (int) (totalTime / timePerFrame);

        if (currentFrame >= frames.length - 1) {
            this.onFinish(entity);
            entity.removeAnimation(this);
            return;
        }

        timeOfCurrFrame = totalTime % timePerFrame;
        if (totalTime > timePerFrame * (currentFrame + 1)) timeOfCurrFrame = 0.01f;

        float lerpAmount = Math.min(1, timeOfCurrFrame / timePerFrame);

        Keyframe currFrame = frames[currentFrame];
        Keyframe nextFrame = frames[currentFrame + 1];

        if (currFrame.size != null && nextFrame.size != null) {
            Vector2f currsize = new Vector2f(currFrame.size.width, currFrame.size.height);
            Vector2f nextsize = new Vector2f(nextFrame.size.width, nextFrame.size.height);

            Vector2f newSize = new Vector2f(currsize).lerp(new Vector2f(nextsize), lerpAmount);

            Vector2f oldSize = new Vector2f(entity.size.width, entity.size.height);

            if (entity.setSize(new Size(newSize.x, newSize.y))) {
                if (!entity.move(new Location(entity.getLocation().asVector().sub(new Vector2f(newSize).sub(oldSize).mul(0.5f).x, 0)).setWorld(entity.getWorld())))
                    return;
            } else return;
        }

        totalTime += dTime;

        if (currFrame.position != null && nextFrame.position != null) {
            if (!entity.move(new Location(original.getLocation().asVector().add(currFrame.position.lerp(nextFrame.position, lerpAmount))).setWorld(original.getWorld())))
                return;
        }

        if (currFrame.rotation != null && nextFrame.rotation != null) {
            entity.setRotation(new Vector3f(currFrame.rotation).lerp(nextFrame.rotation, lerpAmount).add(original.getRotation()));
        }
    }

    public void onFinish(Entity entity) {
        if (entity.getAnimations().size() > 1) return;

        //entity.move(original.getLocation());
        entity.setRotation(original.getRotation());
        entity.setSize(original.size);
    }

    public void Pause() {
        paused = true;
    }

    public void Unpause() {
        paused = false;
    }

    public abstract Keyframe[] getKeyFrames(Entity entity);
}

