package com.rb2750.lwjgl.util;

import com.rb2750.lwjgl.world.World;
import lombok.*;
import lombok.experimental.Accessors;
import org.joml.Vector2f;

@NoArgsConstructor
@AllArgsConstructor
public class Location implements Cloneable {
    @Accessors(chain = true)
    @Getter
    @Setter
    private World world;
    @Accessors(chain = true)
    @Getter
    @Setter
    private float x;
    @Accessors(chain = true)
    @Getter
    @Setter
    private float y;

    public Location(Vector2f vector) {
        this.x = vector.x;
        this.y = vector.y;
    }

    public Location(World world, Vector2f vector) {
        this.world = world;
        this.x = vector.x;
        this.y = vector.y;
    }

    public Vector2f asVector() {
        return new Vector2f(x, y);
    }

    @Override
    public Location clone() {
        try {
            return (Location) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Location set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Location add(float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public Location add(Location loc) {
        this.x += loc.x;
        this.y += loc.y;
        return this;
    }

    public Location subtract(float x, float y) {
        this.x -= x;
        this.y -= y;
        return this;
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
