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
    private float x;
    @Accessors(chain = true)
    @Getter
    @Setter
    private float y;

    @Override
    public Location clone() {
        try {
            return (Location) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void add(float x, float y) {
        this.x += x;
        this.y += y;
    }

    public void subtract(float x, float y) {
        this.x -= x;
        this.y -= y;
    }

    public String toString() {
        return "X: " + x + ", " + "Y:" + y;
    }
}
