package com.rb2750.lwjgl.world;

import com.rb2750.lwjgl.animations.SquashAnimation;
import com.rb2750.lwjgl.entities.Camera;
import com.rb2750.lwjgl.entities.Entity;
import com.rb2750.lwjgl.graphics.*;
import com.rb2750.lwjgl.util.Util;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class World {
    @Getter
    private List<Entity> entities = new ArrayList<>();
    @Getter
    private Entity[][] worldTiles;
    @Getter
    private List<DisplayObject> displayObjects = new ArrayList<>();
    @Getter
    private WorldSettings settings;

//    @Getter
//    private DSpace space;
//    @Getter
//    private DWorld physicsWorld;
//    private DJointGroup contactGroup;
//
//    private DGeom.DNearCallback nearCallback = new DGeom.DNearCallback() {
//        @Override
//        public void call(Object o, DGeom dGeom, DGeom dGeom1) {
//            nearCallback(o, dGeom, dGeom1);
//        }
//    };

    public World(WorldSettings settings) {
        this.settings = settings;
        worldTiles = new Entity[settings.getWorldWidth()][settings.getWorldHeight()];

//        physicsWorld = OdeHelper.createWorld();
//        physicsWorld.setGravity(0, 0, 0);
//        physicsWorld.setQuickStepNumIterations(2);
//
//        space = OdeHelper.createSimpleSpace(null);
//
//        contactGroup = OdeHelper.createJointGroup();
//        DGeom ground = OdeHelper.createPlane(space, 0, 0, 0, 0);


    }

//    private void nearCallback(Object data, DGeom o1, DGeom o2) {
//        DBody b1 = o1.getBody();
//        DBody b2 = o2.getBody();
//
//        final int MAX_CONTACTS = 8;
//
//        DContactBuffer contacts = new DContactBuffer(MAX_CONTACTS);
//
//        int numc = OdeHelper.collide(o1, o2, MAX_CONTACTS, contacts.getGeomBuffer());
//
//        for (int i = 0; i < numc; i++) {
//            contacts.get(i).surface.mode = dContactApprox1;
//            contacts.get(i).surface.mu = 5;
//            DJoint c = OdeHelper.createContactJoint(physicsWorld, contactGroup, contacts.get(i));
//            c.attach(b1, b2);
//        }
//    }

    public static final int MAX_POINT_LIGHTS = 4;
    public static final int MAX_SPOT_LIGHTS = 4;

    @Getter
    @Setter
    private Vector3f ambientLight = new Vector3f(0.5f, 0.5f, 0.5f);
    @Setter
    private DirectionalLight directionalLight;
    private List<PointLight> pointLights = new ArrayList<PointLight>();
    private List<SpotLight> spotLights = new ArrayList<SpotLight>();

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public void addDisplayObject(DisplayObject object) {
        displayObjects.add(object);
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity);
    }

    public void removeDisplayObject(DisplayObject object) {
        displayObjects.remove(object);
    }

    public void addPointLight(PointLight pointLight) {
        if (pointLights.size() == MAX_POINT_LIGHTS)
            throw new IndexOutOfBoundsException("There is already " + MAX_POINT_LIGHTS + " point lights.");

        pointLights.add(pointLight);
    }

    public void addSpotLight(SpotLight spotLight) {
        if (spotLights.size() == MAX_SPOT_LIGHTS)
            throw new IndexOutOfBoundsException("There is already " + MAX_SPOT_LIGHTS + " spot lights.");

        spotLights.add(spotLight);
    }

    private void handleGravity(Entity entity) {
        if (!entity.isGravity()) return;

        if (entity.getLocation().getY() > 0) {
            entity.getAcceleration().y = (float) (entity.getAcceleration().y - settings.getGravity());
        } else if (entity.getAcceleration().y < 0) {
            if (entity.getAcceleration().y < -25) entity.addAnimation(new SquashAnimation());
            entity.getAcceleration().y = 0;
        }
    }

    private void handleFriction(Entity entity) {
        float friction = entity.onGround() ? settings.getFrictionGround() : settings.getFrictionAir();
        if (entity.getAcceleration().x > 0)
            entity.getAcceleration().x = entity.getAcceleration().x - friction;
        if (entity.getAcceleration().x < 0)
            entity.getAcceleration().x = entity.getAcceleration().x + friction;
    }

    private void handleEntities() {
        for (Entity entity : entities) {
            handleGravity(entity);
//            handleFriction(entity);

            entity.update();

            boolean skipY = false;
            float x = Math.max(entity.getLocation().getX() + entity.getAcceleration().x, 0);
            float y = Math.max(entity.getLocation().getY() + entity.getAcceleration().y, 0);

            if (!entity.isCanInteract()) {
                entity.getLocation().setX(x);
                entity.getLocation().setY(y);
                return;
            }

            Entity interactingWithX = null;
            Entity interact;

            if (entity.getAcceleration().x != 0 && ((interact = intersects(entity, entity.getRectangle())) != null || (interactingWithX = intersects(entity, Util.getRectangle(x, entity.getRectangle().getY(), entity.getSize()))) == null)) {
                if (interact != null && entity.getAcceleration().x >= 0 && entity.size.getHeight() < interact.size.getHeight()) {
                    entity.getLocation().setX((float) Util.getNearestPointInPerimeter(interact.getRectangle(), x, y).getX() - entity.getSize().getWidth()); //TODO FIX
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
                    entity.setInteractingWithX(interactingWithX);
                    updateInteractEvents(entity);

                    if (interactingWithX != null) {
//                        interactingWithX.onInteract(entity, null);
                        float pointX = (float) Util.getNearestPointInPerimeter(interactingWithX.getRectangle(), entity.getLocation().getX(), entity.getLocation().getY()).getX();
                        if (pointX == interactingWithX.getRectangle().getX()) pointX -= entity.getSize().getWidth();
                        entity.getLocation().setX(pointX);
                    }
//                    entity.getAcceleration().x = 0;
                }
            }

            if (!skipY) {
                Entity interactingWithY = null;

                if (entity.getAcceleration().y != 0 && (interactingWithY = intersects(entity, Util.getRectangle(entity.getRectangle().getX(), y, entity.getSize()))) == null) {
                    entity.getLocation().setY(y);
                    entity.setInteractingWithY(null);
                } else {
                    entity.setInteractingWithY(interactingWithY);
                    updateInteractEvents(entity);

                    if (interactingWithY != null) {
                        float pointY = (float) Util.getNearestPointInPerimeter(interactingWithY.getRectangle(), entity.getLocation().getX(), entity.getLocation().getY()).getY();
                        if (pointY == interactingWithY.getRectangle().getY()) pointY -= entity.getSize().getHeight();
                        entity.getLocation().setY(pointY);
                        if (entity.getAcceleration().y < -25) entity.addAnimation(new SquashAnimation());
                        if (entity.getAcceleration().y < 0 || entity.getLocation().getY() + entity.size.getHeight() <= interactingWithY.getLocation().getY() && entity.getAcceleration().y > 0)
                            entity.getAcceleration().y = 0;
                    }
                }
            }
        }
    }

    public void updateInteractEvents(Entity entity) {
        if (entity.getInteractingWithX() != null || entity.getInteractingWithY() != null) {
            entity.onInteract(entity.getInteractingWithX(), entity.getInteractingWithY());
            if (entity.getInteractingWithX() != null && entity.getInteractingWithX().equals(entity.getInteractingWithY())) {
                entity.getInteractingWithX().onInteract(entity, entity);
            } else {
                if (entity.getInteractingWithX() != null) entity.getInteractingWithX().onInteract(entity, null);
                if (entity.getInteractingWithY() != null) entity.getInteractingWithY().onInteract(null, entity);
            }
        }
    }

    public void renderWorld(Camera camera, Vector4f clipPlane) {
        prepareShaders();

        for (Entity[] tiles : worldTiles) {
            for (Entity tile : tiles) {
                if (tile != null) {
                    renderObject(tile, camera, clipPlane);
                }
            }
        }
        for (Entity entity : entities) {
            renderObject(entity, camera, clipPlane);
        }
        for (DisplayObject object : displayObjects) {
            renderObject(object, camera, clipPlane);
        }
    }

    private void prepareShaders() {
        Shader.GENERAL.setUniform3f("ambientLight", ambientLight);
        Shader.WATER.setUniform3f("ambientLight", ambientLight);

        if (directionalLight != null) {
            shaderSetDirectionalLight("dirLight", directionalLight);
        }

        int i = 0;

        for (PointLight pointLight : pointLights) {
            shaderSetPointLight("pointLights[" + i + "]", pointLight);
            i++;
        }

        i = 0;

        for (SpotLight spotLight : spotLights) {
            shaderSetSpotLight("spotLights[" + i + "]", spotLight);
            i++;
        }
    }

    private void shaderSetBaseLight(String uniformName, Light base) {
        Shader.GENERAL.setUniform3f(uniformName + ".colour", base.getColour());
        Shader.GENERAL.setUniform1f(uniformName + ".intensity", base.getIntensity());

        Shader.WATER.setUniform3f(uniformName + ".colour", base.getColour());
        Shader.WATER.setUniform1f(uniformName + ".intensity", base.getIntensity());
    }

    private void shaderSetDirectionalLight(String uniformName, DirectionalLight directionalLight) {
        shaderSetBaseLight(uniformName + ".base", directionalLight.getBase());

        Shader.GENERAL.setUniform3f(uniformName + ".direction", directionalLight.getDirection());
        //Shader.WATER.setUniform3f(uniformName + ".direction", directionalLight.getDirection());
    }

    private void shaderSetPointLight(String uniformName, PointLight pointLight) {
        shaderSetBaseLight(uniformName + ".base", pointLight.getBase());

        Shader.GENERAL.setUniform1f(uniformName + ".atten.constant", pointLight.getAtten().getConstant());
        Shader.GENERAL.setUniform1f(uniformName + ".atten.linear", pointLight.getAtten().getLinear());
        Shader.GENERAL.setUniform1f(uniformName + ".atten.exponent", pointLight.getAtten().getExponent());
        Shader.GENERAL.setUniform3f(uniformName + ".position", pointLight.getPosition());
        Shader.GENERAL.setUniform1f(uniformName + ".range", pointLight.getRange());

        Shader.WATER.setUniform1f(uniformName + ".atten.constant", pointLight.getAtten().getConstant());
        Shader.WATER.setUniform1f(uniformName + ".atten.linear", pointLight.getAtten().getLinear());
        Shader.WATER.setUniform1f(uniformName + ".atten.exponent", pointLight.getAtten().getExponent());
        Shader.WATER.setUniform3f(uniformName + ".position", pointLight.getPosition());
        Shader.WATER.setUniform1f(uniformName + ".range", pointLight.getRange());
    }

    private void shaderSetSpotLight(String uniformName, SpotLight spotLight) {
        shaderSetPointLight(uniformName + ".pointLight", spotLight.getPointLight());

        Shader.GENERAL.setUniform3f(uniformName + ".direction", spotLight.getDirection());
        Shader.GENERAL.setUniform1f(uniformName + ".cutoff", spotLight.getCutoff());

        Shader.WATER.setUniform3f(uniformName + ".direction", spotLight.getDirection());
        Shader.WATER.setUniform1f(uniformName + ".cutoff", spotLight.getCutoff());
    }

    private void renderObject(DisplayObject object, Camera camera, Vector4f clipPlane) {
        object.render(camera, clipPlane);
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
