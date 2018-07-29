package com.rb2750.lwjgl.gui.fonts.fontcreator;

import lombok.Getter;

public class Character
{
    @Getter
    private int id;
    @Getter
    private double xTexCoord;
    @Getter
    private double yTexCoord;
    @Getter
    private double xMaxTexCoord;
    @Getter
    private double yMaxTexCoord;
    @Getter
    private double xOffset;
    @Getter
    private double yOffset;
    @Getter
    private double sizeX;
    @Getter
    private double sizeY;
    @Getter
    private double xAdvance;

    protected Character(int id, double xTexCoord, double yTexCoord, double xTexSize, double yTexSize, double xOffset,
                        double yOffset, double sizeX, double sizeY, double xAdvance)
    {
        this.id = id;
        this.xTexCoord = xTexCoord;
        this.yTexCoord = yTexCoord;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.xMaxTexCoord = xTexSize + xTexCoord;
        this.yMaxTexCoord = yTexSize + yTexCoord;
        this.xAdvance = xAdvance;
    }
}
