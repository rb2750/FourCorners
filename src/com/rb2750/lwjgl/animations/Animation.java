package com.rb2750.lwjgl.animations;

import com.rb2750.lwjgl.Main;
import com.rb2750.lwjgl.entities.Entity;
import com.rb2750.lwjgl.util.Location;
import com.rb2750.lwjgl.util.Size;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;

public abstract class Animation {
    @Getter
    @Setter
    private double remainingTime;
    private double timePerFrame;
    private int currentFrame;
    @Getter
    private Entity original;
    private boolean paused;
    private double timeOfCurrFrame;

    Animation(double time) {
        this.remainingTime = time;
        this.currentFrame = 0;
        this.timePerFrame = (float) time / (float) getKeyFrames().length;
        this.paused = false;
        this.timeOfCurrFrame = 0;
    }

    public void doAnimation(Entity entity) {
        if (original == null) {
            for (Animation animation : entity.getAnimations()) {
                original = animation.getOriginal();
                break;
            }
            if (original == null) original = entity.clone();
        }

        if (paused && timeOfCurrFrame < 0.05) {
            if (getKeyFrames()[currentFrame].pauseFrame) {
                return;
            }
        }

        if (currentFrame >= getKeyFrames().length - 1) {
            this.onFinish(entity);
            entity.removeAnimation(this);
            return;
        }

        Keyframe currFrame = getKeyFrames()[currentFrame];
        Keyframe nextFrame = getKeyFrames()[currentFrame + 1];
        double dTime = (double) Main.getDeltaTime();

        if (currFrame.position != null && nextFrame.position != null) {
            Location dLoc = entity.getLocation().clone().add(nextFrame.position.clone().subtract(currFrame.position).multiply(dTime));
            if (!entity.move(dLoc)) return;
        }
        if (currFrame.size != null && nextFrame.size != null) {
            Size dSize = nextFrame.size.clone().subtract(currFrame.size).multiply(dTime);
            if (entity.setSize(entity.getSize().clone().add(dSize))) {
                Size halfSize = dSize.clone().multiply(0.5);

                entity.move(entity.getLocation().clone().add(-halfSize.getWidth(), 0));
            }
            else return;
        }
        if (currFrame.rotation != null && nextFrame.rotation != null) {
            Vector3f dRot = (entity.getRotation().add((nextFrame.rotation.sub(currFrame.rotation)).mul((float)dTime)).mul((float)entity.getFacing()));
            entity.rotate(dRot);
        }

        timeOfCurrFrame += dTime;
        if (timeOfCurrFrame > timePerFrame) {
            currentFrame++;
            timeOfCurrFrame = 0;
        }
        remainingTime--;
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

    public abstract Keyframe[] getKeyFrames();
}

