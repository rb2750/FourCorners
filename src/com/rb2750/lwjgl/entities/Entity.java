package com.rb2750.lwjgl.entities;

import com.rb2750.lwjgl.Main;
import com.rb2750.lwjgl.animations.Animation;
import com.rb2750.lwjgl.graphics.*;
import com.rb2750.lwjgl.maths.MatrixUtil;
import com.rb2750.lwjgl.maths.Vector2;
import com.rb2750.lwjgl.maths.Vector3;
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
    @Getter
    private Location location;
    String texturePath;
    @Getter
    private Vector2 acceleration = new Vector2(0, 0);
    @Getter
    @Setter
    private Vector3f rotation = new Vector3f(0,0,0);
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

    private VertexArray mesh;
    @Getter
    protected Texture texture;
    protected Shader shader;

    float[] vertices;
    int[] indices;
    float[] tcs;
    float[] normals;

    float layer = 0.0f;

    Entity(Location location, Size size, Shader shader) {
        this.location = location;
        this.size = size;
        this.shader = shader;
    }

    void createMesh() {
        Main.instance.runOnUIThread(() -> {
            mesh = new VertexArray(vertices, indices, tcs, normals);
            texture = new Texture(texturePath);
            texture.setReflectivity(0.5f);
        });
    }

    public void createMesh(String filePath) {
        Main.instance.runOnUIThread(() -> {
            mesh = OBJLoader.loadOBJ(filePath);
            texture = new Texture(texturePath);
            texture.setReflectivity(0.5f);
        });
    }

    private boolean canMove(Location location) {
        return location.getWorld().intersects(this, Util.getRectangle(location, getSize())) == null && location.getY() >= 0 && location.getX() >= 0;
    }

    public boolean move(Location location, boolean force) {
        if (!force && !canMove(location)) return false;
        this.location = location;
        return true;
    }

    public boolean move(Location location) {
        return move(location, false);
    }

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

    public boolean onGround() {
        return getLocation().getY() <= 0 || (interactingWithY != null && interactingWithY.getLocation().getY() + interactingWithY.getSize().getHeight() <= getLocation().getY());
    }

    public Rectangle2D getRectangle() {
        return Util.getRectangle(getLocation(), getSize());
    }

    public Location getCenter() {
        return new Location(location.getWorld(), getRectangle().getCenterX(), getRectangle().getCenterY());
    }

    public void rotate(Vector3f rotation) {
        rotation.add(rotation);
        rotation.x %= 360;
        rotation.y %= 360;
        rotation.z %= 360;
    }

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

    public void onInteract(Entity other) {
        //Empty stub
    }

    public World getWorld() {
        return location.getWorld();
    }

    public void render(Camera camera, Vector4f clipPlane) {
        if (mesh == null || shader == null || camera == null || texture == null)
            return;

        shader.enable();
        shader.setUniformMat4f("ml_matrix", MatrixUtil.transformation(new Vector3f((float) location.getX(), (float) location.getY(), layer), rotation.x, rotation.y, rotation.z, new Vector3f((float) size.getWidth(), (float) size.getHeight(), (float) size.getWidth())));
        shader.setUniformMat4f("vw_matrix", MatrixUtil.view(camera));
        shader.setUniform1f("shineDamper", texture.getShineDamper());
        shader.setUniform1f("reflectivity", texture.getReflectivity());
        shader.setUniform4f("clipPlane", clipPlane);
        texture.bind();
        mesh.render();
        shader.disable();
    }

    public boolean animationExists(Class<? extends Animation> clazz) {
        for (Animation a : new ArrayList<>(animations)) {
            if (a.getClass().equals(clazz)) {
                return true;
            }
        }
        return false;
    }

    public Animation getAnimation(Class<? extends Animation> clazz) {
        for (Animation a : new ArrayList<>(animations)) {
            if (a.getClass().equals(clazz)) {
                return a;
            }
        }
        return null;
    }

    public void addAnimation(Animation animation) {
        for (Animation a : new ArrayList<>(animations)) {
            if (a.getClass().equals(animation.getClass())) {
                return;
            }
        }

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
            pendingAnimations.remove(pending);
            addAnimation(pending);
        }

        for (Animation animation : new ArrayList<>(animations)) {
            animation.doAnimation(this);
        }
    }

    public void cleanUp() {
        mesh.cleanUp();
        texture.cleanUp();
    }

    public Entity clone() {
        try {
            return (Entity) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
