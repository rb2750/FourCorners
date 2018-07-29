package com.rb2750.lwjgl.graphics;

import com.rb2750.lwjgl.util.BufferUtils;

import lombok.Getter;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class VertexArray
{
    private int vao, vbo, ibo, tbo, nbo;
    @Getter
    private int count;

    public VertexArray(float[] vertices, int vertexSize)
    {
        count = vertices.length / vertexSize;

        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        vbo = bindAttribute(Shader.VERTEX_ATTRIB, vertexSize, vertices);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public VertexArray(float[] vertices, float[] textureCoordinates, int vertexSize)
    {
        count = vertices.length / vertexSize;

        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        vbo = bindAttribute(Shader.VERTEX_ATTRIB, vertexSize, vertices);
        tbo = bindAttribute(Shader.TCOORD_ATTRIB, 2, textureCoordinates);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public VertexArray(float[] vertices, int[] indices, float[] textureCoordinates, float[] normals)
    {
        count = indices.length;

        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        vbo = bindAttribute(Shader.VERTEX_ATTRIB, 3, vertices);
        tbo = bindAttribute(Shader.TCOORD_ATTRIB, 2, textureCoordinates);
        nbo = bindAttribute(Shader.NORMAL_ATTRIB, 3, normals);

        ibo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, BufferUtils.createIntBuffer(indices), GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    private int bindAttribute(int attributeNumber, int size, float[] data)
    {
        int attribID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, attribID);
        glBufferData(GL_ARRAY_BUFFER, BufferUtils.createFloatBuffer(data), GL_STATIC_DRAW);
        glVertexAttribPointer(attributeNumber, size, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(attributeNumber);

        return attribID;
    }

    public void bind()
    {
        glBindVertexArray(vao);

        if (ibo != 0)
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
    }

    public void unbind()
    {
        if (ibo != 0)
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        glBindVertexArray(0);
    }

    public void draw()
    {
        glDrawElements(GL_TRIANGLES, count, GL_UNSIGNED_INT, 0);
    }

    public void render()
    {
        if (vao == 0 || vbo == 0)
            return;

        bind();
        draw();
        unbind();
    }

    public void cleanUp()
    {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);

        if (tbo != 0)
            glDeleteBuffers(tbo);

        if (ibo != 0)
            glDeleteBuffers(ibo);

        vao = vbo = tbo = ibo = 0;
    }

    public int getVAO()
    {
        return vao;
    }
}
