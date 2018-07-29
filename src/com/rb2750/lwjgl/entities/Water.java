package com.rb2750.lwjgl.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Water
{
    public static final float TILE_SIZE = 60.0f;

    @Getter
    private float height;
    @Getter
    private float x, z;
}
