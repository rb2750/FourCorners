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
    private float totalTime;
    private long lastFrameTime;

    long start;

    Animation(double time) {
        this.timePerFrame = (float) time / (float) getKeyFrames(null).length;
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
            for (Animation animation : entity.getAnimations()) {
                original = animation.getOriginal();
                break;
            }
            if (original == null) original = entity.clone();

            lastFrameTime = Util.getTime();

            start = Util.getTime();
        }

        float lastTime = timeOfCurrFrame;

        double dTime = (Util.getTime() - lastFrameTime) / 1000f;
        totalTime += dTime;

        if (paused) {
            if (frames[currentFrame].pauseFrame) {
                timeOfCurrFrame = 0;
                totalTime -= dTime;
            }
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

        lastFrameTime = Util.getTime();

        Keyframe currFrame = frames[currentFrame];
        Keyframe nextFrame = frames[currentFrame + 1];

        if (currFrame.size != null && nextFrame.size != null) {
            Vector2f currsize = new Vector2f(currFrame.size.getWidth(), currFrame.size.getHeight());
            Vector2f nextsize = new Vector2f(nextFrame.size.getWidth(), nextFrame.size.getHeight());

            Vector2f newSize = new Vector2f(currsize).lerp(new Vector2f(nextsize), lerpAmount);

            Vector2f oldSize = new Vector2f(entity.getSize().getWidth(), entity.getSize().getHeight());

            if (entity.setSize(new Size(newSize.x, newSize.y))) {
                Vector2f dSize = new Vector2f(newSize).sub(oldSize);
                if (!entity.move(new Location(entity.getLocation().asVector().sub(dSize.mul(0.5f).x, 0)).setWorld(entity.getWorld())))
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

//        if (totalTime >= timePerFrame * (currentFrame + 1) - dTime) {
////            System.out.println(Util.getTime() - start);
////            System.out.println("tpf: " + timePerFrame);
//            currentFrame++;
//            start = Util.getTime();
//        }
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

