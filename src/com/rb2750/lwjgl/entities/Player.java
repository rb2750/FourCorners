package com.rb2750.lwjgl.entities;

import com.rb2750.lwjgl.input.*;
import com.rb2750.lwjgl.animations.SquatAnimation;
import com.rb2750.lwjgl.graphics.*;
import com.rb2750.lwjgl.util.Location;
import com.rb2750.lwjgl.util.Size;

public class Player extends Entity {
    float speed = 8f;
    boolean jumping = false;
    boolean doubleJump = false;

    public Player(Location location) {
        super(location, new Size(10, 10), Shader.GENERAL);
        setGravity(true);

        vertices = new float[]{
                -0.5f,0.5f,-0.5f,
                -0.5f,-0.5f,-0.5f,
                0.5f,-0.5f,-0.5f,
                0.5f,0.5f,-0.5f,

                -0.5f,0.5f,0.5f,
                -0.5f,-0.5f,0.5f,
                0.5f,-0.5f,0.5f,
                0.5f,0.5f,0.5f,

                0.5f,0.5f,-0.5f,
                0.5f,-0.5f,-0.5f,
                0.5f,-0.5f,0.5f,
                0.5f,0.5f,0.5f,

                -0.5f,0.5f,-0.5f,
                -0.5f,-0.5f,-0.5f,
                -0.5f,-0.5f,0.5f,
                -0.5f,0.5f,0.5f,

                -0.5f,0.5f,0.5f,
                -0.5f,0.5f,-0.5f,
                0.5f,0.5f,-0.5f,
                0.5f,0.5f,0.5f,

                -0.5f,-0.5f,0.5f,
                -0.5f,-0.5f,-0.5f,
                0.5f,-0.5f,-0.5f,
                0.5f,-0.5f,0.5f

        };

        indices = new int[]{
                0,1,3,
                3,1,2,
                4,5,7,
                7,5,6,
                8,9,11,
                11,9,10,
                12,13,15,
                15,13,14,
                16,17,19,
                19,17,18,
                20,21,23,
                23,21,22

        };

        tcs = new float[]{

                0,0,
                0,1,
                1,1,
                1,0,
                0,0,
                0,1,
                1,1,
                1,0,
                0,0,
                0,1,
                1,1,
                1,0,
                0,0,
                0,1,
                1,1,
                1,0,
                0,0,
                0,1,
                1,1,
                1,0,
                0,0,
                0,1,
                1,1,
                1,0


        };

        layer = -45.0f;
        texturePath = "res/textures/blue.png";

        //createMesh();
        createMesh("res/models/Base Mesh sculpt 2.obj");
    }

//    @Override
//    public void renderEntity(Camera camera) {
//        drawCube(getLocation().getX(), getLocation().getY(), 0.5f, getSize().getWidth(), getSize().getHeight(), getSize().getWidth());
//    }

    @Override
    public void update() {
        super.update();

        if (Input.ButtonMap.get(Action.Jump).state) {
            if (!doubleJump && jumping && !onGround() && !Input.ButtonMap.get(Action.Jump).last /* && player.getAcceleration().getY() < 0*/) {
                doubleJump = true;
                jumping = false;
            } else if (!onGround()) return;
            else jumping = true;
            getAcceleration().setY(21);
        }

        if (onGround()) doubleJump = false;

        getAcceleration().setX((speed * Input.Left_Analog_Stick.getX()));

        Button squat = Input.ButtonMap.get(Action.Squat);

        if (!animationExists(SquatAnimation.class)) {
            if (squat.state && !squat.last) addAnimation(new SquatAnimation());
        } else {
            if (squat.state) getAnimation(SquatAnimation.class).Pause();
            else getAnimation(SquatAnimation.class).Unpause();
        }

        if (Input.ButtonMap.get(Action.Home).state) {
            setSize(new Size(getSize().getWidth() + 1, getSize().getHeight()));
        }

    }
}
