package com.rb2750.lwjgl.gui;

import com.rb2750.lwjgl.graphics.Shader;
import com.rb2750.lwjgl.graphics.VertexArray;
import com.rb2750.lwjgl.maths.MatrixUtil;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;

public class GUIRenderer
{
    private final VertexArray mesh;

    public GUIRenderer()
    {
        float[] positions = { -1, 1, -1, -1, 1, 1, 1, -1 };
        mesh = new VertexArray(positions, 2);
    }

    public void render(List<GUITexture> guis)
    {
        Shader.GUI.enable();
        mesh.bind();

        glActiveTexture(GL_TEXTURE0);

        for (GUITexture gui : guis)
        {
            glBindTexture(GL_TEXTURE_2D, gui.getTexture());
            Shader.GUI.setUniformMat4f("ml_matrix", MatrixUtil.transformation(gui.getPosition(), 0, 0, 0, gui.getScale()));
            glDrawArrays(GL_TRIANGLE_STRIP, 0, mesh.getCount());
        }

        glActiveTexture(GL_TEXTURE1);

        mesh.unbind();
        Shader.GUI.disable();
    }

    public void cleanUp()
    {
        mesh.cleanUp();
    }
}
