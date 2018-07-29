package com.rb2750.lwjgl.gui.fonts.fontcreator;

import lombok.Getter;

import java.io.File;

public class FontType
{
    @Getter
    private int textureAtlas;
    private TextMeshCreator loader;

    public FontType(int textureAtlas, File fontFile)
    {
        this.textureAtlas = textureAtlas;
        this.loader = new TextMeshCreator(fontFile);
    }

    public TextMeshData loadText(GUIText text)
    {
        return loader.createTextMesh(text);
    }
}
