package com.rb2750.lwjgl.entities;

import com.rb2750.lwjgl.animations.Animation;
import com.rb2750.lwjgl.animations.SquatAnimation;
import com.rb2750.lwjgl.util.*;
import com.rb2750.lwjgl.world.World;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public abstract class Entity {
    @Getter
    private Location location;
    @Getter
    private Vector2 acceleration = new Vector2(0, 0);
    @Getter
    @Setter
    private float rotation = 0;
    @Getter
    @Setter
    private boolean gravity = false;
    @Setter
    private Entity interactingWith = null;
    @Getter
    @Setter
    private boolean canBeInteractedWith = true;
    @Getter
    @Setter
    private Size size;
    @Getter
    @Setter
    private Direction facing = Direction.RIGHT;
    private java.util.List<Animation> animations = new ArrayList<>();
    private List<Animation> pendingAnimations = new ArrayList<>();
    @Getter
    @Setter
    private boolean squat = false;

    public Entity(Location location, Size size) {
        this.location = location;
        this.size = size;
    }

    public void teleport(Location location) {
        this.location = location;
    }

    public boolean onGround() {
        return getLocation().getY() <= 0 || (interactingWith != null && interactingWith.getLocation().getY() < getLocation().getY());
    }

    public Rectangle getRectangle() {
        return new Rectangle((int) location.getX(), (int) location.getY(), (int) getSize().getWidth(), (int) getSize().getHeight());
    }

    public Location getCenter() {
        return new Location(location.getWorld(), (int) getRectangle().getCenterX(), (int) getRectangle().getCenterY());
    }

    public void rotate(float angle) {
        rotation += angle;
        rotation %= 360;
    }

    public void update() {
        if (getAcceleration().getX() < 0) setFacing(Direction.LEFT);
        if (getAcceleration().getX() > 0) setFacing(Direction.RIGHT);

        glMatrixMode(GL_MODELVIEW);
        glPushMatrix();
        float translateX = getLocation().getX() + (getSize().getWidth() / 2);
        float translateY = getLocation().getY() + (getSize().getHeight() / 2);
        glTranslatef(translateX, translateY, 0);
        glRotatef(rotation, 0, 0, 1);
        glTranslatef(-translateX, -translateY, 0);
        render();
        glPopMatrix();
    }

    public World getWorld() {
        return location.getWorld();
    }

    public void render() {
        handleAnimations();

        renderEntity();
    }

    public abstract void renderEntity();

    public boolean animationExists(Class<? extends Animation> clazz) {
        for (Animation a : new ArrayList<>(animations)) {
            if (a.getClass().equals(clazz)) {
                return true;
            }
        }
        return false;
    }

    public void addAnimation(Animation animation) {
        //Stop any issues with changing size while squatting
        if (squat && !(animation instanceof SquatAnimation) && (animation.getFlags() & AnimationFlag.SIZE) > 0) return;

        for (Animation a : new ArrayList<>(animations)) {
            if (a.getClass().equals(animation.getClass())) {
//                pendingAnimations.add(animation);
                return;
            }
        }

        //Find conflicts (stop glitchy rendering)
        for (Animation a : animations) if ((a.getFlags() & animation.getFlags()) > 0) return;

        animations.add(animation);
    }

    public void removeAnimation(Animation animation) {
        animations.remove(animation);
    }

    public void removeAnimation(Class<? extends Animation> animation) {
        for (Animation a : new ArrayList<>(animations)) {
            if (a.getClass().equals(animation)) {
                animations.remove(a);
            }
        }
    }

    private void handleAnimations() {
        pendingLoop:
        for (Animation pending : new ArrayList<>(pendingAnimations)) {
            for (Animation a : new ArrayList<>(animations)) {
                if (a.getClass().equals(pending.getClass())) {
                    continue pendingLoop;
                }
            }
//            animations.add(pending);
            pendingAnimations.remove(pending);
            addAnimation(pending);
        }
        for (Animation animation : new ArrayList<>(animations)) {
            if (animation.getRemainingTime() <= 0) {
                animation.onComplete(this);
                animations.remove(animation);
            } else if (animation.doAnimation(this)) animation.setRemainingTime(animation.getRemainingTime() - 1);
        }
    }
}
