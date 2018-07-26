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
import com.rb2750.lwjgl.entities.Entity;
import com.rb2750.lwjgl.entities.Player;
import com.rb2750.lwjgl.entities.Tile;
import com.rb2750.lwjgl.gui.GUIManager;
import com.rb2750.lwjgl.gui.SelectionGUI;
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

                glMatrixMode(GL_MODELVIEW);
            }
        });

        try
        {
            Input.Setup();

            SteamControllerListener listener = new SteamControllerListener(SteamController.getConnectedControllers().get(0));
            listener.open();
            listener.addSubscriber((state, last) -> {
                currentState = state;
                lastState = last;
                handleControls(currentState, lastState);
                runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        guiManager.handleInput(currentState, lastState);
                    }
                });
            });
        }
        catch (IndexOutOfBoundsException e)
        {
            e.printStackTrace();
            System.err.println("Failed to find Steam Controller.");
        }

        // check if XInput 1.3 is available
        if (XInputDevice.isAvailable())
        {
            System.out.println("XInput 1.3 is available on this platform.");
            usingXInput = true;
        }

        // check if XInput 1.4 is available
        if (XInputDevice14.isAvailable())
        {
            System.out.println("XInput 1.4 is available on this platform.");
            usingXInput14 = true;
        }

        Object xInputDevice;

        if (usingXInput || usingXInput14)
        {
            System.out.println("Native library version (XInput): " + XInputDevice.getLibraryVersion());
            boolean notLoaded = false;
            Object[] devices;

            try
            {
                if (usingXInput && !usingXInput14)
                {
                    devices = XInputDevice.getAllDevices();
                }
                else
                {
                    devices = XInputDevice14.getAllDevices();
                }
            }
            catch (XInputNotLoadedException e)
            {
                e.printStackTrace();
                System.err.println("XInput is not loaded.");
                usingXInput = false;
                usingXInput14 = false;
                notLoaded = true;
                devices = null;
            }

            if (!notLoaded)
            {
                xInputDevice = devices[0];

                XInputDeviceListener listener = new SimpleXInputDeviceListener()
                {
                    @Override
                    public void connected()
                    {
                        System.out.println("XInput device connected.");
                    }

                    @Override
                    public void disconnected()
                    {
                        System.out.println("XInput device disconnected.");
                    }

                    @Override
                    public void buttonChanged(final XInputButton button, final boolean pressed)
                    {
                        // the given button was just pressed (if pressed == true) or released (pressed == false)
                        if (pressed)
                        {
                            System.out.println("XInput button pressed: " + button.name());
                        }
                        else
                        {
                            System.out.println("XInput button released: " + button.name());
                        }

                        XInputState.setButton(button, pressed);
                    }
                };

                if (xInputDevice instanceof XInputDevice14)
                {
                    ((XInputDevice14) xInputDevice).addListener(listener);
                }
                else
                {
                    ((XInputDevice) xInputDevice).addListener(listener);
                }
            }
            else
            {
                xInputDevice = null;
            }
        }
        else
        {
            System.err.println("Not using XInput.");
            xInputDevice = null;
        }

        glfwSetWindowFocusCallback(window, new GLFWWindowFocusCallback()
        {
            @Override
            public void invoke(long window, boolean focused)
            {
                if (xInputDevice != null)
                {
                    if (xInputDevice instanceof XInputDevice14)
                    {
                        XInputDevice14.setEnabled(focused);
                    }
                }
            }
        });

        glfwSetCursorPosCallback(window, new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                cursorLocation.setX((int) xpos);
                cursorLocation.setY(gameHeight - (int) ypos);
            }
        });

        Sync sync = new Sync();

        while (!glfwWindowShouldClose(window)) {
            deltaTime = Util.getTime() - lastFrame;
            lastFrame = Util.getTime();
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            if (xInputDevice != null)
            {
                if (xInputDevice instanceof XInputDevice14)
                {
                    if (XInputState.axes == null) XInputState.axes = ((XInputDevice14) xInputDevice).getComponents().getAxes();

                    if(((XInputDevice14) xInputDevice).poll())
                    {
                        XInputComponents components = ((XInputDevice14) xInputDevice).getComponents();
                        XInputButtons buttons = components.getButtons();
                        XInputState.axes = components.getAxes();

                        if (XInputDevice14.isGuideButtonSupported())
                        {
                            XInputState.setButton(XInputButton.GUIDE_BUTTON, buttons.guide);
                        }
                    }

                    //((XInputDevice14) xInputDevice).setVibration(20000, 20000);
                }
                else
                {
                    if (XInputState.axes == null) XInputState.axes = ((XInputDevice) xInputDevice).getComponents().getAxes();

                    if(((XInputDevice) xInputDevice).poll())
                    {
                        XInputComponents components = ((XInputDevice) xInputDevice).getComponents();
                        XInputButtons buttons = components.getButtons();
                        XInputState.axes = components.getAxes();

                        if (XInputDevice.isGuideButtonSupported())
                        {
                            XInputState.setButton(XInputButton.GUIDE_BUTTON, buttons.guide);
                        }
                    }

                    //((XInputDevice) xInputDevice).setVibration(5000, 5000);
                }
            }

            while (!toRun.isEmpty()) toRun.pop().run();
            world.update(player, selectyTile);

            if (xInputDevice != null)
            {
                handleXInputControls();
                XInputState.update();
            }

            guiManager.update();
            glfwSwapBuffers(window);
            glfwPollEvents();

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

    public void handleXInputControls() {
        int speed = 8;

        if (XInputState.isButtonDown(XInputButton.LEFT_SHOULDER)) speed *= 2;

        player.getAcceleration().setX((int) (speed * XInputState.axes.lx));

        if (XInputState.isButtonPressed(XInputButton.A)) {
            if (!doubleJump && jumping && !player.onGround()/* && player.getAcceleration().getY() < 0*/) {
                doubleJump = true;
                jumping = false;
                player.addAnimation(new FlipAnimation());
            } else if (!player.onGround()) return;
            else jumping = true;

            player.getAcceleration().setY(21);
        }

        if (XInputState.isButtonPressed(XInputButton.B)) {
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

        double tileX = halfGameWidth * XInputState.axes.rx + halfGameWidth;
        double tileY = halfGameHeight * XInputState.axes.ry + halfGameHeight;
        if (selectyTile == null) {
            selectyTile = new Tile(new Location(world, Integer.MAX_VALUE, Integer.MAX_VALUE));
            selectyTile.setCanBeInteractedWith(false);
            runOnUIThread(() -> world.addEntity(selectyTile));
        }

        if (!player.animationExists(SquatAnimation.class)) {
            if (XInputState.isButtonPressed(XInputButton.X)) {
                player.addAnimation(new SquatAnimation());
            }
        } else {
            if (XInputState.isButtonUp(XInputButton.X)) {
                player.getAnimation(SquatAnimation.class).Pause();
            }
            if (XInputState.isButtonDown(XInputButton.X)) {
                player.getAnimation(SquatAnimation.class).Unpause();
            }
        }

        if (XInputState.isButtonDown(XInputButton.START)) {
            player.setSize(new Size(player.getSize().getWidth() + 1, player.getSize().getHeight()));
        }
        //System.out.println(player.getLocation().toString());

        //System.out.println(XInputState.axes.lt + ", " + XInputState.axes.rt);
        Size size = new Size(100f * (float) Math.max(1 - XInputState.axes.lt, 0.3), 100f * (float) Math.max(1 - XInputState.axes.rt, 0.3));

        selectyTile.setSize(size);

        runOnUIThread(() -> {
            if (XInputState.isButtonDown(XInputButton.LEFT_THUMBSTICK)) {
                guiManager.displayGUI(new SelectionGUI());
            } else guiManager.hideGUI(SelectionGUI.class);
        });

        selectyTile.move(new Location(world, (int) tileX, (int) tileY), true);

        if (XInputState.isButtonPressed(XInputButton.RIGHT_THUMBSTICK)) xInputShowBox = !xInputShowBox;

        if (!xInputShowBox)
            selectyTile.move(new Location(world, Integer.MAX_VALUE, Integer.MAX_VALUE), true);
        runOnUIThread(() -> {
            if (XInputState.isButtonPressed(XInputButton.RIGHT_SHOULDER)/* && !last.isLeftPadPressed()*/) {
                Tile newTile = new Tile(new Location(world, (int) tileX, (int) tileY));
                newTile.setSize(size);

                for (Entity entity : world.getEntities())
                    if (entity != selectyTile && entity.getRectangle().intersects(newTile.getRectangle())) return;
                world.addEntity(newTile);
            }
        });
    }
}