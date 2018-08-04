package com.rb2750.lwjgl.world;

import com.rb2750.lwjgl.animations.SquashAnimation;
import com.rb2750.lwjgl.entities.Camera;
import com.rb2750.lwjgl.entities.Entity;
import com.rb2750.lwjgl.util.Util;
import lombok.Getter;
import org.joml.Vector4f;
import org.ode4j.ode.*;
import static org.ode4j.ode.OdeConstants.dContactApprox1;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class World {
    @Getter
    private List<Entity> entities = new ArrayList<>();
    @Getter
    private WorldSettings settings;

    @Getter
    private DSpace space;
    @Getter
    private DWorld physicsWorld;
    private DJointGroup contactGroup;

    private DGeom.DNearCallback nearCallback = new DGeom.DNearCallback() {
        @Override
        public void call(Object o, DGeom dGeom, DGeom dGeom1) {
            nearCallback(o, dGeom, dGeom1);
        }
    };

    public World(WorldSettings settings) {
        this.settings = settings;

//        physicsWorld = OdeHelper.createWorld();
//        physicsWorld.setGravity(0, 0, 0);
//        physicsWorld.setQuickStepNumIterations(2);
//
//        space = OdeHelper.createSimpleSpace(null);
//
//        contactGroup = OdeHelper.createJointGroup();
//        DGeom ground = OdeHelper.createPlane(space, 0, 0, 0, 0);


    }

    private void nearCallback(Object data, DGeom o1, DGeom o2) {
        DBody b1 = o1.getBody();
        DBody b2 = o2.getBody();

        final int MAX_CONTACTS = 8;

        DContactBuffer contacts = new DContactBuffer(MAX_CONTACTS);

        int numc = OdeHelper.collide(o1, o2, MAX_CONTACTS, contacts.getGeomBuffer());

        for (int i = 0; i < numc; i++) {
            contacts.get(i).surface.mode = dContactApprox1;
            contacts.get(i).surface.mu = 5;
            DJoint c = OdeHelper.createContactJoint(physicsWorld, contactGroup, contacts.get(i));
            c.attach(b1, b2);
        }
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    private void handleGravity(Entity entity) {
        if (!entity.isGravity()) return;

        if (entity.getLocation().getY() > 0) {
            entity.getAcceleration().setY(entity.getAcceleration().getY() - settings.getGravity());
        } else if (entity.getAcceleration().getY() < 0) {
            if (entity.getAcceleration().getY() < -25) entity.addAnimation(new SquashAnimation());
            entity.getAcceleration().setY(0);
        }
    }

    private void handleFriction(Entity entity) {
        double friction = entity.onGround() ? settings.getFrictionGround() : settings.getFrictionAir();
        if (entity.getAcceleration().getX() > 0)
            entity.getAcceleration().setX(entity.getAcceleration().getX() - friction);
        if (entity.getAcceleration().getX() < 0)
            entity.getAcceleration().setX(entity.getAcceleration().getX() + friction);
    }

    private void handleEntities() {
        for (Entity entity : entities) {
            handleGravity(entity);
            handleFriction(entity);

            entity.update();

            boolean skipY = false;
            float x = (float) Math.max(entity.getLocation().getX() + entity.getAcceleration().getX(), 0);
            float y = (float) Math.max(entity.getLocation().getY() + entity.getAcceleration().getY(), 0);

            if (!entity.isCanInteract()) {
                entity.getLocation().setX(x);
                entity.getLocation().setY(y);
                return;
            }

            Entity interactingWithX = null;
            Entity interact = null;

            if (entity.getAcceleration().getX() != 0 && ((interact = intersects(entity, entity.getRectangle())) != null || (interactingWithX = intersects(entity, Util.getRectangle(x, entity.getRectangle().getY(), entity.getSize()))) == null)) {
                if (interact != null && entity.getAcceleration().getX() >= 0) {
                    entity.getLocation().setX((float) Util.getNearestPointInPerimeter(interact.getRectangle(), x, y).getX() - entity.getSize().getWidth());
                } else {
                    entity.getLocation().setX(x);
                    entity.setInteractingWithX(null);
                }
            } else {
                /*
                  Handle steps
                 */

                if (interactingWithX != null && interactingWithX.getLocation().getY() + interactingWithX.getSize().getHeight() - entity.getLocation().getY() < 60 && entity.onGround() && entity.move(entity.getLocation().clone().setY(interactingWithX.getLocation().getY() + interactingWithX.getSize().getHeight()))) {
                    skipY = true;
                } else {
                    if (interactingWithX != null) {
                        float pointX = (float) Util.getNearestPointInPerimeter(interactingWithX.getRectangle(), entity.getLocation().getX(), entity.getLocation().getY()).getX();
                        if (pointX == interactingWithX.getRectangle().getX()) pointX -= entity.getSize().getWidth();
                        entity.getLocation().setX(pointX);
                    }
                    entity.getAcceleration().setX(0);
                }
                entity.setInteractingWithX(interactingWithX);
            }

            if (!skipY) {
                Entity interactingWithY = null;

                if (entity.getAcceleration().getY() != 0 && (interactingWithY = intersects(entity, Util.getRectangle(entity.getRectangle().getX(), y, entity.getSize()))) == null) {
                    entity.getLocation().setY(y);
                    entity.setInteractingWithY(null);
                } else {
                    if (interactingWithY != null) {
                        float pointY = (float) Util.getNearestPointInPerimeter(interactingWithY.getRectangle(), entity.getLocation().getX(), entity.getLocation().getY()).getY();
                        if (pointY == interactingWithY.getRectangle().getY()) pointY -= entity.getSize().getHeight();
                        entity.getLocation().setY(pointY);
                        if (entity.getAcceleration().getY() < -25) entity.addAnimation(new SquashAnimation());
                    }
                    entity.getAcceleration().setY(0);
                    if (interactingWithY != null) entity.setInteractingWithY(interactingWithY);
                }
            }
        }
    }

    public void renderWorld(Camera camera, Vector4f clipPlane) {
        for (Entity entity : entities) {
            renderEntity(entity, camera, clipPlane);
        }
    }

    private void renderEntity(Entity entity, Camera camera, Vector4f clipPlane) {
        entity.render(camera, clipPlane);
    }

    public Entity intersects(Entity e, Rectangle2D rect) {
        for (Entity entity : getEntities()) {
            if (e == entity || !entity.isCanInteract()) continue;
            if (entity.getRectangle().intersects(rect)) {
                return entity;
            }
        }
        return null;
    }

    public void update(float deltaTime) {
//        space.collide(null, nearCallback);
//        physicsWorld.quickStep(deltaTime);
        handleEntities();
    }
}
