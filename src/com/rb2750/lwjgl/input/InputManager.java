package com.rb2750.lwjgl.input;

import com.rb2750.lwjgl.Main;
import com.rb2750.lwjgl.input.controllers.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import se.albin.steamcontroller.SteamController;
import se.albin.steamcontroller.SteamControllerListener;

import java.util.*;

public class InputManager {
    private static List<InputListener> listeners = new ArrayList<>();
    private Queue<SteamQueuedEvent> queue = new ArrayDeque<>();

    public void Setup() {
        glfwSetKeyCallback(Main.instance.window, new GLFWKeyCallbackI() {
            private Keyboard state = new Keyboard();

            @Override
            public void invoke(long window, int key, int scancode, int action, int modifiers) {
                if (action == GLFW_RELEASE) state.handleKeyRelease(key);
                if (action == GLFW_PRESS) state.handleKeyPress(key);
                if (action == GLFW_REPEAT) state.handleKeyPress(key);

                for (InputListener listener : listeners) listener.handleKeyboardInput(state);
            }
        });

        //Mouse
        glfwSetCursorPosCallback(Main.instance.window, new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xPos, double yPos) {
                Mouse mouse = new Mouse(((float) xPos - Main.getGameWidth() / 2f) / Main.getGameWidth(), ((float) yPos - Main.getGameHeight() / 2f) / Main.getGameHeight());

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