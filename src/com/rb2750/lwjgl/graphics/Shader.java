package com.rb2750.lwjgl.graphics;

import com.rb2750.lwjgl.maths.MatrixUtil;
import com.rb2750.lwjgl.util.ShaderUtils;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

public class Shader
{
    public static final int VERTEX_ATTRIB = 0;
    public static final int TCOORD_ATTRIB = 1;
    public static final int NORMAL_ATTRIB = 2;

    private boolean enabled = false;

    private final int ID;

    private Map<String, Integer> locationCache = new HashMap<String, Integer>();

    public static Shader BASIC;
    public static Shader GENERAL;
    public static Shader WATER;
    public static Shader GUI;
    public static Shader FONT;

    private static Shader activeShader;

    public Shader(String vertex, String fragment)
    {
        ID = ShaderUtils.load(vertex, fragment);
    }

    public static void loadAllShaders()
    {
        BASIC = new Shader("res/shaders/basic.vert", "res/shaders/basic.frag");
        GENERAL = new Shader("res/shaders/general.vert", "res/shaders/general.frag");
        WATER = new Shader("res/shaders/water.vert", "res/shaders/water.frag");
        GUI = new Shader("res/shaders/gui.vert", "res/shaders/gui.frag");
        FONT = new Shader("res/shaders/font.vert", "res/shaders/font.frag");
    }

    public int getUniform(String name)
    {
        if (locationCache.containsKey(name))
        {
            return locationCache.get(name);
        }

        int result = glGetUniformLocation(ID, name);

        if (result == -1)
        {
            System.err.println("Could not find uniform variable '" + name + "'!");
        }
        else
        {
            locationCache.put(name, result);
        }

        return result;
    }

    public void setUniform1i(String name, int value)
    {
        checkShader();
        glUniform1i(getUniform(name), value);
    }

    public void setUniform1f(String name, float value)
    {
        checkShader();
        glUniform1f(getUniform(name), value);
    }

    public void loadBoolean(String name, boolean value)
    {
        checkShader();
        setUniform1f(name, value ? 1.0f : 0.0f);
    }

    public void setUniform2f(String name, Vector2f vector)
    {
        checkShader();
        glUniform2f(getUniform(name), vector.x, vector.y);
    }

    public void setUniform3f(String name, Vector3f vector)
    {
        checkShader();
        glUniform3f(getUniform(name), vector.x, vector.y, vector.z);
    }

    public void setUniform4f(String name, Vector4f vector)
    {
        checkShader();
        glUniform4f(getUniform(name), vector.x, vector.y, vector.z, vector.w);
    }

    public void setUniformMat4f(String name, Matrix4f matrix)
    {
        checkShader();
        glUniformMatrix4fv(getUniform(name), false, MatrixUtil.toFloatBuffer(matrix));
    }

    private void checkShader()
    {
        disableOtherShader();
        if(!enabled) enable();
    }

    private void disableOtherShader()
    {
        if (activeShader != this && activeShader != null)
            activeShader.disable();
    }

    public void enable()
    {
        if (activeShader != null)
            activeShader.disable();

        glUseProgram(ID);
        enabled = true;
        activeShader = this;
    }

    public void disable()
    {
        glUseProgram(0);
        enabled = false;
        activeShader = null;
    }

    public void cleanUp()
    {
        glDeleteProgram(ID);
    }

    public static void cleanUpAll()
    {
        Shader.BASIC.cleanUp();
        Shader.GENERAL.cleanUp();
        Shader.WATER.cleanUp();
        Shader.FONT.cleanUp();
        Shader.GUI.cleanUp();
    }
}
