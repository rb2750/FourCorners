package com.rb2750.lwjgl.animations;

import com.rb2750.lwjgl.Main;
import com.rb2750.lwjgl.entities.Entity;
import com.rb2750.lwjgl.util.Location;
import com.rb2750.lwjgl.util.Size;
import lombok.Getter;
import lombok.Setter;

public abstract class Animation {
    @Getter
    @Setter
    private double remainingTime;
    private double timePerFrame;
    private int currentFrame;
    private Entity original;
    private boolean paused;
    private double timeOfCurrFrame;


    Animation(double time) {
        this.remainingTime = time;
        this.currentFrame = 0;
        this.timePerFrame = (float)time / (float)getKeyFrames().length;
        this.paused = false;
        this.timeOfCurrFrame = 0;


        System.out.println("Animation info");
        System.out.println("Current frame time:" + timePerFrame);
        System.out.println("Total time:" + time);
        System.out.println("End animation info");
    }

    public void doAnimation(Entity entity) {
        System.out.println("Frame: " + currentFrame);
        System.out.println("Frame info");
        System.out.println("Current frame time:" + timeOfCurrFrame);
        System.out.println("End frame info");


        if(original == null) original = entity.clone();
        if(paused) return;

        // Rob: you don't need the equals
        if(currentFrame >= getKeyFrames().length-1) {
            this.onFinish(entity);
            entity.removeAnimation(this);
            return;
        }

        Keyframe currFrame = getKeyFrames()[currentFrame];
        Keyframe nextFrame = getKeyFrames()[currentFrame+1];
        double dTime = (double)Main.getDeltaTime()/1000;
        System.out.println("Delta time: " + dTime);

        if(currFrame.position != null && nextFrame.position != null){
            Location dLoc = entity.getLocation().clone().add(nextFrame.position.clone().subtract(currFrame.position).multiply(dTime));
            entity.move(dLoc);
        }
        if(currFrame.rotation != 0 && nextFrame.rotation != 0){
            double dRot = entity.getRotation()+(nextFrame.rotation - currFrame.rotation) * dTime;
            entity.rotate(dRot);
        }
        if(currFrame.size != null && nextFrame.size != null){
            Size dSize = nextFrame.size.clone().subtract(currFrame.size).multiply(dTime);
            entity.setSize(entity.getSize().clone().add(dSize));
            Size halfSize = dSize.clone().multiply(0.5);
            
            entity.move(entity.getLocation().add(-halfSize.getWidth(),0));
        }

        timeOfCurrFrame += dTime;
        if(timeOfCurrFrame > timePerFrame) {currentFrame++; timeOfCurrFrame = 0;}
        remainingTime--;
    }

    public void onFinish(Entity entity) {
        System.out.println("Finish animation");
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

