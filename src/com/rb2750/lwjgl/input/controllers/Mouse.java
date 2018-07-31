package com.rb2750.lwjgl.input.controllers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Mouse {
    @Getter
    private float x;
    @Getter
    private float y;
    @Getter
    private boolean leftMouseDown;
    @Getter
    private boolean middleMouseDown;
    @Getter
    private boolean rightMouseDown;
    @Getter
    private long lastUsed;
    @Getter
    private double scrollDx;
    @Getter
    private double scrollDy;

    public Mouse(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setX(float x) {
        this.x = x;
        lastUsed = System.currentTimeMillis();
    }

    public void setY(float y) {
        this.y = y;
        lastUsed = System.currentTimeMillis();
    }

    public void setLeftMouseDown(boolean leftMouseDown) {
        this.leftMouseDown = leftMouseDown;
        lastUsed = System.currentTimeMillis();
    }

    public void setMiddleMouseDown(boolean middleMouseDown) {
        this.middleMouseDown = middleMouseDown;
        lastUsed = System.currentTimeMillis();
    }

    public void setRightMouseDown(boolean rightMouseDown) {
        this.rightMouseDown = rightMouseDown;
        lastUsed = System.currentTimeMillis();
    }

    public void setScrollDx(double scrollDx) {
        this.scrollDx = scrollDx;
    }

    public void setScrollDy(double scrollDy) {
        this.scrollDy = scrollDy;
    }
}
