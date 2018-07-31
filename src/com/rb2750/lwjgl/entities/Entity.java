package com.rb2750.lwjgl.entities;

import com.rb2750.lwjgl.Main;
import com.rb2750.lwjgl.animations.Animation;
import com.rb2750.lwjgl.graphics.*;
import com.rb2750.lwjgl.maths.MatrixUtil;
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

public abstract class Entity implements Cloneable {
    // Set in child objects of the entity
    String texturePath;

    @Getter
    private Location location;
    @Getter
    private Vector2 acceleration = new Vector2(0, 0);
    @Getter
    @Setter
    private Vector3f rotation = new Vector3f(0, 0, 0);
    @Getter
    @Setter
    private boolean gravity = false;
    @Setter
    private Entity interactingWithX = null;
    @Setter
    private Entity interactingWithY = null;
    @Getter
    @Setter
    private boolean canBeInteractedWith = true;
    @Getter
    private Size size;

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

    private VertexArray mesh;
    @Getter
    protected Texture texture;
    protected Shader shader;

    float[] vertices;
    int[] indices;
    float[] tcs;
    float[] normals;

    float layer = 0.0f;

    /**
     *
     * @param location Where on the screen
     * @param size How big should the entity be on the screen
     * @param shader What shader should be used to render the entity
     */
    Entity(Location location, Size size, Shader shader) {
        this.location = location;
        this.size = size;
        this.shader = shader;

        this.originalSize = size;
        this.originalRotation = new Vector3f(0);
    }

    void createMesh() {
        Main.instance.runOnUIThread(() -> {
            mesh = new VertexArray(vertices, indices, tcs, normals);
            texture = new Texture(texturePath);
            texture.setReflectivity(0.5f);
        });
    }

    /**
     * Create new mesh from an obj file
     * @param filePath Path to the obj tile
     */
    public void createMesh(String filePath) {
        Main.instance.runOnUIThread(() -> {
            mesh = OBJLoader.loadOBJ(filePath);
            texture = new Texture(texturePath);
            texture.setReflectivity(0.5f);
        });
    }

    /**
     * Determine if the ability is able to move to a specific location
     * @param location The location to move to
     * @return Is the entity able to move
     */
    private boolean canMove(Location location) {
        if(location == null) return false;
        return location.getWorld().intersects(this, Util.getRectangle(location, getSize())) == null && location.getY() >= 0 && location.getX() >= 0;
    }

    private boolean move(Location location, boolean force) {
        if (!force && !canMove(location)) return false;
        this.location = location;
        return true;
    }


    /**
     * Move the entity to a specific location while ignoring any objects that are in the way
     * @param location Location to move to
     * @return Was the entity able to move
     */
    public boolean teleport(Location location) {
        return move(location, true);
    }

    /**
     * Move the entity to a specific location if its possible
     * @param location Location to move to
     * @return Was the entity able to move
     */
    public boolean move(Location location) {
        return move(location, false);
    }

    /**
     * Set the size of the entity. If the new size would cause the entity to collide with another entity it will scale in the opposite direction.
     * @param size The new size of the entity
     * @return Was the size of the entity changed
     */
    public boolean setSize(Size size) {
        Entity intersectsWith = location.getWorld().intersects(this, Util.getRectangle(getLocation(), size));

        if (intersectsWith != null) {
            boolean left = intersectsWith.getLocation().getX() + intersectsWith.getSize().getWidth() <= getLocation().getX();
            boolean right = intersectsWith.getLocation().getX() > getLocation().getX();
            boolean top = intersectsWith.getLocation().getY() >= getLocation().getY();
//            boolean bottom = intersectsWith.getLocation().getY() + intersectsWith.getSize().getHeight() > getLocation().getY();

            if (top && !left && !right) return false;
//            if (bottom)
//                if (!move(new Location(getWorld(), getLocation().getX(), getLocation().getY() + Math.abs(getSize().getHeight() - size.getHeight()))))
//                    return;
            Location moveTo = null;

            if (right)
                moveTo = new Location(getWorld(), getLocation().getX() - Math.abs(getSize().getWidth() - size.getWidth()), getLocation().getY());
            if (left)
                moveTo = new Location(getWorld(), getLocation().getX() + Math.abs(getSize().getWidth() - size.getWidth()), getLocation().getY());

            if (moveTo != null) {
                if (location.getWorld().intersects(this, Util.getRectangle(moveTo, size)) == null)
                    this.location = moveTo;
                else return false;
            }
        }

        this.size = size;
        return true;
    }

    /**
     * Check whether the entity is on the ground
     * @return Is the entity on the ground
     */
    public boolean onGround() {
        return getLocation().getY() <= 0 || (interactingWithY != null && interactingWithY.getLocation().getY() + interactingWithY.getSize().getHeight() <= getLocation().getY());
    }

    /**
     * Get Rectangle of the entity
     * @return the entity Rectangle2D
     */
    public Rectangle2D getRectangle() {
        return Util.getRectangle(getLocation(), getSize());
    }

    /**
     * Get the midpoint of the entity
     * @return A location for the center of the entity
     */
    public Location getCenter() {
        return new Location(location.getWorld(), getRectangle().getCenterX(), getRectangle().getCenterY());
    }

    /**
     * Rotate the entity relative to its current rotation
     * @param rotation How much to rotate the entity
     */
    public void rotate(Vector3f rotation) {
        this.setRotation(new Vector3f(this.rotation).add(rotation));
    }

    /**
     * Change the entities rotation to whatever is passed in as a parameter
     * @param rotation The new rotation of the entity
     */
    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
        this.rotation.x %= 360;
        this.rotation.y %= 360;
        this.rotation.z %= 360;
    }

    /**
     * The update function of the entity. Should be called every frame
     */
    public void update() {
        if (getAcceleration().getX() < 0) setFacing(Direction.LEFT);
        if (getAcceleration().getX() > 0) setFacing(Direction.RIGHT);
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
     * @param other The entity that is interacting with it
     */
    public void onInteract(Entity other) {
        //Empty stub
    }

    /**
     * Get the world the entity exists within
     * @return The world
     */
    public World getWorld() {
        return location.getWorld();
    }

    /**
     * Render the object using the camera
     * @param camera The camera to render the object to
     * @param clipPlane The clipping plane, meaning if the object is to far from the camera it won't be rendered
     */
    public void render(Camera camera, Vector4f clipPlane) {
        if (mesh == null || shader == null || camera == null || texture == null)
            return;

//        System.out.println(rotation);
        shader.enable();
        shader.setUniformMat4f("ml_matrix", MatrixUtil.transformation(new Vector3f((float) location.getX(), (float) location.getY(), layer), rotation.x, rotation.y, rotation.z, new Vector3f((float) size.getWidth(), (float) size.getHeight(), (float) size.getWidth())));
//        shader.setUniformMat4f("ml_matrix", MatrixUtil.transformation(new Vector3f((float) location.getX(), (float) location.getY(), layer), rotation.x, rotation.y, rotation.z, new Vector3f((float) size.getWidth(), (float) size.getHeight(), (float) size.getWidth())));
        shader.setUniformMat4f("vw_matrix", MatrixUtil.view(camera));

        if (shader != Shader.BASIC)
        {
            shader.setUniform1f("shineDamper", texture.getShineDamper());
            shader.setUniform1f("reflectivity", texture.getReflectivity());
        }

        shader.setUniform4f("clipPlane", clipPlane);
        texture.bind();
        mesh.render();
        shader.disable();
    }

    /**
     * A check to verify if an animation already exists
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
     * @param animation The animation to add
     */
    public void addAnimation(Animation animation) {
        if(this.animationExists(animation.getClass())) return;

        animations.add(animation);
    }

    /**
     * Remove a given animation given that it does exist
     * @param animation The animation to remove
     */
    public void removeAnimation(Animation animation) {
        if(!this.animationExists(animation.getClass())) return;

        animations.remove(animation);

        if(animations.size() == 0) {
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
     * Prepare for safe deletion of an object
     */
    public void cleanUp() {
        mesh.cleanUp();
        texture.cleanUp();
    }

    /**
     * Create a copy of the current object
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
