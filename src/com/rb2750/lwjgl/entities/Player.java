package com.rb2750.lwjgl.entities;

import com.rb2750.lwjgl.graphics.Shader;
import com.rb2750.lwjgl.Input.Action;
import com.rb2750.lwjgl.Input.Button;
import com.rb2750.lwjgl.Input.Input;
import com.rb2750.lwjgl.animations.SquatAnimation;
import com.rb2750.lwjgl.graphics.Texture;
import com.rb2750.lwjgl.graphics.VertexArray;
import com.rb2750.lwjgl.util.Location;
import com.rb2750.lwjgl.util.Size;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.system.CallbackI;

import static com.rb2750.lwjgl.util.Util.drawCube;

public class Player extends Entity {
    float speed = 8f;
    boolean jumping = false;
    boolean doubleJump = false;

    public Player(Location location) {
        super(location, new Size(100, 100), Shader.GENERAL);
        setGravity(true);

        float[] vertices = new float[] {
                -1.0f, -1.0f, 0.0f,
                -1.0f,  1.0f, 0.0f,
                1.0f,  1.0f, 0.0f,
                1.0f, -1.0f, 0.0f
        };

        byte[] indices = new byte[] {
                0, 1, 2,
                2, 3, 0
        };

        float[] tcs = new float[] {
                0, 1,
                0, 0,
                1, 0,
                1, 1
        };

        mesh = new VertexArray(vertices, indices, tcs);

        texture = new Texture("res/textures/blue.png");
    }

    @Override
    public void renderEntity(Camera camera) {
        drawCube(getLocation().getX(), getLocation().getY(), 0.5f, getSize().getWidth(), getSize().getHeight(), getSize().getWidth());
    }

    @Override
    public void update(Camera camera) {
        super.update(camera);


        if(Input.ButtonMap.get(Action.Jump).state) {
            if (!doubleJump && jumping && !onGround() && !Input.ButtonMap.get(Action.Jump).last /* && player.getAcceleration().getY() < 0*/) {
                doubleJump = true;
                jumping = false;
            } else if (!onGround()) return;
            else jumping = true;
            getAcceleration().setY(21);
        }

        if (onGround()) doubleJump = false;

        getAcceleration().setX((speed * Input.Analog_Stick.getX()));

        Button squat = Input.ButtonMap.get(Action.Squat);

        if (!animationExists(SquatAnimation.class)) {
            if (squat.state && !squat.last) addAnimation(new SquatAnimation());
        } else {
            if (!squat.state ) getAnimation(SquatAnimation.class).Pause();
            else getAnimation(SquatAnimation.class).Unpause();
        }

        if (Input.ButtonMap.get(Action.Home).state) {
            setSize(new Size(getSize().getWidth() + 1, getSize().getHeight()));
        }

    }
}
