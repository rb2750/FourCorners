package com.rb2750.lwjgl.input.controllers;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
public final class Analog2D {
    @Setter
    private double x;
    @Setter
    private double y;

    public Analog2D(se.albin.steamcontroller.Analog2D old) {
        this.x = old.x();
        this.y = old.y();
    }

    public boolean isNeutral() {
        return x == 0 && y == 0;
    }

    public double x() {
        return this.x;
    }

    public double y() {
        return this.y;
    }
}
