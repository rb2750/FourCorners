package com.rb2750.lwjgl.entities;

import com.ivan.xinput.enums.XInputButton;
import com.rb2750.lwjgl.Main;
import com.rb2750.lwjgl.animations.StretchAnimation;
import com.rb2750.lwjgl.input.XInputState;
import com.rb2750.lwjgl.util.Location;
import org.joml.Vector4f;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;

public class BouncyTile extends Tile {
    public BouncyTile() {
        super();
        setBaseColour(new Vector4f(255, 0, 255, 255));
    }

    public BouncyTile(Location location) {
        super(location);
    }

    @Override
    public boolean onInteract(Entity x, Entity y) {
        if (y != null) {
            if (y.getAcceleration().y < -7) {
                boolean a = Main.instance.getInputManager().getCurrentControllerState() != null && Main.instance.getInputManager().getCurrentControllerState().isAHeld() || Main.instance.getInputManager().getKeyboard().isKeyDown(GLFW_KEY_SPACE) || XInputState.isButtonDown(XInputButton.A);

                if (y.getAcceleration().y < -25) y.addAnimation(new StretchAnimation());

                float startAcceleration = y.getAcceleration().y;

                if (a && Math.abs(startAcceleration) > 40) {
                    y.getAcceleration().y *= -1;
                } else {
                    y.getAcceleration().y *= -(a ? 1.2 : 0.75);
                    y.getAcceleration().y = Math.min(40, y.getAcceleration().y);
                }
            }
        }
        return true;
    }
}
