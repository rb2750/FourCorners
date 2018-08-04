package com.rb2750.lwjgl.entities;

import com.rb2750.lwjgl.animations.Animation;
import com.rb2750.lwjgl.graphics.DisplayObject;
import com.rb2750.lwjgl.graphics.Shader;
import com.rb2750.lwjgl.maths.Vector2;
import com.rb2750.lwjgl.util.*;
import com.rb2750.lwjgl.world.World;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public abstract class Entity extends DisplayObject implements Cloneable {
    @Getter
    private Vector2 acceleration = new Vector2(0, 0);
    @Getter
    @Setter
    private boolean gravity = false;
    @Setter
    private Entity interactingWithX = null;
    @Setter
    private Entity interactingWithY = null;
    @Getter
    @Setter
    private int facing = Direction.RIGHT;
    @Getter
    private java.util.List<Animation> animations = new ArrayList<>();
    private List<Animation> pendingAnimations = new ArrayList<>();
    @Getter
    @Setter
    private boolean squat = false;
    private Size originalSize;
    private Vector3f originalRotation;
    @Getter
    @Setter
    private boolean canInteract = true;

    Entity(Size size, Shader shader, Vector4f baseColour) {
        super(size, shader, baseColour);

//        body = OdeHelper.createBody(location.getWorld().getPhysicsWorld());
//        body.setPosition(location.getX(), location.getY(), layer);
//
//        if (hasPhysics) {
//            geom = OdeHelper.createBox(location.getWorld().getSpace(), size.getWidth(), size.getHeight(), size.getWidth());
//            geom.setBody(body);
//            geom.setData(this);
//            DMass mass = OdeHelper.createMass();
//            mass.setBox(1, size.getWidth(), size.getHeight(), size.getWidth());
//            body.setMass(mass);
//        }

        this.originalSize = size;
        this.originalRotation = new Vector3f(0);
    }

    /**
     * @param location Where on the screen
     * @param size     How big should the entity be on the screen
     * @param shader   What shader should be used to render the entity
     */
    Entity(Location location, Size size, Shader shader, Vector4f baseColour) {
        this(size, shader, baseColour);
        this.location = location;
    }

    /**
     * Determine if the ability is able to move to a specific location
     *
     * @param location The location to move to
     * @return Is the entity able to move
     */
    private boolean canMove(Location location) {
        if (location == null) return false;
        return location.getWorld().intersects(this, Util.getRectangle(location, getSize())) == null && location.getY() >= 0 && location.getX() >= 0;
    }

    /**
     * Move the entity to a specific location if its possible
     *
     * @param location Location to move to
     * @return Was the entity able to move
     */
    public boolean move(Location location) {
        return move(location, false);
    }

    private boolean move(Location location, boolean force) {
        if (!force && !canMove(location)) return false;
        this.location = location;
        //body.setPosition(location.getX(), location.getY(), layer);
        return true;
    }

    public boolean setSize(Size size) {
        return setSize(size, false);
    }

    /**
     * Set the size of the entity. If the new size would cause the entity to collide with another entity it will scale in the opposite direction.
     *
     * @param size The new size of the entity
     * @return Was the size of the entity changed
     */
    public boolean setSize(Size size, boolean force) {
        if (!force) {
            Entity intersectsWith = location.getWorld().intersects(this, Util.getRectangle(getLocation(), size));

            if (intersectsWith != null) {
                boolean left = intersectsWith.getLocation().getX() + intersectsWith.getSize().getWidth() <= getLocation().getX();
                boolean right = intersectsWith.getLocation().getX() <= getLocation().getX() + size.getWidth();
                boolean top = intersectsWith.getLocation().getY() >= getLocation().getY();

                if (top && !left && !right)
                    return false;

                Location moveTo = null;

                if (left)
                    moveTo = new Location(getWorld(), intersectsWith.getLocation().getX() + intersectsWith.getSize().getWidth() + size.getWidth(), getLocation().getY());
                if (right)
                    moveTo = new Location(getWorld(), intersectsWith.getLocation().getX() - size.getWidth() - 1, getLocation().getY());

                if (moveTo != null) {
                    Entity intersects = location.getWorld().intersects(this, Util.getRectangle(moveTo, size));

                    if (intersects == null) this.location = moveTo;
                    else return false;
                }
            }
        }

        this.size = size;
        return true;
    }

    /**
     * Check whether the entity is on the ground
     *
     * @return Is the entity on the ground
     */
    public boolean onGround() {
        return getLocation().getY() <= 0 || (interactingWithY != null && interactingWithY.getLocation().getY() + interactingWithY.getSize().getHeight() <= getLocation().getY());
    }

    /**
     * Get Rectangle of the entity
     *
     * @return the entity Rectangle2D
     */
    public Rectangle2D getRectangle() {
        return Util.getRectangle(getLocation(), getSize());
    }

    /**
     * Get the midpoint of the entity
     *
     * @return A location for the center of the entity
     */
    public Location getCenter() {
        return new Location(location.getWorld(), (float) getRectangle().getCenterX(), (float) getRectangle().getCenterY());
    }

    /**
     * The update function of the entity. Should be called every frame
     */
    public void update() {
        if (getAcceleration().getX() < 0) setFacing(Direction.LEFT);
        if (getAcceleration().getX() > 0) setFacing(Direction.RIGHT);
//        body.setPosition(location.getX(), location.getY(), layer);
//        body.setRotation(Conversions.mat4fToOdeMat3f(MatrixUtil.rotate(rotation.x, rotation.y, -rotation.z)));
        handleAnimations();

        if (interactingWithX != null) {
            onInteract(interactingWithX);
            interactingWithX.onInteract(this);
        }
        if (interactingWithY != null) {
            onInteract(interactingWithY);
            interactingWithY.onInteract(this);
        }
    }

    /**
     * Called whenever another entity interacts with the current one
     *
     * @param other The entity that is interacting with it
     */
    public void onInteract(Entity other) {
        //Empty stub
    }

    /**
     * Get the world the entity exists within
     *
     * @return The world
     */
    public World getWorld() {
        return location.getWorld();
    }

    /**
     * A check to verify if an animation already exists
     *
     * @param clazz The Animation to check
     * @return Does the animation already exist on the entity
     */
    public boolean animationExists(Class<? extends Animation> clazz) {
        for (Animation a : new ArrayList<>(animations)) {
            if (a.getClass().equals(clazz)) {
                return true;
            }
        }
        return false;
    }

    /**
     * If an animation return that animation
     *
     * @param clazz The animation to return
     * @return The animation if it exists. Null if it doesn't
     */
    public Animation getAnimation(Class<? extends Animation> clazz) {
        for (Animation a : new ArrayList<>(animations)) {
            if (a.getClass().equals(clazz)) {
                return a;
            }
        }
        return null;
    }

    /**
     * Add an animation to the entity provided it isn't already added
     *
     * @param animation The animation to add
     */
    public void addAnimation(Animation animation) {
        if (this.animationExists(animation.getClass())) return;

        animations.add(animation);
    }

    /**
     * Remove a given animation given that it does exist
     *
     * @param animation The animation to remove
     */
    public void removeAnimation(Animation animation) {
        if (!this.animationExists(animation.getClass())) return;

        animations.remove(animation);

        if (animations.size() == 0) {
            this.setRotation(originalRotation);
            this.setSize(originalSize);
        }
    }

    /**
     * Update the running animations
     */
    private void handleAnimations() {
        pendingLoop:
        for (Animation pending : new ArrayList<>(pendingAnimations)) {
            for (Animation a : new ArrayList<>(animations)) {
                if (a.getClass().equals(pending.getClass())) {
                    continue pendingLoop;
                }
            }
            pendingAnimations.remove(pending);
            addAnimation(pending);
        }

        for (Animation animation : new ArrayList<>(animations)) {
            animation.doAnimation(this);
        }
    }

    /**
     * Create a copy of the current object
     *
     * @return A copy of the current object
     */
    public Entity clone() {
        try {
            return (Entity) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
