package com.rb2750.lwjgl.world;

import com.rb2750.lwjgl.animations.SquashAnimation;
import com.rb2750.lwjgl.entities.*;
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

    public void addPointLight(PointLight pointLight)
    {
        if (pointLights.size() == MAX_POINT_LIGHTS)
            throw new IndexOutOfBoundsException("There is already " + MAX_POINT_LIGHTS + " point lights.");

        pointLights.add(pointLight);
    }

    public void addSpotLight(SpotLight spotLight)
    {
        if (spotLights.size() == MAX_SPOT_LIGHTS)
            throw new IndexOutOfBoundsException("There is already " + MAX_SPOT_LIGHTS + " spot lights.");

        spotLights.add(spotLight);
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

                if (interactingWithX != null && interactingWithX.getLocation().getY() + interactingWithX.getSize().getHeight() - entity.getLocation().getY() < 60 && entity.onGround() && entity.move(entity.getLocation().clone().setY(interactingWithX.getLocation().getY() + interactingWithX.getSize().getHeight()))) {
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

    public void renderWorld(Camera camera, Vector4f clipPlane) {
        prepareShaders();

        for (Entity entity : entities) {
            renderEntity(entity, camera, clipPlane);
        }
    }

    private void prepareShaders()
    {
        Shader.GENERAL.setUniform3f("ambientLight", ambientLight);
        Shader.WATER.setUniform3f("ambientLight", ambientLight);

        if (directionalLight != null)
        {
            shaderSetDirectionalLight("dirLight", directionalLight);
        }

        int i = 0;

        for (PointLight pointLight : pointLights)
        {
            shaderSetPointLight("pointLights[" + i + "]", pointLight);
            i++;
        }

        i = 0;

        for (SpotLight spotLight : spotLights)
        {
            shaderSetSpotLight("spotLights[" + i + "]", spotLight);
            i++;
        }
    }

    private void shaderSetBaseLight(String uniformName, Light base)
    {
        Shader.GENERAL.setUniform3f(uniformName + ".colour", base.getColour());
        Shader.GENERAL.setUniform1f(uniformName + ".intensity", base.getIntensity());

        Shader.WATER.setUniform3f(uniformName + ".colour", base.getColour());
        Shader.WATER.setUniform1f(uniformName + ".intensity", base.getIntensity());
    }

    private void shaderSetDirectionalLight(String uniformName, DirectionalLight directionalLight)
    {
        shaderSetBaseLight(uniformName + ".base", directionalLight.getBase());

        Shader.GENERAL.setUniform3f(uniformName + ".direction", directionalLight.getDirection());
        //Shader.WATER.setUniform3f(uniformName + ".direction", directionalLight.getDirection());
    }

    private void shaderSetPointLight(String uniformName, PointLight pointLight)
    {
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

    private void shaderSetSpotLight(String uniformName, SpotLight spotLight)
    {
        shaderSetPointLight(uniformName + ".pointLight", spotLight.getPointLight());

        Shader.GENERAL.setUniform3f(uniformName + ".direction", spotLight.getDirection());
        Shader.GENERAL.setUniform1f(uniformName + ".cutoff", spotLight.getCutoff());

        Shader.WATER.setUniform3f(uniformName + ".direction", spotLight.getDirection());
        Shader.WATER.setUniform1f(uniformName + ".cutoff", spotLight.getCutoff());
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

    public void update() {
        handleEntities();
    }
}
