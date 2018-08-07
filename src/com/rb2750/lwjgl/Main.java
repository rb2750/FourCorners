package com.rb2750.lwjgl;

import com.ivan.xinput.*;
import com.ivan.xinput.enums.XInputButton;
import com.ivan.xinput.exceptions.XInputNotLoadedException;
import com.ivan.xinput.listener.SimpleXInputDeviceListener;
import com.ivan.xinput.listener.XInputDeviceListener;
import com.rb2750.lwjgl.animations.TestAnimation;
import com.rb2750.lwjgl.debug.Timer;
import com.rb2750.lwjgl.entities.*;
import com.rb2750.lwjgl.graphics.*;
import com.rb2750.lwjgl.gui.*;
import com.rb2750.lwjgl.gui.fonts.fontcreator.FontType;
import com.rb2750.lwjgl.gui.fonts.fontcreator.GUIText;
import com.rb2750.lwjgl.gui.fonts.fontrenderer.TextMaster;
import com.rb2750.lwjgl.input.*;
import com.rb2750.lwjgl.input.controllers.*;
import com.rb2750.lwjgl.util.*;
import com.rb2750.lwjgl.world.World;
import com.rb2750.lwjgl.world.WorldManager;
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

import java.awt.*;
import java.io.File;
import java.lang.Math;
import java.nio.IntBuffer;
import java.util.*;
import java.util.List;

public class Main implements InputListener {
    public static Main instance;

    private static final float ORTHO_NEAR_PLANE = -100000.0f;
    private static final float ORTHO_FAR_PLANE = 100000.0f;
    private static final float PERSP_NEAR_PLANE = 0.1f;
    private static final float PERSP_FAR_PLANE = 1000.0f;
    private static final boolean USE_TIMERS = false;

    // The handle
    public static long handle;
    @Getter
    private static int gameWidth = 1000;
    @Getter
    private static int gameHeight = 1000;
    @Getter
    private Player player;

    //input
    private boolean usingXInput = false;
    private boolean usingXInput14 = false;

    @Getter
    private InputManager inputManager = new InputManager();
    @Getter
    private GUIManager guiManager = new GUIManager();

    private int currentFPS;

    private Matrix4f currentProjMatrix;

    private DirectionalLight directionalLight;

    private FontType font;
    private GUIText fpsText;
    private GUIText posText;

    public static Entity selectedObject;

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        instance = this;
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        // Free the handle callbacks and destroy the handle
        glfwFreeCallbacks(handle);
        glfwDestroyWindow(handle);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
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

        // Create the handle
        handle = glfwCreateWindow(gameWidth, gameHeight, "LWJGL Test", NULL, NULL);

        if (handle == NULL)
            throw new RuntimeException("Failed to create the GLFW handle");

        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            glfwGetWindowSize(handle, pWidth, pHeight);
            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the handle
            assert vidmode != null;
            glfwSetWindowPos(
                    handle,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        }

        glfwMakeContextCurrent(handle);

        // Enable v-sync
        glfwSwapInterval(0);

        // Make the handle visible
        glfwShowWindow(handle);

        GL.createCapabilities();
        GLUtil.setupDebugMessageCallback();

        glViewport(0, 0, gameWidth, gameHeight);

        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
//        glEnable(GL_CULL_FACE);
//        glCullFace(GL_BACK);

        glEnable(GL_DEPTH_TEST);

        glActiveTexture(GL_TEXTURE1);

        Shader.loadAllShaders();

        currentProjMatrix = new Matrix4f().ortho(0, gameWidth, 0, gameHeight, ORTHO_NEAR_PLANE, ORTHO_FAR_PLANE);
        //  currentProjMatrix = new Matrix4f().perspective(70.0f, (float) gameWidth / (float) gameHeight, PERSP_NEAR_PLANE, PERSP_FAR_PLANE);

        Shader.GENERAL.setUniformMat4f("pr_matrix", currentProjMatrix);
        Shader.GENERAL.disable();

//        Shader.WATER.setUniform1f("nearPlane", PERSP_NEAR_PLANE);
//        Shader.WATER.setUniform1f("farPlane", PERSP_FAR_PLANE);
        Shader.WATER.setUniform1f("nearPlane", ORTHO_NEAR_PLANE);
        Shader.WATER.setUniform1f("nearPlane", ORTHO_FAR_PLANE);

        Shader.WATER.disable();

        //Shader.GENERAL.setUniformMat4f("pr_matrix", MatrixUtil.projection(gameWidth, gameHeight, 0.1f, 1000.0f, 70.0f));
        Shader.GENERAL.setUniform1i("tex", 1);
        Shader.GENERAL.disable();

        Shader.BASIC_TEX.setUniformMat4f("pr_matrix", currentProjMatrix);
        Shader.BASIC_TEX.setUniform1i("tex", 1);
        Shader.BASIC_TEX.disable();

        Shader.BASIC_COLOUR.setUniformMat4f("pr_matrix", currentProjMatrix);
        Shader.BASIC_COLOUR.disable();

        System.out.println("OpenGL version: " + glGetString(GL_VERSION));

        WorldManager.createDefaultWorld();

        World world = WorldManager.getWorlds().get(0);

        player = new Player(new Location(world, 0, 0));
        world.addEntity(player);

        directionalLight = new DirectionalLight(new Light(new Vector3f(1, 1, 1), 0.8f),
                new Vector3f(1, 1, 1));

        //world.setDirectionalLight(directionalLight);
//        world.addPointLight(new PointLight(new Light(new Vector3f(1, 0, 0), 0.8f),
//                new Attenution(0, 0, 1),
//                new Vector3f(-2, 0, 5), 6.0f));
//        world.addPointLight(new PointLight(new Light(new Vector3f(0, 0, 1), 0.8f),
//                new Attenution(0, 0, 1),
//                new Vector3f(2, 0, 7), 6.0f));

        InputManager.registerInputListener(this);
        inputManager.Setup();

        TextMaster.init();

        font = new FontType(new Texture("res/fonts/calibriHR.png").getTexture(), new File("res/fonts/calibriHR.fnt"));
//        GUIText text = new GUIText("The quick brown fox jumps over the lazy dog.", 2, font, new Vector2f(0.0f, 0.0f), 1f, true);
//        text.setColour(1, 1, 0);
        fpsText = new GUIText("", 1, font, new Vector2f(0.0f, 0.0f), 1f, false);
        posText = new GUIText("", 1, font, new Vector2f(0.85f, 0f), 1f, false);

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


        glfwSetWindowSizeCallback(handle, new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                gameWidth = width;
                gameHeight = height;
                glViewport(0, 0, width, height);
//                Shader.GENERAL.setUniformMat4f("pr_matrix", new Matrix4f().ortho(0, gameWidth, 0, gameHeight, -1, 1));
//                System.out.println("AR: " + ((float) gameWidth / (float) gameHeight));
                currentProjMatrix = new Matrix4f().ortho(0, gameWidth, 0, gameHeight, ORTHO_NEAR_PLANE, ORTHO_FAR_PLANE);
                //  currentProjMatrix = new Matrix4f().perspective(70.0f, (float) gameWidth / (float) gameHeight, PERSP_NEAR_PLANE, PERSP_FAR_PLANE);
//                Shader.GENERAL.setUniformMat4f("pr_matrix", currentProjMatrix);
//                Shader.GENERAL.disable();
                Shader.WATER.setUniformMat4f("pr_matrix", currentProjMatrix);
//                Shader.WATER.setUniform1f("nearPlane", PERSP_NEAR_PLANE);
//                Shader.WATER.setUniform1f("farPlane", PERSP_FAR_PLANE);
                Shader.WATER.setUniform1f("nearPlane", ORTHO_NEAR_PLANE);
                Shader.WATER.setUniform1f("farPlane", ORTHO_FAR_PLANE);
                Shader.WATER.disable();
                Shader.GENERAL.setUniformMat4f("pr_matrix", currentProjMatrix);
                Shader.GENERAL.disable();
                Shader.BASIC_TEX.setUniformMat4f("pr_matrix", currentProjMatrix);
                Shader.BASIC_TEX.disable();
                Shader.BASIC_COLOUR.setUniformMat4f("pr_matrix", currentProjMatrix);
                Shader.BASIC_COLOUR.disable();

                TextMaster.cleanUp();

                font = new FontType(new Texture("res/fonts/calibriHR.png").getTexture(), new File("res/fonts/calibriHR.fnt"));
//        GUIText text = new GUIText("The quick brown fox jumps over the lazy dog.", 2, font, new Vector2f(0.0f, 0.0f), 1f, true);
//        text.setColour(1, 1, 0);
                fpsText = new GUIText("", 1, font, new Vector2f(0.0f, 0.0f), 1f, false);
                posText = new GUIText("", 1, font, new Vector2f(0.85f, 0f), 1f, false);
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

        glfwSetWindowFocusCallback(handle, new GLFWWindowFocusCallback() {
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

        List<Water> waters = new ArrayList<>();
        Water water = new Water(-10, 75, -75);
        waters.add(water);

        WaterFrameBuffers fbos = new WaterFrameBuffers();

        WaterRenderer waterRenderer = new WaterRenderer(currentProjMatrix, fbos);

        List<GUITexture> guis = new ArrayList<>();

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
        Timer updateTimer;
        Timer renderTimer;
        Timer sleepTimer;

        if (USE_TIMERS) {
            updateTimer = new Timer("update");
            updateTimer.addTimer("input");
            updateTimer.addTimer("tasks");
            updateTimer.addTimer("world");

            renderTimer = new Timer("render");
            renderTimer.addTimer("water");
            renderTimer.addTimer("world");
            renderTimer.addTimer("gui");

            sleepTimer = new Timer("sleep");
        }

        while (!glfwWindowShouldClose(handle)) {
            deltaTime = (float) (Util.getTime() - lastFrame) / 1000.0f;
            lastFrame = Util.getTime();
            averageDeltaTime += deltaTime;

            if (USE_TIMERS)
                updateTimer.startTimer();

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            camera.setPosition(new Vector3f(0, 0f, 0));

            posText.setText("X: " + (int) player.getLocation().getX() + " Y: " + (int) player.getLocation().getY());

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

            if (USE_TIMERS)
                updateTimer.startSubTimer("input");

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

            if (USE_TIMERS) {
                updateTimer.stopSubTimer("input");
                updateTimer.startSubTimer("tasks");
            }

            while (!toRun.isEmpty()) toRun.pop().run();

            if (USE_TIMERS) {
                updateTimer.stopSubTimer("tasks");
                updateTimer.startSubTimer("world");
            }

            for (World world : WorldManager.getWorlds())
                world.update(deltaTime);

            if (USE_TIMERS) {
                updateTimer.stopSubTimer("world");

                updateTimer.stopTimer();

                renderTimer.startTimer();
                renderTimer.startSubTimer("water");
            }

//            fbos.bindReflectionFrameBuffer();
//            float distance = 2 * (camera.getPosition().y - water.getHeight());
//            camera.getPosition().y -= distance;
//            camera.invertPitch();
//            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//            world.renderWorld(camera, new Vector4f(0, 1, 0, -water.getHeight() + waterHeightIncrease));
//            camera.getPosition().y += distance;
//            camera.invertPitch();
//
//            fbos.bindRefractionFrameBuffer();
//            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//            world.renderWorld(camera, new Vector4f(0, -1, 0, water.getHeight()));
//            fbos.unbindFrameBuffer();

            if (USE_TIMERS) {
                renderTimer.pauseSubTimer("water");
                renderTimer.startSubTimer("world");
            }

            if (player.getWorld() != null)
                player.getWorld().renderWorld(camera, new Vector4f(0, 0, 0, 0));

            if (USE_TIMERS) {
                renderTimer.stopSubTimer("world");
                renderTimer.resumeSubTimer("water");
            }

            waterRenderer.render(waters, camera, deltaTime);

            if (USE_TIMERS) {
                renderTimer.stopSubTimer("water");
                renderTimer.startSubTimer("gui");
            }

            guiManager.render(player.getWorld());

            guiRenderer.render(guis);
            TextMaster.render();

            if (USE_TIMERS)
                renderTimer.pauseSubTimer("gui");

            glfwSwapBuffers(handle);
            glfwPollEvents();

            if (USE_TIMERS)
                renderTimer.stopTimer();

            if (Util.getTime() - lastFPS >= 1000) {
                if (USE_TIMERS)
                    renderTimer.resumeSubTimer("gui");

                fpsText.setText("FPS: " + currentFPS);

                if (USE_TIMERS)
                    renderTimer.stopSubTimer("gui");

//                System.out.println("Average delta time: " + (averageDeltaTime / (float)currentFPS));
                averageDeltaTime = 0.0f;

                currentFPS = 0;
                lastFPS += 1000;
            }

            currentFPS++;

            if (USE_TIMERS)
                sleepTimer.startTimer();

            sync.sync(60);

            if (USE_TIMERS) {
                sleepTimer.stopTimer();

                updateTimer.timerInfomation();
                renderTimer.timerInfomation();
                sleepTimer.timerInfomation();
            }
        }

        Shader.cleanUpAll();
        TextMaster.cleanUp();
        waterRenderer.cleanUp();
        guiRenderer.cleanUp();


        for (GUITexture guiTexture : guis) {
            guiTexture.cleanUp();
        }
    }

    private static final float gridSize = 100f;
    private Circle pointer;

    private Vector2f snapToGrid(Vector2f location) {
        return new Vector2f((float) Math.floor(location.x / gridSize) * gridSize, (float) Math.floor(location.y / gridSize) * gridSize);
    }

    private Vector2f asGridLocation(Vector2f location) {
        return new Vector2f((float) Math.floor(location.x / gridSize), (float) Math.floor(location.y / gridSize));
    }

    @Override
    public void handleControllerInput(Controller state, Controller last) {
        double halfGameWidth = gameWidth / 2f;
        double halfGameHeight = gameHeight / 2f;

        float x = (float) (halfGameWidth * state.getAnalogRight().x() + halfGameWidth);
        float y = (float) (halfGameHeight * state.getAnalogRight().y() + halfGameHeight);

        Vector2f location = snapToGrid(new Vector2f(x, y));

        tryCreateSelectyObject();

        Vector3f rot = new Vector3f(0, 0, 0);
        Size size = new Size(100f, 100f);

        if (state.getLeftTrigger() == 1) {
            removeEntityAtLocation(location);
        }

        if (!state.isRightPadTouched() || state.getAnalogRight().isNeutral()) {
            selectedObject.setInvisible(true);
            pointer.setInvisible(true);
        } else {
            selectedObject.setInvisible(false);
            pointer.setInvisible(false);
        }
        selectedObject.teleport(new Location(player.getWorld(), location.x, location.y));
        pointer.teleport(new Location(player.getWorld(), x, y));
        if (getTileAtLocation(new Vector2f(x, y)) != null || intersectsWithEntity(new Vector2f(x, y)))
            pointer.setBaseColour(new Vector4f(255, 0, 0, 255));
        else pointer.setBaseColour(new Vector4f(0, 255, 0, 200));

        runOnUIThread(() -> {
            if (state.isRightPadPressed() || state.getRightTrigger() == 1)
                tryPlaceObject(location.x, location.y, size, rot);

            if (state.isLeftPadTouched()) {
                if (!guiManager.guiExists(SelectionGUI.class)) guiManager.displayGUI(new SelectionGUI());
            } else guiManager.hideGUI(player.getWorld(), SelectionGUI.class);
        });

        if (state.isBHeld()) resetWorld();
    }

    private void tryPlaceObject(float objectX, float objectY, Size size, Vector3f rotation) {
        Vector2f location = new Vector2f(objectX, objectY);

        Entity newObject = selectedObject.clone();
        newObject.teleport(new Location(player.getWorld(), location));
        newObject.setSize(size, true);
        newObject.setRotation(rotation);
        newObject.setBaseColour(new Vector4f(newObject.getBaseColour()));
        newObject.getBaseColour().w = 255;
        newObject.setLayer(-120);

        if (getTileAtLocation(location) != null || intersectsWithEntity(location)) return;

        Vector2f tileLocation = asGridLocation(location);

        player.getWorld().getWorldTiles()[(int) tileLocation.x][(int) tileLocation.y] = newObject;
    }

    private void resetWorld() {
        player.getWorld().getEntities().clear();
        for (int x = 0; x < player.getWorld().getWorldTiles().length; x++) {
            for (int y = 0; y < player.getWorld().getWorldTiles()[x].length; y++) {
                player.getWorld().getWorldTiles()[x][y] = null;
            }
        }

        player.getWorld().addEntity(player);
    }

    private void tryCreateSelectyObject() {
        if (selectedObject == null) {
            selectedObject = new Tile();
            selectedObject.getBaseColour().w = 100;
            player.getWorld().addDisplayObject(selectedObject);
        }
        if (pointer == null) {
            pointer = new Circle(new Location(player.getWorld(), Integer.MAX_VALUE, Integer.MAX_VALUE));
            pointer.setBaseColour(new Vector4f(150, 150, 150, 200));
            pointer.size = new Size(15, 15);
            pointer.setLayer(50);
            player.getWorld().addDisplayObject(pointer);
        }
        selectedObject.teleport(new Location(player.getWorld(), Integer.MAX_VALUE, Integer.MAX_VALUE));
    }

    @Override
    public void handleKeyboardInput(Keyboard keyboard) {
        if (keyboard.isKeyDown(GLFW_KEY_R)) resetWorld();
        if (keyboard.isKeyDown(GLFW_KEY_X)) player.addAnimation(new TestAnimation());
    }

    private long lastScroll;

    @Override
    public void handleMouseInput(Mouse mouse) {
        tryCreateSelectyObject();

        float x = Math.max(0, mouse.getX());
        float y = Math.max(0, mouse.getY());
        Vector2f location = snapToGrid(new Vector2f(x, y));

        int size = 100;

        Size s = new Size(size, size);

        selectedObject.setSize(s, true);

        if (mouse.getScrollDy() != 0) {
            lastScroll = System.currentTimeMillis();
            if (!guiManager.guiExists(SelectionGUI.class))
                runOnUIThread(() -> guiManager.displayGUI(new SelectionGUI()));
        }

        if (System.currentTimeMillis() - lastScroll > 1200) {
            runOnUIThread(() -> guiManager.hideGUI(player.getWorld(), SelectionGUI.class));
        }

        if (System.currentTimeMillis() - mouse.getLastUsed() > 900) selectedObject.setInvisible(true);
        else selectedObject.setInvisible(false);
        selectedObject.teleport(new Location(player.getWorld(), location.x, location.y));

        if (mouse.isLeftMouseDown()) {
            tryPlaceObject(location.x, location.y, s, new Vector3f());
        } else if (mouse.isRightMouseDown()) {
            removeEntityAtLocation(location);
        }
    }

    private boolean intersectsWithEntity(Vector2f point) {
        point = snapToGrid(point);

        for (Entity entity : player.getWorld().getEntities())
            if (entity.getRectangle().intersects(new Rectangle((int) point.x, (int) point.y, (int) gridSize, (int) gridSize)))
                return true;
        return false;
    }

    private boolean locationInGrid(Vector2f location) {
        location = asGridLocation(location);
        return location.x >= 0 && location.y >= 0 && location.x <= player.getWorld().getSettings().getWorldWidth() && location.y <= player.getWorld().getSettings().getWorldHeight();
    }

    private Entity getTileAtLocation(Vector2f location) {
        if (!locationInGrid(location)) return null;
        location = asGridLocation(location);
        return player.getWorld().getWorldTiles()[(int) location.x][(int) location.y];
    }

    private void removeEntityAtLocation(Vector2f location) {
        if (!locationInGrid(location)) return;
        location = asGridLocation(location);
        player.getWorld().getWorldTiles()[(int) location.x][(int) location.y] = null;
    }
}