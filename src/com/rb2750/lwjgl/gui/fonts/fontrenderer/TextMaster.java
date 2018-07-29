package com.rb2750.lwjgl.gui.fonts.fontrenderer;

import com.rb2750.lwjgl.graphics.VertexArray;
import com.rb2750.lwjgl.gui.fonts.fontcreator.FontType;
import com.rb2750.lwjgl.gui.fonts.fontcreator.GUIText;
import com.rb2750.lwjgl.gui.fonts.fontcreator.TextMeshData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextMaster
{
    private static Map<FontType, List<GUIText>> texts = new HashMap<FontType, List<GUIText>>();
    private static FontRenderer renderer;

    public static void init()
    {
        renderer = new FontRenderer();
    }

    public static void render()
    {
        renderer.render(texts);
    }

    public static void loadText(GUIText text)
    {
        FontType font = text.getFont();
        TextMeshData data = font.loadText(text);
        VertexArray mesh = new VertexArray(data.getVertexPositions(), data.getTextureCoords(), 2);
        text.setMeshInfo(mesh, data.getVertexCount());
        List<GUIText> textBatch = texts.get(font);

        if (textBatch == null)
        {
            textBatch = new ArrayList<GUIText>();
            texts.put(font, textBatch);
        }

        textBatch.add(text);
    }

    public static void removeText(GUIText text)
    {
        List<GUIText> textBatch = texts.get(text.getFont());
        textBatch.remove(text);

        if (textBatch.isEmpty())
        {
            texts.remove(text.getFont());
        }
    }

    public static void cleanUp()
    {
        for (FontType font : texts.keySet())
        {
            for (GUIText text : texts.get(font))
            {
                text.getTextMesh().cleanUp();
            }
        }
    }
}
