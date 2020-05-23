package com.rb2750.lwjgl.util;

import com.rb2750.lwjgl.serialization.SerialObject;
import lombok.Getter;

public class Size implements Cloneable {
    @Getter
    public float width;
    @Getter
    public float height;
    @Getter
    public float depth;

    public Size(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public Size(float width, float height, float depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    public Size add(Size size) {
        this.width += size.width;
        this.height += size.height;
        return this;
    }

    public Size subtract(Size size) {
        this.width -= size.width;
        this.height -= size.height;
        return this;
    }

    public Size multiply(float factor) {
        this.width *= factor;
        this.height *= factor;
        return this;
    }

    public Size clone() {
        try {
            return (Size) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String toString() {
        return "Width: " + width + ", Height" + height;
    }
}
