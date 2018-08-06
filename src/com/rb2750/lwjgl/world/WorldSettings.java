package com.rb2750.lwjgl.world;

import lombok.Getter;
import lombok.Setter;

public class WorldSettings {
    @Getter
    @Setter
    private float gravity = 1.2f;
    @Getter
    @Setter
    private float frictionGround = 0.6f;
    @Getter
    @Setter
    private float frictionAir = 0.1f;
}
