package com.rb2750.lwjgl.world;

import com.rb2750.lwjgl.input.Action;
import com.rb2750.lwjgl.input.Input;
import com.rb2750.lwjgl.animations.SquashAnimation;
import com.rb2750.lwjgl.entities.Camera;
import com.rb2750.lwjgl.entities.Entity;
import com.rb2750.lwjgl.entities.Player;
import com.rb2750.lwjgl.entities.Tile;
import com.rb2750.lwjgl.util.Util;
import lombok.Getter;
import org.joml.Vector4f;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class World {
    @Getter
    private List<Entity> entities = new ArrayList<>();

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    private void handleGravity(Entity entity) {
        if (!entity.isGravity()) return;

        if (entity.getLocation().getY() > 0) {
            entity.getAcceleration().setY(entity.getAcceleration().getY() - WorldSettings.GRAVITY);
        } else if (entity.getAcceleration().getY() < 0) {
            if (entity.getAcceleration().getY() < -25) entity.addAnimation(new SquashAnimation());
            entity.getAcceleration().setY(0);
        }
    }

    private void handleFriction(Entity entity) {
        double friction = entity.onGround() ? WorldSettings.frictionGround : WorldSettings.frictionAir;
        if (entity.getAcceleration().getX() > 0)
            entity.getAcceleration().setX(entity.getAcceleration().getX() - friction);
        if (entity.getAcceleration().getX() < 0)
            entity.getAcceleration().setX(entity.getAcceleration().getX() + friction);
    }

    private void handleEntities() {
        for (Entity entity : entities) {
            handleGravity(entity);
            handleFriction(entity);

            boolean skipY = false;
            double x = Math.max(entity.getLocation().getX() + entity.getAcceleration().getX(), 0);
            double y = Math.max(entity.getLocation().getY() + entity.getAcceleration().getY(), 0);

            Entity interactingWithX = null;

            if (entity.getAcceleration().getX() != 0 && (intersects(entity, entity.getRectangle()) != null || (interactingWithX = intersects(entity, Util.getRectangle(x, entity.getRectangle().getY(), entity.getSize()))) == null)) {
                entity.getLocation().setX(x);
                entity.setInteractingWithX(null);
            } else {
                /*
                  Handle steps
                 */
                if (interactingWithX != null && interactingWithX.getLocation().getY() + interactingWithX.getSize().getHeight() - entity.getLocation().getY() < 60 && entity.onGround()) {
                    entity.getLocation().setY(interactingWithX.getLocation().getY() + interactingWithX.getSize().getHeight());
                    skipY = true;
                } else {
                    if (interactingWithX != null) {
                        double pointX = Util.getNearestPointInPerimeter(interactingWithX.getRectangle(), entity.getLocation().getX(), entity.getLocation().getY()).getX();
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
                        double pointY = Util.getNearestPointInPerimeter(interactingWithY.getRectangle(), entity.getLocation().getX(), entity.getLocation().getY()).getY();
                        if (pointY == interactingWithY.getRectangle().getY()) pointY -= entity.getSize().getHeight();
                        entity.getLocation().setY(pointY);
                        if (entity.getAcceleration().getY() < -25) entity.addAnimation(new SquashAnimation());
                    }
                    entity.getAcceleration().setY(0);
                    if (interactingWithY != null) entity.setInteractingWithY(interactingWithY);
                }
            }

            entity.update();
        }
    }

    public void renderWorld(Camera camera, Vector4f clipPlane)
    {
        for (Entity entity : entities)
        {
            renderEntity(entity, camera, clipPlane);
        }
    }

    private void renderEntity(Entity entity, Camera camera, Vector4f clipPlane) {
        entity.render(camera, clipPlane);
    }

    public Entity intersects(Entity e, Rectangle2D rect) {
        for (Entity entity : getEntities()) {
            if (e == entity || !entity.isCanBeInteractedWith()) continue;
            if (entity.getRectangle().intersects(rect)) {
                return entity;
            }
        }
        return null;
    }

    public void update(Player player, Tile protoTile) {
        handleEntities();

        if(Input.ButtonMap.get(Action.Clear).state) {
            getEntities().clear();

            addEntity(player);
            addEntity(protoTile);
        }
    }
}
