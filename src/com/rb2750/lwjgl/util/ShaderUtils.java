package com.rb2750.lwjgl.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ShaderUtils
{
    private static String currentVertPath;
    private static String currentFragPath;

    public static int load(String vertPath, String fragPath)
    {
        currentVertPath = vertPath;
        currentFragPath = fragPath;

        String vert = FileUtils.loadAsString(vertPath);
        String frag = FileUtils.loadAsString(fragPath);

        return create(vert, frag);
    }

    public static int create(String vert, String frag)
    {
        int program = glCreateProgram();
        int vertID = glCreateShader(GL_VERTEX_SHADER);
        int fragID = glCreateShader(GL_FRAGMENT_SHADER);

        glShaderSource(vertID, vert);
        glShaderSource(fragID, frag);

        glCompileShader(vertID);

        if (glGetShaderi(vertID, GL_COMPILE_STATUS) == GL_FALSE)
        {
            System.err.println("Failed to compile vertex shader! (" + currentVertPath + ")");
            System.err.println(glGetShaderInfoLog(vertID));
            new Exception().printStackTrace();
            return -1;
        }

        glCompileShader(fragID);

        if (glGetShaderi(fragID, GL_COMPILE_STATUS) == GL_FALSE)
        {
            System.err.println("Failed to compile fragment shader! (" + currentFragPath + ")");
            System.err.println(glGetShaderInfoLog(fragID));
            new Exception().printStackTrace();
            return -1;
        }

        glAttachShader(program, vertID);
        glAttachShader(program, fragID);
        glLinkProgram(program);
        glValidateProgram(program);

        glDeleteShader(vertID);
        glDeleteShader(fragID);

        return program;
    }
}
