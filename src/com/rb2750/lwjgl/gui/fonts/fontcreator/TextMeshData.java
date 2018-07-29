package com.rb2750.lwjgl.gui.fonts.fontcreator;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class TextMeshData
{
    @Getter
    private float[] vertexPositions;
    @Getter
    private float[] textureCoords;

    public int getVertexCount()
    {
        return vertexPositions.length / 2;
    }
}
