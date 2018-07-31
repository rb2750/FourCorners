package com.rb2750.lwjgl.input.controllers;

import lombok.Getter;

import java.util.HashMap;

public class Keyboard {
    @Getter
    private HashMap<Integer, Boolean> state = new HashMap<>();
    @Getter
    private HashMap<Integer, Boolean> last = new HashMap<>();

    public boolean isKeyDown(int key) {
        return state.containsKey(key) && state.get(key);
    }

    public boolean wasKeyDown(int key) {
        return last.containsKey(key) && last.get(key);
    }

    public void handleKeyPress(int key) {
        if (state.getOrDefault(key, false)) last.put(key, true);
        state.put(key, true);
    }

    public void handleKeyRelease(int key) {
        last.put(key, false);
        state.put(key, false);
    }
}
