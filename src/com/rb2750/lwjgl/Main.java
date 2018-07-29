package com.rb2750.lwjgl;

import com.ivan.xinput.*;
import com.ivan.xinput.enums.XInputButton;
import com.ivan.xinput.exceptions.XInputNotLoadedException;
import com.ivan.xinput.listener.SimpleXInputDeviceListener;
import com.ivan.xinput.listener.XInputDeviceListener;
import com.rb2750.lwjgl.entities.*;
import com.rb2750.lwjgl.graphics.*;
import com.rb2750.lwjgl.gui.*;
import com.rb2750.lwjgl.gui.fonts.fontrenderer.TextMaster;
import com.rb2750.lwjgl.input.*;
import com.rb2750.lwjgl.input.controllers.*;
import com.rb2750.lwjgl.util.*;
import com.rb2750.lwjgl.world.World;
import lombok.Getter;
import org.joml.*;
import org.lwjgl.Version;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.GL_CLIP_DISTANCE0;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.IntBuffer;
import java.util.*;

public class Main implements InputListener {
    public static Main instance;

    private static final float ORTHO_NEAR_PLANE = -1.0f;
    private static final float ORTHO_FAR_PLANE = 1.0f;
    private static final float PERSP_NEAR_PLANE = 0.1f;
    private static final float PERSP_FAR_PLANE = 1000.0f;

    // The window handle
    public long window;
    @Getter
    private static int gameWidth = 1000;
    @Getter
    private static int gameHeight = 1000;
    private Player player;
    private World world = new World();
    private Location cursorLocation = new Location();

    //input
    private boolean usingXInput = false;
    private boolean usingXInput14 = false;

    private InputManager inputManager = new InputManager();
    @Getter
    private GUIManager guiManager = new GUIManager();

    private int currentFPS;

    private Matrix4f currentProjMatrix;

    private Light light;

    public static void main(String[] args) {
        new Main().run();
    }

    public void run() {
        instance = this;
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
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        // Create the window
        window = glfwCreateWindow(gameWidth, gameHeight, "LWJGL Test", NULL, NULL);

        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            glfwGetWindowSize(window, pWidth, pHeight);
            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

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
        GLUtil.setupDebugMessageCallback();

        glViewport(0, 0, gameWidth, gameHeight);

        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
//        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        glEnable(GL_DEPTH_TEST);
        glActiveTexture(GL_TEXTURE1);

        Shader.loadAllShaders();
        //Shader.GENERAL.setUniformMat4f("pr_matrix", new Matrix4f().ortho(0, gameWidth, 0, gameHeight, -1, 1));
        System.out.println("AR: " + ((float) gameWidth / (float) gameHeight));
        currentProjMatrix = new Matrix4f().perspective(70.0f, (float) gameWidth / (float) gameHeight, PERSP_NEAR_PLANE, PERSP_FAR_PLANE);
        Shader.GENERAL.setUniformMat4f("pr_matrix", currentProjMatrix);
        Shader.GENERAL.disable();
        Shader.WATER.setUniform1f("nearPlane", PERSP_NEAR_PLANE);
        Shader.WATER.setUniform1f("farPlane", PERSP_FAR_PLANE);
//        Shader.WATER.setUniform1f("nearPlane", ORTHO_NEAR_PLANE);
//        Shader.WATER.setUniform1f("nearPlane", ORTHO_FAR_PLANE);
        Shader.WATER.disable();
        //Shader.GENERAL.setUniformMat4f("pr_matrix", MatrixUtil.projection(gameWidth, gameHeight, 0.1f, 1000.0f, 70.0f));
        Shader.GENERAL.setUniform1i("tex", 1);

        light = new Light(new Vector3f(80, 10, -30), new Vector3f(1, 1, 1));
        Shader.GENERAL.setUniform3f("lightPosition", light.getPosition());
        Shader.GENERAL.setUniform3f("lightColour", light.getColour());

        System.out.println("OpenGL version: " + glGetString(GL_VERSION));

        player = new Player(new Location(world, 0, 0));
        world.addEntity(player);

        InputManager.registerInputListener(this);
        inputManager.Setup();

        TextMaster.init();

//        FontType font = new FontType(new Texture("res/fonts/calibriHR.png").getTexture(), new File("res/fonts/calibriHR.fnt"));
//        GUIText text = new GUIText("The quick brown fox jumps over the lazy dog.", 2, font, new Vector2f(0.0f, 0.0f), 1f, true);
//        text.setColour(1, 1, 0);

        System.out.println("OpenGL version: " + glGetString(GL_VERSION));
    }

    private Stack<Runnable> toRun = new Stack<>();

    public void runOnUIThread(Runnable runnable) {
        toRun.push(runnable);
    }

    private static long lastFrame;
    @Getter
    private static float deltaTime;

    private void loop() {
        glfwSetWindowSizeCallback(window, new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                System.out.println("RESIZE");
                gameWidth = width;
                gameHeight = height;
                glViewport(0, 0, width, height);
//                Shader.GENERAL.setUniformMat4f("pr_matrix", new Matrix4f().ortho(0, gameWidth, 0, gameHeight, -1, 1));
//                System.out.println("AR: " + ((float) gameWidth / (float) gameHeight));
////                currentProjMatrix = new Matrix4f().perspective(70.0f, (float) gameWidth / (float) gameHeight, PERSP_NEAR_PLANE, PERSP_FAR_PLANE);
//                Shader.GENERAL.setUniformMat4f("pr_matrix", currentProjMatrix);
//                Shader.GENERAL.disable();
                Shader.WATER.setUniformMat4f("pr_matrix", currentProjMatrix);
                Shader.WATER.setUniform1f("nearPlane", PERSP_NEAR_PLANE);
                Shader.WATER.setUniform1f("farPlane", PERSP_FAR_PLANE);
                Shader.WATER.setUniform1f("nearPlane", ORTHO_NEAR_PLANE);
                Shader.WATER.setUniform1f("farPlane", ORTHO_FAR_PLANE);
                Shader.WATER.disable();
                Shader.GENERAL.setUniformMat4f("pr_matrix", new Matrix4f().ortho(0, gameWidth, 0, gameHeight, -100000, 100000));
            }
        });

        try {
            // check if XInput 1.3 is available
            if (XInputDevice.isAvailable()) {
                System.out.println("XInput 1.3 is available on this platform.");
                usingXInput = true;
            }

            // check if XInput 1.4 is available
            if (XInputDevice14.isAvailable()) {
                System.out.println("XInput 1.4 is available on this platform.");
                usingXInput14 = true;
            }
        } catch (UnsatisfiedLinkError e) {
//            e.printStackTrace();
            System.err.println("Failed to load XInput. Missing library or unsupported platform.");
        }

        Object xInputDevice;

        if (usingXInput || usingXInput14) {
            boolean notLoaded = false;
            Object[] devices;

            try {
                if (usingXInput && !usingXInput14) {
                    devices = XInputDevice.getAllDevices();
                } else {
                    devices = XInputDevice14.getAllDevices();
                }
            } catch (XInputNotLoadedException e) {
                e.printStackTrace();
                System.err.println("XInput is not loaded.");
                usingXInput = false;
                usingXInput14 = false;
                notLoaded = true;
                devices = null;
            }

            if (!notLoaded) {
                xInputDevice = devices[0];

                XInputDeviceListener listener = new SimpleXInputDeviceListener() {
                    @Override
                    public void connected() {
                        System.out.println("XInput device connected.");
                    }

                    @Override
                    public void disconnected() {
                        System.out.println("XInput device disconnected.");
                    }

                    @Override
                    public void buttonChanged(final XInputButton button, final boolean pressed) {
                        // the given button was just pressed (if pressed == true) or released (pressed == false)
                        if (pressed) {
                            System.out.println("XInput button pressed: " + button.name());
                        } else {
                            System.out.println("XInput button released: " + button.name());
                        }

                        XInputState.setButton(button, pressed);
                    }
                };

                if (xInputDevice instanceof XInputDevice14) {
                    ((XInputDevice14) xInputDevice).addListener(listener);
                } else {
                    ((XInputDevice) xInputDevice).addListener(listener);
                }
            } else {
                xInputDevice = null;
            }
        } else {
            System.err.println("Not using XInput.");
            xInputDevice = null;
        }

        glfwSetWindowFocusCallback(window, new GLFWWindowFocusCallback() {
            @Override
            public void invoke(long window, boolean focused) {
                if (xInputDevice != null) {
                    if (xInputDevice instanceof XInputDevice14) {
                        XInputDevice14.setEnabled(focused);
                    }
                }
            }
        });

        Sync sync = new Sync();
        long lastFPS = Util.getTime();

        Camera camera = new Camera();
        float averageDeltaTime = 0.0f;

        Shader.GENERAL.disable();

        List<Water> waters = new ArrayList<Water>();
        Water water = new Water(-10, 75, -75);
        waters.add(water);

        WaterFrameBuffers fbos = new WaterFrameBuffers();

        WaterRenderer waterRenderer = new WaterRenderer(currentProjMatrix, fbos);

        List<GUITexture> guis = new ArrayList<GUITexture>();

//        GUITexture reflectGUI = new GUITexture(fbos.getReflectionTexture(), new Vector2f(-0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
//        GUITexture refractGUI = new GUITexture(fbos.getRefractionTexture(), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
//        guis.add(reflectGUI);
//        guis.add(refractGUI);

        GUIRenderer guiRenderer = new GUIRenderer();

        glEnable(GL_CLIP_DISTANCE0);

//        Player player2 = new Player(new Location(world, 45, -75));
//        world.addEntity(player2);
//
//        Player player3 = new Player(new Location(world, 30, -5));
//        world.addEntity(player3);

        // Used to reduce glitchy edges when water intersects geometry.
        float waterHeightIncrease = 0.5f;

        while (!glfwWindowShouldClose(window)) {
            deltaTime = (float) (Util.getTime() - lastFrame) / 1000.0f;
            lastFrame = Util.getTime();
            averageDeltaTime += deltaTime;

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            camera.setPosition(new Vector3f(0, 0, 0));
            //camera.setPosition(new Vector3f(camera.getPosition().x, camera.getPosition().y + 0.5f, camera.getPosition().z + 0.5f));
            //camera.setPosition(new Vector3f(camera.getPosition().x + 0.1f, 100.0f,  0.0f));
//            camera.setPosition(new Vector3f(45.0f, 100.0f, 0.0f));
            //camera.setYaw(camera.getYaw() - 0.5f);
//            camera.setPitch(55.0f);
            //System.out.println(camera.getPosition());

//            player.move(new Location(world, 75, -5), true);
//            player.setRotation(player.getRotation() + 1.0);
//            player2.setRotation(player2.getRotation() - 2.0);
//            player3.setRotation(player3.getRotation() + 3.5);

            if (xInputDevice != null) {
                if (xInputDevice instanceof XInputDevice14) {
                    if (XInputState.getAxes() == null)
                        XInputState.setAxes(((XInputDevice14) xInputDevice).getComponents().getAxes(), 0.23f, 0.23f);

                    if (((XInputDevice14) xInputDevice).poll()) {
                        XInputComponents components = ((XInputDevice14) xInputDevice).getComponents();
                        XInputButtons buttons = components.getButtons();
                        XInputState.setAxes(components.getAxes(), 0.23f, 0.23f);

                        if (XInputDevice14.isGuideButtonSupported()) {
                            XInputState.setButton(XInputButton.GUIDE_BUTTON, buttons.guide);
                        }
                    }

                    //((XInputDevice14) xInputDevice).setVibration(20000, 20000);
                } else {
                    if (XInputState.getAxes() == null)
                        XInputState.setAxes(((XInputDevice) xInputDevice).getComponents().getAxes(), 0.23f, 0.23f);

                    if (((XInputDevice) xInputDevice).poll()) {
                        XInputComponents components = ((XInputDevice) xInputDevice).getComponents();
                        XInputButtons buttons = components.getButtons();
                        XInputState.setAxes(components.getAxes(), 0.23f, 0.23f);

                        if (XInputDevice.isGuideButtonSupported()) {
                            XInputState.setButton(XInputButton.GUIDE_BUTTON, buttons.guide);
                        }
                    }

                    //((XInputDevice) xInputDevice).setVibration(5000, 5000);
                }

                inputManager.updateXInputController();

                XInputState.update();
            }

            inputManager.update();

            while (!toRun.isEmpty()) toRun.pop().run();
            world.update(player, selectyTile);

            fbos.bindReflectionFrameBuffer();
            float distance = 2 * (camera.getPosition().y - water.getHeight());
            camera.getPosition().y -= distance;
            camera.invertPitch();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            world.renderWorld(camera, new Vector4f(0, 1, 0, -water.getHeight() + waterHeightIncrease));
            camera.getPosition().y += distance;
            camera.invertPitch();

            fbos.bindRefractionFrameBuffer();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            world.renderWorld(camera, new Vector4f(0, -1, 0, water.getHeight()));
            fbos.unbindFrameBuffer();

            world.renderWorld(camera, new Vector4f(0, 0, 0, 0));
            waterRenderer.render(waters, camera, light, deltaTime);

            guiManager.render();

            guiRenderer.render(guis);
            TextMaster.render();

            glfwSwapBuffers(window);
            glfwPollEvents();

            if (Util.getTime() - lastFPS >= 1000) {
                System.out.println("FPS: " + currentFPS);
                System.out.println("Average deltaTime: " + (averageDeltaTime / currentFPS));
                averageDeltaTime = 0;

                currentFPS = 0;
                lastFPS += 1000;
            }

            currentFPS++;

            sync.sync(120);
        }

        Shader.cleanUpAll();
        TextMaster.cleanUp();
        waterRenderer.cleanUp();
        guiRenderer.cleanUp();

        for (GUITexture guiTexture : guis) {
            guiTexture.cleanUp();
        }
    }

    public Tile selectyTile;

    @Override
    public void handleControllerInput(Controller state, Controller last) {
        double halfGameWidth = gameWidth / 2f;
        double halfGameHeight = gameHeight / 2f;

        System.out.println(state.getAnalogRight().x() + ":" + state.getAnalogRight().y() + ":" + state.isRightPadTouched());

        double tileX = halfGameWidth * state.getAnalogRight().x() + halfGameWidth;
        double tileY = halfGameHeight * state.getAnalogRight().y() + halfGameHeight;

        if (selectyTile == null) {
            selectyTile = new Tile(new Location(world, Integer.MAX_VALUE, Integer.MAX_VALUE));
            selectyTile.setCanBeInteractedWith(false);
            runOnUIThread(() -> world.addEntity(selectyTile));
        }
        double rot = state.getLeftTrigger() * 90;
//        Size size = new Size(100f * Math.max(1 - Input.Left_Trigger, 0.3), 100f * Math.max(1 - Input.Right_Trigger, 0.3));
        Size size = new Size(100f, 100f);
//        selectyTile.setSize(size);
        selectyTile.setRotation(rot);

        if (!state.isRightPadTouched() || state.getAnalogRight().isNeutral())
            selectyTile.move(new Location(world, Integer.MAX_VALUE, Integer.MAX_VALUE), true);
        else
            selectyTile.move(new Location(world, tileX, tileY), true);

        runOnUIThread(() -> {
            if (state.isRightPadPressed()) {
                Tile newTile = new Tile(new Location(world, tileX, tileY));
                newTile.setSize(size);
                newTile.setRotation(rot);

                for (Entity entity : world.getEntities())
                    if (entity != selectyTile && entity.getRectangle().intersects(newTile.getRectangle())) return;
                world.addEntity(newTile);
            }

            if (state.isLeftPadTouched()) {
                guiManager.displayGUI(new SelectionGUI());
            } else guiManager.hideGUI(SelectionGUI.class);
        });

        if (state.isBHeld()) {
            world.getEntities().clear();

            world.addEntity(player);
            world.addEntity(selectyTile);
        }
    }

    @Override
    public void handleKeyboardInput(Keyboard keyboard) {

    }

    @Override
    public void handleMouseInput(Mouse mouse) {

    }
}