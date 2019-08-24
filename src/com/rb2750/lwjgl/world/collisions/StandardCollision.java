package com.rb2750.lwjgl.world.collisions;

import org.dyn4j.collision.narrowphase.Penetration;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.CollisionAdapter;

public class StandardCollision extends CollisionAdapter {
    private Body body;

    public StandardCollision(Body body) {
        this.body = body;
    }

    @Override
    public boolean collision(Body body1, BodyFixture fixture1, Body body2, BodyFixture fixture2, Penetration penetration) {
        // the bodies can appear in either order
        if (body1 == body || body2 == body) {
            // its the collision we were looking for
            // do whatever you need to do here

            // stopping them like this isn't really recommended
            // there are probably better ways to do what you want

            body1.getLinearVelocity().zero();
            body1.setAngularVelocity(0.0);
            body2.getLinearVelocity().zero();
            body2.setAngularVelocity(0.0);
            return false;
        }
        return true;
    }
}