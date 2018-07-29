package com.rb2750.lwjgl.gui.fonts.fontcreator;

import com.rb2750.lwjgl.graphics.VertexArray;
import com.rb2750.lwjgl.gui.fonts.fontrenderer.TextMaster;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class GUIText
{
    private String textString;
    private float fontSize;

    @Getter
    private VertexArray textMesh;
    @Getter
    private int vertexCount;
    @Getter
    @Setter
    private Vector3f colour = new Vector3f(0.0f, 0.0f, 0.0f);

    @Getter
    private Vector2f position;
    private float lineMaxSize;
    @Getter
    private int numberOfLines;

    @Getter
    private FontType font;

    private boolean centreText = false;

    public GUIText(String text, float fontSize, FontType font, Vector2f position, float maxLineLength, boolean centred)
    {
        this.textString = text;
        this.fontSize = fontSize;
        this.font = font;
        this.position = position;
        this.lineMaxSize = maxLineLength;
        this.centreText = centred;
        TextMaster.loadText(this);
    }

    public void remove()
    {
        TextMaster.removeText(this);
    }

    public void setColour(float r, float g, float b)
    {
        colour.set(r, g, b);
    }

    public void setMeshInfo(VertexArray mesh, int vertexCount)
    {
        this.textMesh = mesh;
        this.vertexCount = vertexCount;
    }

    protected float getFontSize()
    {
        return fontSize;
    }

    protected void setNumberOfLines(int number)
    {
        this.numberOfLines = number;
    }

    protected boolean isCentred()
    {
        return centreText;
    }

    protected float getMaxLineSize()
    {
        return lineMaxSize;
    }

    protected String getTextString()
    {
        return textString;
    }
}
