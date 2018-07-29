package com.rb2750.lwjgl.entities;

import com.rb2750.lwjgl.animations.SquatAnimation;
import com.rb2750.lwjgl.graphics.Shader;
import com.rb2750.lwjgl.input.InputListener;
import com.rb2750.lwjgl.input.InputManager;
import com.rb2750.lwjgl.input.controllers.*;
import com.rb2750.lwjgl.util.Location;
import com.rb2750.lwjgl.util.Size;
import static org.lwjgl.glfw.GLFW.*;

public class Player extends Entity implements InputListener {
    private float speed = 8f;
    private boolean jumping = false;
    private boolean doubleJump = false;

    public Player(Location location) {
        super(location, new Size(100, 100), Shader.GENERAL);
        setGravity(true);

        InputManager.registerInputListener(this);

        vertices = new float[]{
                0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 1.0f, 0.0f,

                0.0f, 1.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 1.0f,
                1.0f, 1.0f, 1.0f,

                1.0f, 1.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 1.0f,
                1.0f, 1.0f, 1.0f,

                0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 1.0f,

                0.0f, 1.0f, 1.0f,
                0.0f, 1.0f, 0.0f,
                1.0f, 1.0f, 0.0f,
                1.0f, 1.0f, 1.0f,

                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 1.0f

        };

        indices = new int[]{
                0, 1, 3,
                3, 1, 2,
                4, 5, 7,
                7, 5, 6,
                8, 9, 11,
                11, 9, 10,
                12, 13, 15,
                15, 13, 14,
                16, 17, 19,
                19, 17, 18,
                20, 21, 23,
                23, 21, 22

        };

        tcs = new float[]{

                0, 0,
                0, 1,
                1, 1,
                1, 0,
                0, 0,
                0, 1,
                1, 1,
                1, 0,
                0, 0,
                0, 1,
                1, 1,
                1, 0,
                0, 0,
                0, 1,
                1, 1,
                1, 0,
                0, 0,
                0, 1,
                1, 1,
                1, 0,
                0, 0,
                0, 1,
                1, 1,
                1, 0
        };


        normals = new float[]{
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,

                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,

                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,

                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,

                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,

                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f
        };

        layer = -45.0f;
        texturePath = "res/textures/blue.png";

        createMesh();
//        createMesh("res/models/Base Mesh sculpt 2.obj");
    }

//    @Override
//    public void renderEntity(Camera camera) {
//        drawCube(getLocation().getX(), getLocation().getY(), 0.5f, getSize().getWidth(), getSize().getHeight(), getSize().getWidth());
//    }


    @Override
    public void update() {
        super.update();

        if (onGround()) doubleJump = false;
    }

    @Override
    public void handleControllerInput(Controller state, Controller last) {
        if (state.isAHeld()) {
            if (!doubleJump && jumping && !onGround() && !last.isAHeld()) {
                doubleJump = true;
                jumping = false;
            } else if (!onGround()) return;
            else jumping = true;
            getAcceleration().setY(21);
        }

        getAcceleration().setX(speed * state.getAnalogStick().x());

        if (state.isHomeHeld()) rotate(2);

        if (!animationExists(SquatAnimation.class)) {
            if (state.isXHeld() && !last.isXHeld()) addAnimation(new SquatAnimation());
        } else {
            if (state.isXHeld()) getAnimation(SquatAnimation.class).Pause();
            else getAnimation(SquatAnimation.class).Unpause();
        }
    }

    @Override
    public void handleKeyboardInput(Keyboard keyboard) {
        if (keyboard.isKeyDown(GLFW_KEY_SPACE)) {
            if (!doubleJump && jumping && !onGround() && !keyboard.wasKeyDown(GLFW_KEY_SPACE)) {
                doubleJump = true;
                jumping = false;
            } else if (!onGround()) return;
            else jumping = true;
            getAcceleration().setY(21);
        }

        getAcceleration().setX(keyboard.isKeyDown(GLFW_KEY_RIGHT) ? speed : keyboard.isKeyDown(GLFW_KEY_LEFT) ? -speed : 0);

        if (!animationExists(SquatAnimation.class)) {
            if (keyboard.isKeyDown(GLFW_KEY_LEFT_SHIFT) && !keyboard.wasKeyDown(GLFW_KEY_LEFT_SHIFT))
                addAnimation(new SquatAnimation());
        } else {
            if (keyboard.isKeyDown(GLFW_KEY_LEFT_SHIFT)) getAnimation(SquatAnimation.class).Pause();
            else getAnimation(SquatAnimation.class).Unpause();
        }
    }

    @Override
    public void handleMouseInput(Mouse mouse) {

    }
}
