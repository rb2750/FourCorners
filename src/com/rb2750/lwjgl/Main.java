package com.rb2750.lwjgl;

import com.rb2750.lwjgl.Input.Action;
import com.rb2750.lwjgl.Input.Input;
import com.ivan.xinput.XInputButtons;
import com.ivan.xinput.XInputComponents;
import com.ivan.xinput.enums.XInputButton;
import com.ivan.xinput.exceptions.XInputNotLoadedException;
import com.ivan.xinput.listener.SimpleXInputDeviceListener;
import com.ivan.xinput.listener.XInputDeviceListener;
import com.rb2750.lwjgl.animations.FlipAnimation;
import com.rb2750.lwjgl.animations.SquatAnimation;
import com.rb2750.lwjgl.entities.Camera;
import com.rb2750.lwjgl.entities.Entity;
import com.rb2750.lwjgl.entities.Player;
import com.rb2750.lwjgl.entities.Tile;
import com.rb2750.lwjgl.graphics.Shader;
import com.rb2750.lwjgl.gui.GUIManager;
import com.rb2750.lwjgl.gui.SelectionGUI;
import com.rb2750.lwjgl.maths.Matrix4;
import com.rb2750.lwjgl.maths.Vector3;
import com.rb2750.lwjgl.util.Location;
import com.rb2750.lwjgl.util.Size;
import com.rb2750.lwjgl.util.Sync;
import com.rb2750.lwjgl.util.Util;
import com.rb2750.lwjgl.util.XInputState;
import com.rb2750.lwjgl.world.World;
import lombok.Getter;
import org.lwjgl.Version;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import se.albin.steamcontroller.SteamController;
import se.albin.steamcontroller.SteamControllerListener;

import java.nio.IntBuffer;
import java.util.Stack;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import com.ivan.xinput.XInputDevice; // Class for XInput 1.3. Legacy for Win7.
import com.ivan.xinput.XInputDevice14; // Class for XInput 1.4. Includes 1.3 API.

public class Main {
    boolean doubleJump = false;
    // The window handle
    private long window;
    private GLFWVidMode vidmode;
    @Getter
    private static int gameWidth = 1000;
    @Getter
    private static int gameHeight = 1000;
    private Player player;
    private World world = new World();
    private Location cursorLocation = new Location();
    private boolean usingXInput = false;
    private boolean usingXInput14 = false;
    private boolean xInputShowBox = false;
    @Getter
    private GUIManager guiManager = new GUIManager();

    private int currentFPS;
    private long lastFPS;

    /**
     * Rob Notes for Rob
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

        GL.createCapabilities();

//        glMatrixMode(GL_PROJECTION);
//        glLoadIdentity();
//        glOrtho(0, gameWidth, 0, gameHeight, 1, -1);
//        glMatrixMode(GL_MODELVIEW);
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
//
//        glEnable(GL_DEPTH_TEST);
//        glEnable(GL_DEPTH_BUFFER_BIT);
//        glDepthFunc(GL11.GL_LEQUAL);
//        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
//        glClearDepth(1f);

        glEnable(GL_DEPTH_TEST);
        glActiveTexture(GL_TEXTURE1);

        Shader.loadAllShaders();
        //Shader.GENERAL.setUniformMat4f("pr_matrix", Matrix4.orthographic(-10.0f, 10.0f, -10.0f * 9.0f / 16.0f, 10.0f * 9.0f / 16.0f, -1.0f, 1.0f));
        Shader.GENERAL.setUniformMat4f("pr_matrix", Matrix4.orthographic(0, gameWidth, 0, gameHeight, -1, 1));
        //Shader.GENERAL.setUniformMat4f("pr_matrix", Matrix4.projection(gameWidth, gameHeight, 0.1f, 1000.0f, 70.0f));
        Shader.GENERAL.setUniform1i("tex", 1);

        player = new Player(new Location(world, 0, 0));
        world.addEntity(player);
    }

    private Stack<Runnable> toRun = new Stack<>();

    public void runOnUIThread(Runnable runnable) {
        toRun.push(runnable);
    }

    private static long lastFrame;
    @Getter
    private static long deltaTime;

    private void loop() {
        glfwSetWindowSizeCallback(window, new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                gameWidth = width;
                gameHeight = height;
//                glMatrixMode(GL_PROJECTION);
//                glLoadIdentity();
                glViewport(0, 0, width, height);
//                glScissor(0, 0, width, height);
////                double fovY = 1;
////                double zNear = 0.01;
////                double zFar = 100.0;
////
////                double aspect = (double) gameWidth / (double) gameHeight;
////
////                double fH = Math.tan(fovY / 360 * Math.PI) * zNear;
////                double fW = fH * aspect;
////                glFrustum(-fW, fW, -fH, fH, zNear, zFar);
//                glOrtho(0, width, 0, height, 1, -1);
//
//                glMatrixMode(GL_MODELVIEW);
                //Shader.GENERAL.setUniformMat4f("pr_matrix", Matrix4.projection(gameWidth, gameHeight, 0.1f, 1000.0f, 70.0f));
                //Shader.GENERAL.setUniformMat4f("pr_matrix", Matrix4.orthographic(-10.0f, 10.0f, -10.0f * 9.0f / 16.0f, 10.0f * 9.0f / 16.0f, -1.0f, 1.0f));
                Shader.GENERAL.setUniformMat4f("pr_matrix", Matrix4.orthographic(0, gameWidth, 0, gameHeight, 1, -1));
            }
        });

        try
        {
            Input.Setup();

            SteamControllerListener listener = new SteamControllerListener(SteamController.getConnectedControllers().get(0));
            listener.open();
            listener.addSubscriber((state, last) -> {
                handleControls(state, last);
                Input.updateSteamController(state, last);
                runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        guiManager.handleInput(state, last);
                    }
                });
            });
        }
        catch (IndexOutOfBoundsException e)
        {
            e.printStackTrace();
            System.err.println("Failed to find Steam Controller.");
        }

        glfwSetCursorPosCallback(window, new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                cursorLocation.setX((int) xpos);
                cursorLocation.setY(gameHeight - (int) ypos);
            }
        });

        Sync sync = new Sync();
        lastFPS = Util.getTime();

        Camera camera = new Camera();

        while (!glfwWindowShouldClose(window)) {
            deltaTime = Util.getTime() - lastFrame;
            lastFrame = Util.getTime();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            while (!toRun.isEmpty()) toRun.pop().run();
            world.update(player, camera, selectyTile);

            guiManager.update();

            int error = glGetError();
            if (error != GL_NO_ERROR)
                System.err.println("OpenGL Error: " + error);

            glfwSwapBuffers(window);
            glfwPollEvents();

            if (Util.getTime() - lastFPS >= 1000)
            {
                System.out.println("FPS: " + currentFPS);
                currentFPS = 0;
                lastFPS += 1000;
            }

            currentFPS++;

            sync.sync(120);
        }
    }


    public Tile selectyTile;
    public void handleControls(SteamController state, SteamController last) {
        double halfGameWidth = gameWidth / 2;
        double halfGameHeight = gameHeight / 2;

        double tileX = halfGameWidth * state.getRightTouchPosition().x() + halfGameWidth;
        double tileY = halfGameHeight * state.getRightTouchPosition().y() + halfGameHeight;
        if (selectyTile == null) {
            selectyTile = new Tile(new Location(world, Integer.MAX_VALUE, Integer.MAX_VALUE));
            selectyTile.setCanBeInteractedWith(false);
            runOnUIThread(() -> world.addEntity(selectyTile));
        }

        Size size = new Size(100f * Math.max(1 - state.getLeftTrigger(), 0.3), 100f * Math.max(1 - state.getRightTrigger(), 0.3));
        selectyTile.setSize(size);

        if (!Input.ButtonMap.get(Action.ShowBlock).state || state.getRightTouchPosition().x() == 0 && state.getRightTouchPosition().y() == 0)
            selectyTile.move(new Location(world, Integer.MAX_VALUE, Integer.MAX_VALUE), true);
        else
            selectyTile.move(new Location(world, tileX, tileY), true);

        runOnUIThread(() -> {
            if (Input.ButtonMap.get(Action.PlaceBlock).state) {
                Tile newTile = new Tile(new Location(world, tileX, tileY));
                newTile.setSize(size);

                for (Entity entity : world.getEntities())
                    if (entity != selectyTile && entity.getRectangle().intersects(newTile.getRectangle())) return;
                world.addEntity(newTile);
            }

            if (Input.ButtonMap.get(Action.ShowGUI).state) {
                guiManager.displayGUI(new SelectionGUI());
            } else guiManager.hideGUI(SelectionGUI.class);
        });
    }
}