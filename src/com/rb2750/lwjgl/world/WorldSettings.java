package com.rb2750.lwjgl.world;

import lombok.Getter;
import lombok.Setter;

public class WorldSettings {
    @Getter
    @Setter
    private double gravity = 1.2f;
    @Getter
    @Setter
    private double frictionGround = 0.6f;
    @Getter
    @Setter
    private double frictionAir = 0.1f;
}
