package com.rb2750.lwjgl.graphics;

import com.rb2750.lwjgl.maths.Matrix4;
import com.rb2750.lwjgl.maths.Vector2;
import com.rb2750.lwjgl.maths.Vector3;
import com.rb2750.lwjgl.util.ShaderUtils;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

public class Shader
{
    public static final int VERTEX_ATTRIB = 0;
    public static final int TCOORD_ATTRIB = 1;

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

    public void setUniform2f(String name, Vector2 vector)
    {
        if(!enabled) enable();

        glUniform2f(getUniform(name), (float)vector.getX(), (float)vector.getY());
    }

    public void setUniform3f(String name, Vector3 vector)
    {
        if(!enabled) enable();

        glUniform3f(getUniform(name), (float)vector.getX(), (float)vector.getY(), (float)vector.getZ());
    }

    public void setUniformMat4f(String name, Matrix4 matrix)
    {
        if(!enabled) enable();

        glUniformMatrix4fv(getUniform(name), false, matrix.toFloatBuffer());
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