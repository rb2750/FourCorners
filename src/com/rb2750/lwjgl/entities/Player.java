package com.rb2750.lwjgl.entities;

import com.rb2750.lwjgl.animations.FlipAnimation;
import com.rb2750.lwjgl.animations.SquashAnimation;
import com.rb2750.lwjgl.animations.SquatAnimation;
import com.rb2750.lwjgl.graphics.Shader;
import com.rb2750.lwjgl.input.InputListener;
import com.rb2750.lwjgl.input.InputManager;
import com.rb2750.lwjgl.input.controllers.Controller;
import com.rb2750.lwjgl.input.controllers.Keyboard;
import com.rb2750.lwjgl.input.controllers.Mouse;
import com.rb2750.lwjgl.util.Location;
import com.rb2750.lwjgl.util.Size;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.*;

public class Player extends Entity implements InputListener {
    float speed = 12f;
    boolean jumping = false;
    boolean doubleJump = false;

    public Player(Location location) {
        super(location, new Size(100, 100), Shader.BASIC_TEX, new Vector4f(0.0f, 0.0f, 255.0f, 255.0f));
        gravity = true;

        InputManager.registerInputListener(this);

        vertices = new float[]{
                0f, 1f, 0f,
                0f, 0f, 0f,
                1f, 0f, 0f,
                1f, 1f, 0f,

                0f, 1f, 1f,
                0f, 0f, 1f,
                1f, 0f, 1f,
                1f, 1f, 1f,

                1f, 1f, 0f,
                1f, 0f, 0f,
                1f, 0f, 1f,
                1f, 1f, 1f,

                0f, 1f, 0f,
                0f, 0f, 0f,
                0f, 0f, 1f,
                0f, 1f, 1f,

                0f, 1f, 1f,
                0f, 1f, 0f,
                1f, 1f, 0f,
                1f, 1f, 1f,

                0f, 0f, 1f,
                0f, 0f, 0f,
                1f, 0f, 0f,
                1f, 0f, 1f

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


        normals = calcNormals();

        layer = 0.0f;
        texturePath = "res/textures/blue.png";

        createMesh();
//        createMesh("res/models/newplayer.obj");
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
        acceleration.x = (float) (speed * state.getAnalogStick().x());

        if (!animationExists(SquatAnimation.class)) {
            if (state.isXHeld() && !last.isXHeld()) addAnimation(new SquatAnimation());
        } else {
            if (state.isXHeld()) getAnimation(SquatAnimation.class).Pause();
            else getAnimation(SquatAnimation.class).Unpause();
        }

        //Let's assume the squash animation 'sticks them to the ground' here.
        //Let's also stop them from jumping while squatting because hey, that's pretty stupid.
        if (state.isAHeld() && !animationExists(SquashAnimation.class) && !animationExists(SquatAnimation.class) && (acceleration.y >= 0 || !doubleJump) && acceleration.y <= 21) {
            if (!doubleJump && jumping && !onGround() && !last.isAHeld()) {
                doubleJump = true;
                jumping = false;
                addAnimation(new FlipAnimation());
            } else if (!onGround()) return;
            else jumping = true;
            acceleration.y = 21;
        }
    }

    @Override
    public void handleKeyboardInput(Keyboard keyboard) {
        acceleration.x = keyboard.isKeyDown(GLFW_KEY_RIGHT) || keyboard.isKeyDown(GLFW_KEY_D) ? speed : keyboard.isKeyDown(GLFW_KEY_LEFT) || keyboard.isKeyDown(GLFW_KEY_A) ? -speed : 0;

        if (!animationExists(SquatAnimation.class)) {
            if (keyboard.isKeyDown(GLFW_KEY_LEFT_SHIFT) && !keyboard.wasKeyDown(GLFW_KEY_LEFT_SHIFT))
                addAnimation(new SquatAnimation());
        } else {
            if (keyboard.isKeyDown(GLFW_KEY_LEFT_SHIFT)) getAnimation(SquatAnimation.class).Pause();
            else getAnimation(SquatAnimation.class).Unpause();
        }

        //Let's assume the squash animation 'sticks them to the ground' here.
        //Let's also stop them from jumping while squatting because hey, that's pretty stupid.
        if (keyboard.isKeyDown(GLFW_KEY_SPACE) && !animationExists(SquashAnimation.class) && !animationExists(SquatAnimation.class) && acceleration.y >= 0 && acceleration.y <= 21) {
            if (!doubleJump && jumping && !onGround() && !keyboard.wasKeyDown(GLFW_KEY_SPACE)) {
                doubleJump = true;
                jumping = false;
                addAnimation(new FlipAnimation());
            } else if (!onGround()) return;
            else jumping = true;
            acceleration.y = 21;
        }
    }

    @Override
    public void handleMouseInput(Mouse mouse) {

    }
}
