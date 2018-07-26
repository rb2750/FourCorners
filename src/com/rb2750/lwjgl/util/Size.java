package com.rb2750.lwjgl.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class Size implements Cloneable{
    @Getter
    @Setter
    private double width;
    @Getter
    @Setter
    private double height;

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

    public Size multiply(double factor) {
        this.width *= factor;
        this.height *= factor;
        return this;
    }

    public Size clone() {
        try {
            return (Size)super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String toString()
    {
        return "Width: "+width+", Height"+height;
    }
}
