package com.rb2750.lwjgl.graphics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class Attenution
{
    @Getter
    @Setter
    private float constant;
    @Getter
    @Setter
    private float linear;
    @Getter
    @Setter
    private float exponent;
}
