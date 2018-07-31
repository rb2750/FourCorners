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
    private Keyboard keyboard = new Keyboard();
    private Mouse mouse = new Mouse(0, 0);
    private HashMap<Integer, Boolean> keyState = new HashMap<>();

    public void Setup() {
        glfwSetKeyCallback(Main.handle, (window, key, scancode, action, modifiers) -> {
            if (action == GLFW_RELEASE) keyState.put(key, false);//keyboard.handleKeyRelease(key);
            if (action == GLFW_PRESS || action == GLFW_REPEAT) keyState.put(key, true);//keyboard.handleKeyPress(key);
        });

        //Mouse
        glfwSetCursorPosCallback(Main.handle, new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xPos, double yPos) {
                mouse.setX((float) xPos/*((float) xPos - Main.getGameWidth() / 2f) / Main.getGameWidth()*/);
                mouse.setY((float) (Main.getGameHeight() - yPos)/*((float) yPos - Main.getGameHeight() / 2f) / Main.getGameHeight()*/);

                for (InputListener listener : listeners) listener.handleMouseInput(mouse);
            }
        });
        glfwSetMouseButtonCallback(Main.handle, new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int key, int action, int modifiers) {
                if (key == GLFW_MOUSE_BUTTON_LEFT) mouse.setLeftMouseDown(action == GLFW_PRESS);
                if (key == GLFW_MOUSE_BUTTON_MIDDLE) mouse.setMiddleMouseDown(action == GLFW_PRESS);
                if (key == GLFW_MOUSE_BUTTON_RIGHT) mouse.setRightMouseDown(action == GLFW_PRESS);

                for (InputListener listener : listeners) listener.handleMouseInput(mouse);
            }
        });

        //Steam Controller
        try {
            SteamControllerListener listener = new SteamControllerListener(SteamController.getConnectedControllers().get(0));
            listener.open();
            listener.addSubscriber((state, last) -> {
                queue.add(new SteamQueuedEvent(new Controller().updateSteam(state), new Controller().updateSteam(last)));
            });
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Failed to find Steam Controller.");
        }
    }

    public static void registerInputListener(InputListener listener) {
        listeners.add(listener);
    }

    public void update() {
        while (!queue.isEmpty()) {
            SteamQueuedEvent event = queue.remove();

            for (InputListener listener : listeners) listener.handleControllerInput(event.state, event.last);
        }

        for (Map.Entry<Integer, Boolean> entry : keyState.entrySet()) {
            if (entry.getValue()) keyboard.handleKeyPress(entry.getKey());
            else keyboard.handleKeyRelease(entry.getKey());
        }

        for (InputListener listener : listeners) {
            listener.handleKeyboardInput(keyboard);
            listener.handleMouseInput(mouse);
        }
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