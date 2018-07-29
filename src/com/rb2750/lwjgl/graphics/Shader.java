package com.rb2750.lwjgl.graphics;

import com.rb2750.lwjgl.maths.MatrixUtil;
import com.rb2750.lwjgl.util.ShaderUtils;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

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

    public static Shader GENERAL;

    public Shader(String vertex, String fragment)
    {
        ID = ShaderUtils.load(vertex, fragment);
    }

    public static void loadAllShaders()
    {
        GENERAL = new Shader("res/shaders/general.vert", "res/shaders/general.frag");
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
        if(!enabled) enable();
        glUniform1i(getUniform(name), value);
    }

    public void setUniform1f(String name, float value)
    {
        if(!enabled) enable();
        glUniform1f(getUniform(name), value);
    }

    public void loadBoolean(String name, boolean value)
    {
        setUniform1f(name, value ? 1.0f : 0.0f);
    }

    public void setUniform2f(String name, Vector2f vector)
    {
        if(!enabled) enable();

        glUniform2f(getUniform(name), vector.x, vector.y);
    }

    public void setUniform3f(String name, Vector3f vector)
    {
        if(!enabled) enable();

        glUniform3f(getUniform(name), vector.x, vector.y, vector.z);
    }

    public void setUniformMat4f(String name, Matrix4f matrix)
    {
        if(!enabled) enable();

        glUniformMatrix4fv(getUniform(name), false, MatrixUtil.toFloatBuffer(matrix));
    }

    public void enable()
    {
        glUseProgram(ID);
        enabled = true;
    }

    public void disable()
    {
        glUseProgram(0);
        enabled = false;
    }
}
