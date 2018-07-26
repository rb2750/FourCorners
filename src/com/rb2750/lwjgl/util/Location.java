package com.rb2750.lwjgl.util;

import com.rb2750.lwjgl.world.World;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@NoArgsConstructor
@AllArgsConstructor
public class Location implements Cloneable {
    @Getter
    private World world;
    @Accessors(chain = true)
    @Getter
    @Setter
    private double x;
    @Accessors(chain = true)
    @Getter
    @Setter
    private double y;

    @Override
    public Location clone() {
        try {
            return (Location) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Location add(double x, double y) {
        this.x += x;
        this.y += y;
        return this;
    }
    public Location add(Location loc) {
        this.x += loc.x;
        this.y += loc.y;
        return this;
    }

    public void subtract(double x, double y) {
        this.x -= x;
        this.y -= y;
    }

    public String toString() {
        return "X: " + x + ", " + "Y:" + y;
    }
    public Location subtract(Location loc) {
        this.x -= loc.x;
        this.y -= loc.y;
        return this;
    }

    public Location multiply(float factor) {
        this.x *= factor;
        this.y *= factor;
        return this;
    }
}
