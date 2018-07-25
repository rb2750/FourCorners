package com.rb2750.lwjgl;

import com.rb2750.lwjgl.animations.FlipAnimation;
import com.rb2750.lwjgl.animations.SquatAnimation;
import com.rb2750.lwjgl.entities.Entity;
import com.rb2750.lwjgl.entities.Player;
import com.rb2750.lwjgl.entities.Tile;
import com.rb2750.lwjgl.util.Location;
import com.rb2750.lwjgl.util.Size;
import com.rb2750.lwjgl.world.World;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import se.albin.steamcontroller.SteamController;
import se.albin.steamcontroller.SteamControllerListener;

import java.nio.IntBuffer;
import java.util.Stack;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Main {
    boolean doubleJump = false;
    // The window handle
    private long window;
    private GLFWVidMode vidmode;
    private int gameWidth = 1000;
    private int gameHeight = 1000;
    private Player player;
    private World world = new World();
    private Location cursorLocation = new Location();

    /**
     * Rob Notes
     * I3 Commands:
     * Mod+Control+Shift+Equals = PC Mode
     * Mod+Control+Equals = Game Mode
     * Mod+Control+Minus = Stop all (In case the controller says it's in use)
     */

    public static void main(String[] args) {
        new Main().run();
    }

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        // Create the window
        window = glfwCreateWindow(gameWidth, gameHeight, "LWJGL Test", NULL, NULL);

        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");
//        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
//            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
//                glfwSetWindowShouldClose(window, true);
//        });

        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            glfwGetWindowSize(window, pWidth, pHeight);
            // Get the resolution of the primary monitor
            vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        }
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);
        // Make the window visible
        glfwShowWindow(window);

        player = new Player(new Location(world, 0, 0));
        world.addEntity(player);
    }

    private SteamController currentState;
    private SteamController lastState;

    private Stack<Runnable> toRun = new Stack<>();

    public void runOnUIThread(Runnable runnable) {
        toRun.push(runnable);
    }

    private void loop() {
        GL.createCapabilities();

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, gameWidth, 0, gameHeight, 1, -1);
        glMatrixMode(GL_MODELVIEW);
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_DEPTH_BUFFER_BIT);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
        glClearDepth(1f);

        glfwSetWindowSizeCallback(window, new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                gameWidth = width;
                gameHeight = height;
                glMatrixMode(GL_PROJECTION);
                glLoadIdentity();
                glViewport(0, 0, width, height);
                glScissor(0, 0, width, height);
//                double fovY = 1;
//                double zNear = 0.01;
//                double zFar = 100.0;
//
//                double aspect = (double) gameWidth / (double) gameHeight;
//
//                double fH = Math.tan(fovY / 360 * Math.PI) * zNear;
//                double fW = fH * aspect;
//                glFrustum(-fW, fW, -fH, fH, zNear, zFar);
                glOrtho(0, width, 0, height, 1, -1);

//                glFrustum(0, width, 0, height, 0.00100f, 100f);
                glMatrixMode(GL_MODELVIEW);
            }
        });
        SteamControllerListener listener = new SteamControllerListener(SteamController.getConnectedControllers().get(0));
        listener.open();
        listener.addSubscriber((state, last) -> {
            currentState = state;
            lastState = last;
            handleControls(currentState, lastState);
//            if (state.isAHeld()/* && !last.isAHeld()*/)
//                System.out.println("A button pressed!");
//            if (state.isBHeld() && !last.isBHeld())
//                System.out.println("B button pressed!");
        });

        glfwSetCursorPosCallback(window, new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                cursorLocation.setX((int) xpos);
                cursorLocation.setY(gameHeight - (int) ypos);
            }
        });

        while (!glfwWindowShouldClose(window)) {
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

//            if (currentState != null) handleControls(currentState, lastState);

            while (!toRun.isEmpty()) toRun.pop().run();
            world.update();

//            glLoadIdentity();
//            GL11.glTranslatef(0f, 0.0f, -10f);
//            GL11.glRotatef(45f, 1.0f, 1.0f, 0.0f);
//            GL11.glColor3f(0.5f, 0.5f, 1.0f);
//
//            Util.drawCube(0, 0, 0, 1, 1, 1);

            glfwSwapBuffers(window);
            glfwPollEvents();
//            if ((System.currentTimeMillis() - start) > 2) System.out.println((System.currentTimeMillis() - start) + "");
        }
    }

    Tile selectyTile;

    public void handleControls(SteamController state, SteamController last) {
        int speed = 8;

        if (state.isLGHeld()) speed *= 2;

        player.getAcceleration().setX((int) (speed * state.getAnalogStickPosition().x()));

        if (state.isAHeld() && !last.isAHeld()) {
            if (!doubleJump && !player.onGround()/* && player.getAcceleration().getY() < 0*/) {
                doubleJump = true;
                player.addAnimation(new FlipAnimation());
            } else if (!player.onGround()) return;

            player.getAcceleration().setY(21);
        }

        if (state.isBHeld() && !last.isBHeld()) {
            world.getEntities().clear();

            world.addEntity(player);
            world.addEntity(selectyTile);
        }

        if (player.onGround()) doubleJump = false;

        double halfGameWidth = gameWidth / 2;
        double halfGameHeight = gameHeight / 2;

        /**
         * X = halfGameWidth * controller.x + halfGameWidth
         */

        double tileX = halfGameWidth * state.getRightTouchPosition().x() + halfGameWidth;
        double tileY = halfGameHeight * state.getRightTouchPosition().y() + halfGameHeight;
        if (selectyTile == null) {
            selectyTile = new Tile(new Location(world, Integer.MAX_VALUE, Integer.MAX_VALUE));
            selectyTile.setCanBeInteractedWith(false);
            runOnUIThread(() -> world.addEntity(selectyTile));
        }
        boolean wasSquatting = player.isSquat();
        if (player.animationExists(SquatAnimation.class)) {
            if (!player.isSquat() && !state.isXHeld()) {
                player.removeAnimation(SquatAnimation.class);
                wasSquatting = true;
            }
        }
        if (!player.animationExists(SquatAnimation.class)) {
            if (state.isXHeld() && !wasSquatting) {
                player.addAnimation(new SquatAnimation(true));
            } else if (!state.isXHeld() && wasSquatting) {
                player.addAnimation(new SquatAnimation(false));
            }
        }
        selectyTile.setSize(new Size(100f * (float) (1 - state.getLeftTrigger()), 100f * (float) (1 - state.getRightTrigger())));
        selectyTile.teleport(new Location(world, (int) tileX, (int) tileY));
        if (!state.isRightPadTouched()) selectyTile.teleport(new Location(world, Integer.MAX_VALUE, Integer.MAX_VALUE));
        runOnUIThread(() -> {
            if (state.isRightPadPressed()/* && !last.isLeftPadPressed()*/) {
                Tile newTile = new Tile(new Location(world, (int) tileX, (int) tileY));

                for (Entity entity : world.getEntities())
                    if (entity != selectyTile && entity.getRectangle().intersects(newTile.getRectangle())) return;

                newTile.setSize(new Size(100f * (float) (1 - state.getLeftTrigger()), 100f * (float) (1 - state.getRightTrigger())));
                world.addEntity(newTile);
            }
        });
    }
}