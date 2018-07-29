package com.rb2750.lwjgl.gui.fonts.fontrenderer;

import com.rb2750.lwjgl.graphics.Shader;
import com.rb2750.lwjgl.gui.fonts.fontcreator.FontType;
import com.rb2750.lwjgl.gui.fonts.fontcreator.GUIText;

import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;

public class FontRenderer
{
    public FontRenderer()
    {
    }

    public void render(Map<FontType, List<GUIText>> texts)
    {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_DEPTH_TEST);
        Shader.FONT.enable();

        glActiveTexture(GL_TEXTURE0);

        for (FontType font : texts.keySet())
        {
            glBindTexture(GL_TEXTURE_2D, font.getTextureAtlas());

            for (GUIText text : texts.get(font))
            {
                renderText(text);
            }
        }

        glActiveTexture(GL_TEXTURE1);

        Shader.FONT.disable();
        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
    }

    private void renderText(GUIText text)
    {
        text.getTextMesh().bind();
        Shader.FONT.setUniform3f("colour", text.getColour());
        Shader.FONT.setUniform2f("translation", text.getPosition());
        glDrawArrays(GL_TRIANGLES, 0, text.getVertexCount());
        text.getTextMesh().unbind();
    }
}
