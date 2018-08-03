package com.rb2750.lwjgl.world;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class WorldManager {
    @Getter
    private static List<World> worlds = new ArrayList<>();

    public static void createDefaultWorld() {
        worlds.add(new World(new WorldSettings()));
    }
}
