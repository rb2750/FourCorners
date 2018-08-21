package com.rb2750.lwjgl.graphics;

import com.rb2750.lwjgl.entities.Camera;
import com.rb2750.lwjgl.graphics.postprocess.WaterFrameBuffers;
import com.rb2750.lwjgl.maths.MatrixUtil;
import com.rb2750.lwjgl.util.Util;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;

public class WaterRenderer
{
    private static final String DUDV_MAP = "res/textures/waterDUDV2.png";
    private static final String NORMAL_MAP = "res/textures/matchingNormalMap.png";
    private static final float WAVE_SPEED = 0.03f;

    private VertexArray mesh;
    private WaterFrameBuffers fbos;

    private float moveFactor = 0.0f;
    private long startTime;

    private Texture dudvTexture;
    private Texture normalMap;

    public WaterRenderer(Matrix4f projectionMatrix, WaterFrameBuffers fbos)
    {
        this.fbos = fbos;

        startTime = Util.getTime();

        dudvTexture = new Texture(DUDV_MAP);
        normalMap = new Texture(NORMAL_MAP);

        Shader.WATER.setUniformMat4f("pr_matrix", projectionMatrix);
        Shader.WATER.setUniform1i("reflectionTex", 0);
        Shader.WATER.setUniform1i("refractionTex", 1);
        Shader.WATER.setUniform1i("dudvMap", 2);
        Shader.WATER.setUniform1i("normalMap", 3);
        Shader.WATER.setUniform1i("depthMap", 4);
        Shader.WATER.disable();

        float[] vertices = { -1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f };

        mesh = new VertexArray(vertices, 2);
    }

    public void render(List<Water> water, Camera camera, float deltaTime)
    {
        Shader.WATER.setUniformMat4f("vw_matrix", MatrixUtil.view(camera));
        Shader.WATER.setUniform3f("cameraPosition", camera.getPosition());

        moveFactor += WAVE_SPEED * deltaTime;
        moveFactor %= 1;

        Shader.WATER.setUniform1f("moveFactor", moveFactor);

        mesh.bind();

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, fbos.getReflectionTexture());
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, fbos.getRefractionTexture());
        glActiveTexture(GL_TEXTURE2);
        glBindTexture(GL_TEXTURE_2D, dudvTexture.getTexture());
        glActiveTexture(GL_TEXTURE3);
        glBindTexture(GL_TEXTURE_2D, normalMap.getTexture());
        glActiveTexture(GL_TEXTURE4);
        glBindTexture(GL_TEXTURE_2D, fbos.getRefractionDepthTexture());

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        for (Water tile : water)
        {
            Shader.WATER.setUniformMat4f("ml_matrix", MatrixUtil.transformation(new Vector3f(tile.getX(), tile.getHeight(), tile.getZ()), 0, 0, 0, new Vector3f(Water.TILE_SIZE, Water.TILE_SIZE, Water.TILE_SIZE)));

            glDrawArrays(GL_TRIANGLES, 0, mesh.getCount());
        }

        glDisable(GL_BLEND);

        mesh.unbind();

        Shader.WATER.disable();
    }

    public void cleanUp()
    {
        mesh.cleanUp();
        fbos.cleanUp();
        dudvTexture.cleanUp();
        normalMap.cleanUp();
    }

    public void setProjectionMatrix(Matrix4f projectionMatrix)
    {
        Shader.WATER.setUniformMat4f("pr_matrix", projectionMatrix);
        Shader.WATER.disable();
    }
}
