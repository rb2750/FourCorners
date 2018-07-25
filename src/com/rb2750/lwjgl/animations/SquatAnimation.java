package com.rb2750.lwjgl.animations;

import com.rb2750.lwjgl.entities.Entity;
import com.rb2750.lwjgl.util.AnimationFlag;
import com.rb2750.lwjgl.util.Size;

import java.awt.*;

public class SquatAnimation extends Animation {
    private boolean newState;

    public SquatAnimation(boolean newState) {
        super();
        this.newState = newState;
    }

    @Override
    public int getFrames() {
        return 25;
    }

    @Override
    public boolean doAnimation(Entity entity) {
        Size newSize;

        if (newState)
            newSize = new Size(100, Math.max(20, entity.getSize().getHeight() - (80 / getFrames())));
        else
            newSize = new Size(100, Math.min(100, entity.getSize().getHeight() + (80 / getFrames())));

        Rectangle rect = entity.getRectangle();
        rect.setSize((int) newSize.getWidth(), (int) newSize.getHeight());

        for (Entity e : entity.getLocation().getWorld().getEntities())
            if (!e.equals(entity) && e.getRectangle().intersects(rect)) return false;

        if (newState && entity.getSize().getHeight() == 20 || !newState && entity.getSize().getHeight() == 100)
            entity.removeAnimation(this);

        entity.setSize(newSize);
        return true;
    }

    @Override
    public void onComplete(Entity entity) {
        if (newState) {
            entity.setSize(new Size(100, 20));
            entity.setSquat(true);
        } else {
            entity.setSize(new Size(100, 100));
            entity.setSquat(false);
        }
    }

    @Override
    public void onFinish(Entity entity) {
        entity.setSize(new Size(100, 100));
        entity.setSquat(false);
    }

    @Override
    public int getFlags() {
        return AnimationFlag.SIZE;
    }
}
