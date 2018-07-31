package com.rb2750.lwjgl.input;

import com.rb2750.lwjgl.Main;
import com.rb2750.lwjgl.input.controllers.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import se.albin.steamcontroller.SteamController;
import se.albin.steamcontroller.SteamControllerListener;

import java.util.*;

public class InputManager {
    private static List<InputListener> listeners = new ArrayList<>();
    private Queue<SteamQueuedEvent> queue = new ArrayDeque<>();
    @Getter private Keyboard keyboard = new Keyboard();
    @Getter private Mouse mouse = new Mouse(0, 0);
    private HashMap<Integer, Boolean> keyState = new HashMap<>();
    private InputMode mode;
    @Getter private Controller currentControllerState;

    public void Setup() {
        glfwSetKeyCallback(Main.handle, (window, key, scancode, action, modifiers) -> {
            if (action == GLFW_RELEASE) {
                keyState.put(key, false);
            }
            if (action == GLFW_PRESS || action == GLFW_REPEAT) {
                keyState.put(key, true);
                mode = InputMode.KEYBOARD;
            }
        });

        //Mouse
        glfwSetCursorPosCallback(Main.handle, new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xPos, double yPos) {
                mouse.setX((float) xPos);
                mouse.setY((float) (Main.getGameHeight() - yPos));
            }
        });
        glfwSetMouseButtonCallback(Main.handle, new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int key, int action, int modifiers) {
                if (key == GLFW_MOUSE_BUTTON_LEFT) mouse.setLeftMouseDown(action == GLFW_PRESS);
                if (key == GLFW_MOUSE_BUTTON_MIDDLE) mouse.setMiddleMouseDown(action == GLFW_PRESS);
                if (key == GLFW_MOUSE_BUTTON_RIGHT) mouse.setRightMouseDown(action == GLFW_PRESS);
            }
        });
        glfwSetScrollCallback(Main.handle, (window, dx, dy) -> {
            mouse.setScrollDx(dx);
            mouse.setScrollDy(dy);
        });

        //Steam Controller
        try {
            SteamControllerListener listener = new SteamControllerListener(SteamController.getConnectedControllers().get(0));
            listener.open();
            listener.addSubscriber((state, last) -> {
                if (new Controller().updateSteam(state).isKeyDown()) mode = InputMode.STEAM_CONTROLLER;
                queue.add(new SteamQueuedEvent(currentControllerState = new Controller().updateSteam(state), new Controller().updateSteam(last)));
            });
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Failed to find Steam Controller.");
        }
    }

    public static void registerInputListener(InputListener listener) {
        listeners.add(listener);
    }

    public void update() {
        if (mode == InputMode.KEYBOARD) queue.clear();

        while (!queue.isEmpty()) {
            SteamQueuedEvent event = queue.remove();

            for (InputListener listener : listeners) listener.handleControllerInput(event.state, event.last);
        }

        for (Map.Entry<Integer, Boolean> entry : keyState.entrySet()) {
            if (entry.getValue()) keyboard.handleKeyPress(entry.getKey());
            else keyboard.handleKeyRelease(entry.getKey());
        }

        for (InputListener listener : listeners) {
            if (currentControllerState == null || !currentControllerState.isPadTouched())
                listener.handleMouseInput(mouse);
            if (mode == null || mode == InputMode.KEYBOARD) {
                listener.handleKeyboardInput(keyboard);
            }
        }

        mouse.setScrollDx(0);
        mouse.setScrollDy(0);
    }

    public void updateXInputController() {
        for (InputListener listener : listeners)
            listener.handleControllerInput(new Controller().updateXInput(), new Controller().updateLastXInput());
    }

    @AllArgsConstructor
    public static final class SteamQueuedEvent {
        @Getter
        private Controller state;
        @Getter
        private Controller last;
    }
}