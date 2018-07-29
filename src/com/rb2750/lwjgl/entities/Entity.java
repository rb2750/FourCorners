package com.rb2750.lwjgl.entities;

import com.rb2750.lwjgl.Main;
import com.rb2750.lwjgl.animations.Animation;
import com.rb2750.lwjgl.graphics.*;
import com.rb2750.lwjgl.maths.*;
import com.rb2750.lwjgl.util.*;
import com.rb2750.lwjgl.world.World;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public abstract class Entity implements Cloneable {
    @Getter
    private Location location;
    protected String texturePath;
    @Getter
    private Vector2 acceleration = new Vector2(0, 0);
    @Getter
    @Setter
    private double rotation = 0;
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
    private java.util.List<Animation> animations = new ArrayList<>();
    private List<Animation> pendingAnimations = new ArrayList<>();
    @Getter
    @Setter
    private boolean squat = false;

    protected VertexArray mesh;
    protected Texture texture;
    protected Shader shader;

    protected float[] vertices;
    protected int[] indices;
    protected float[] tcs;
    protected float[] normals;

    protected float layer = 0.0f;

    public Entity(Location location, Size size, Shader shader) {
        this.location = location;
        this.size = size;
        this.shader = shader;
    }

    public void createMesh() {
        Main.instance.runOnUIThread(() -> {
            mesh = new VertexArray(vertices, indices, tcs, normals);
            texture = new Texture(texturePath);
        });
    }

    public void createMesh(String filePath) {
        Main.instance.runOnUIThread(() -> {
            mesh = OBJLoader.loadOBJ(filePath);
            texture = new Texture(texturePath);
            texture.setReflectivity(0.5f);
        });
    }

    public boolean move(Location location, boolean force) {
        if (!force && (location.getWorld().intersects(this, Util.getRectangle(location, getSize())) != null || location.getY() < 0 || location.getX() < 0))
            return false;
        this.location = location;
        return true;
    }

    public boolean move(Location location) {
        return move(location, false);
    }

    public void setSize(Size size) {
        Entity intersectsWith = location.getWorld().intersects(this, Util.getRectangle(getLocation(), size));

        if (intersectsWith != null) {
            boolean left = intersectsWith.getLocation().getX() + intersectsWith.getSize().getWidth() <= getLocation().getX();
            boolean right = intersectsWith.getLocation().getX() > getLocation().getX();
            boolean top = intersectsWith.getLocation().getY() >= getLocation().getY();
            boolean bottom = intersectsWith.getLocation().getY() + intersectsWith.getSize().getHeight() > getLocation().getY();

            if (top && !left && !right) return;
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
                else return;
            }
        }

        this.size = size;
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

    public void rotate(double angle) {
        rotation += angle;
        rotation %= 360;
    }

    public void update(Camera camera) {
        if (getAcceleration().getX() < 0) setFacing(Direction.LEFT);
        if (getAcceleration().getX() > 0) setFacing(Direction.RIGHT);
//        glMatrixMode(GL_MODELVIEW);
//        glPushMatrix();
        double translateX = getLocation().getX() + (getSize().getWidth() / 2);
        double translateY = getLocation().getY() + (getSize().getHeight() / 2);
//        glTranslated(translateX, translateY, 0);
//        glRotated(rotation, 0, 0, 1);
//        glTranslated(-translateX, -translateY, 0);
        render(camera);
//        glPopMatrix();
    }

    int x = 0;

    public World getWorld() {
        return location.getWorld();
    }

    public void render(Camera camera) {
        handleAnimations();

        renderEntity(camera);
    }

    public void renderEntity(Camera camera) {
        if (mesh == null || shader == null || camera == null || texture == null)
            return;

        shader.enable();
        shader.setUniformMat4f("ml_matrix", MatrixUtil.transformation(new Vector3f((float)location.getX(), (float)location.getY(), layer), 0, (float) rotation, 0, new Vector3f((float)size.getWidth(), (float)size.getHeight(), (float)size.getWidth())));
        shader.setUniformMat4f("vw_matrix", MatrixUtil.view(camera));
        shader.setUniform1f("shineDamper", texture.getShineDamper());
        shader.setUniform1f("reflectivity", texture.getReflectivity());
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
        //Stop any issues with changing size while squatting
        //if (squat && !(animation instanceof SquatAnimation) && (animation.getFlags() & AnimationFlag.SIZE) > 0) return;

        for (Animation a : new ArrayList<>(animations)) {
            if (a.getClass().equals(animation.getClass())) {
//                pendingAnimations.add(animation);
                return;
            }
        }

        //Find conflicts (stop glitchy rendering)
        //for (Animation a : animations) if ((a.getFlags() & animation.getFlags()) > 0) return;

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
            animation.doAnimation(this);
        }
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
