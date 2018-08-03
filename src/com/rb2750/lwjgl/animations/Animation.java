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
    private float timeOfCurrFrame;
    private float timeOfOldFrame = 0;
    private Vector3f remainingRotation;
    private long frameStartTime = 0;
    private long lastFrameStartTime = 0;
    private long animationStartTime = 0;
    private Long pauseTime = null;
    private long lastFrame = 0;

    Animation(double time) {
        this.currentFrame = 0;
        this.timePerFrame = (float) time / (float) getKeyFrames(null).length;
        this.paused = false;
        this.timeOfCurrFrame = 0;
    }

    public void doAnimation(Entity entity) {
        Keyframe[] frames = getKeyFrames(entity);

//        for (int i = 1; i <= frames.length - 1; i++) {
//            if (frames[i].size != null && frames[i].position == null) {
//                if (i == frames.length - 1) {
//                    frames[i].position = new Vector2f(0, 0);
//                    continue;
//                }
//                frames[i].position = new Vector2f((frames[i + 1].size.getWidth() - frames[i].size.getWidth()) / 2, 0);
//            }
//        }
//
//        for (Keyframe frame : frames) {
//            System.out.println(frame.getPosition() + ":" + frame.getSize());
//        }

        if (original == null) {
            frameStartTime = Util.getTime();
            animationStartTime = Util.getTime();

            for (Animation animation : entity.getAnimations()) {
                original = animation.getOriginal();
                break;
            }
            if (original == null) original = entity.clone();
        }

        if (paused && timeOfCurrFrame < 0.05) {
            if (frames[currentFrame].pauseFrame) {
                return;
            }
        }

        double dTime = (Util.getTime() - frameStartTime) / 1000f;

        timeOfCurrFrame = (Util.getTime() - frameStartTime) / 1000f;
        timeOfOldFrame = (Util.getTime() - lastFrameStartTime) / 1000f;

        lastFrameStartTime = Util.getTime();

        Keyframe currFrame = frames[currentFrame];
        Keyframe nextFrame = frames[currentFrame + 1];
        float lerpAmount = Math.min(1, timeOfCurrFrame / timePerFrame);

        if (currFrame.size != null && nextFrame.size != null) {
            Vector2f currsize = new Vector2f(currFrame.size.getWidth(), currFrame.size.getHeight());
            Vector2f nextsize = new Vector2f(nextFrame.size.getWidth(), nextFrame.size.getHeight());

            Vector2f newSize = new Vector2f(currsize.lerp(nextsize, lerpAmount));

            Vector2f oldSize = new Vector2f(entity.getSize().getWidth(), entity.getSize().getHeight());

            if (entity.setSize(new Size(newSize.x, newSize.y))) {
                Vector2f dSize = new Vector2f(newSize).sub(oldSize);
                if (!entity.move(new Location(entity.getLocation().asVector().sub(dSize.mul(0.5f))).setWorld(entity.getWorld())))
                    return;
            } else return;
        }
        if (currFrame.position != null && nextFrame.position != null) {
            if (!entity.move(new Location(original.getLocation().asVector().add(currFrame.position.lerp(nextFrame.position, lerpAmount))).setWorld(original.getWorld())))
                return;
        }

        if (currFrame.rotation != null && nextFrame.rotation != null) {
            entity.setRotation(new Vector3f(currFrame.rotation).lerp(nextFrame.rotation, lerpAmount).add(original.getRotation()));
        }

        if (timeOfCurrFrame >= timePerFrame) {
            frameStartTime = Util.getTime();
            currentFrame++;
        }

        if (currentFrame >= frames.length - 1) {
            this.onFinish(entity);
            entity.removeAnimation(this);
        }
    }

    public void onFinish(Entity entity) {
        if (entity.getAnimations().size() > 1) return;

        //entity.move(original.getLocation());
        entity.setRotation(original.getRotation());
        entity.setSize(original.getSize());
    }

    public void Pause() {
        paused = true;
    }

    public void Unpause() {
        paused = false;
    }

    public abstract Keyframe[] getKeyFrames(Entity entity);
}

