package com.rb2750.lwjgl.world;

import com.rb2750.lwjgl.animations.SquashAnimation;
import com.rb2750.lwjgl.entities.Camera;
import com.rb2750.lwjgl.entities.Entity;
import com.rb2750.lwjgl.graphics.*;
import com.rb2750.lwjgl.world.collisions.StandardCollision;
import lombok.Getter;
import lombok.Setter;
import org.dyn4j.geometry.Vector2;
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
    @Getter
    private org.dyn4j.dynamics.World physicsWorld;

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
        physicsWorld = new org.dyn4j.dynamics.World();
        physicsWorld.setGravity(new Vector2(0, -1));

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
//        if (entity.getBody() == null || physicsWorld.getBodies().contains(entity.getBody())) return;
//        physicsWorld.addBody(entity.getBody());
//        physicsWorld.addListener(new StandardCollision(entity.getBody()));
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

    private List<Entity> getInteractionEntities() {
        List<Entity> interactionEntities = new ArrayList<>(entities);
        for (Entity[] tiles : worldTiles)
            for (Entity tile : tiles)
                if (tile != null && !tile.isInvisible())
                    interactionEntities.add(tile);
        return interactionEntities;
    }

    private void handleEntities() {
        for (Entity entity : getInteractionEntities()) {
            handleGravity(entity);
//            handleFriction(entity);

            entity.update();

            float x = Math.max(entity.getLocation().getX() + entity.getAcceleration().x, 0);
            float y = Math.max(entity.getLocation().getY() + entity.getAcceleration().y, 0);

            if (!entity.isCanInteract()) {
                entity.getLocation().setX(x);
                entity.getLocation().setY(y);
                return;
            }


        }
    }

    /**
     * Call the interaction events
     *
     * @param entity The entity the events are related to.
     * @return Whether the interaction should be allowed or denied.
     */
    public boolean updateInteractEvents(Entity entity) {
        boolean denyInteraction = false;
        if (entity.getInteractingWithX() != null || entity.getInteractingWithY() != null) {
            if (entity.onInteract(entity.getInteractingWithX(), entity.getInteractingWithY()))
                denyInteraction = true;
            if (entity.getInteractingWithX() != null && entity.getInteractingWithX().equals(entity.getInteractingWithY())) {
                if (entity.getInteractingWithX().onInteract(entity, entity)) denyInteraction = true;
            } else {
                if (entity.getInteractingWithX() != null)
                    if (entity.getInteractingWithX().onInteract(entity, null)) denyInteraction = true;
                if (entity.getInteractingWithY() != null)
                    if (entity.getInteractingWithY().onInteract(null, entity)) denyInteraction = true;
            }
        }
        return denyInteraction;
    }

    public void renderWorld(Camera camera, Vector4f clipPlane) {
        prepareShaders();

        for (Entity[] tiles : worldTiles)
            for (Entity tile : tiles)
                if (tile != null && !tile.isInvisible())
                    renderObject(tile, camera, clipPlane);
        for (Entity entity : entities) if (!entity.isInvisible()) renderObject(entity, camera, clipPlane);
        for (DisplayObject object : displayObjects)
            if (!object.isInvisible()) renderObject(object, camera, clipPlane);
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
        for (Entity entity : getInteractionEntities()) {
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
