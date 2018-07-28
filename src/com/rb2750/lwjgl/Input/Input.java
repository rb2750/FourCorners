package com.rb2750.lwjgl.Input;

import com.ivan.xinput.XInputDevice;
import com.ivan.xinput.enums.XInputButton;
import com.rb2750.lwjgl.Main;
import com.rb2750.lwjgl.util.Location;
import se.albin.steamcontroller.SteamController;

import static org.lwjgl.glfw.GLFW.*;

import java.util.HashMap;

public class Input {
    public static SteamController state;
    public static SteamController last;

    public static InputMethod inputMethod;

    public static HashMap<Action, Button> ButtonMap;
    public static Location Left_Analog_Stick;
    public static Location Right_Analog_Stick;
    public static double Left_Trigger;
    public static double Right_Trigger;

    public static void Setup(InputMethod inputMethod) {
        Input.inputMethod = inputMethod;
        ButtonMap = new HashMap<>();

        for (Action a : Action.values()) {
            ButtonMap.put(a, new Button(false, false));
        }

        Left_Analog_Stick = new Location( null, 0,0);
        Right_Analog_Stick = new Location(null, 0, 0);
        Left_Trigger = 0.0f;
        Right_Trigger = 0.0f;
    }

    public static void updateSteamController(SteamController state, SteamController last) {
        Input.state = state;
        Input.last = last;

        Left_Analog_Stick.set(state.getAnalogStickPosition().x(),state.getAnalogStickPosition().y());
        Right_Analog_Stick.set(state.getRightTouchPosition().x(),state.getRightTouchPosition().y());
        Left_Trigger = state.getLeftTrigger();
        Right_Trigger = state.getRightTrigger();

        ButtonMap.get(Action.Jump).Set(state.isAHeld(), last.isAHeld());
        ButtonMap.get(Action.Clear).Set(state.isBHeld(), last.isBHeld());
        ButtonMap.get(Action.Squat).Set(state.isXHeld(), last.isXHeld());
        ButtonMap.get(Action.PlaceBlock).Set(state.isRightPadPressed(), last.isRightPadPressed());
        ButtonMap.get(Action.ShowBlock).Set(state.isRightPadTouched(), last.isRightPadTouched());
        ButtonMap.get(Action.Home).Set(state.isHomeHeld(), last.isHomeHeld());
        ButtonMap.get(Action.ShowGUI).Set(state.isLeftPadTouched(), last.isLeftPadTouched());
    }

    public static void updateXInputController()
    {
        Left_Analog_Stick.set(XInputState.axes.lx, XInputState.axes.ly);
        Right_Analog_Stick.set(XInputState.axes.rx, XInputState.axes.ry);
        Left_Trigger = XInputState.axes.lt;
        Right_Trigger = XInputState.axes.rt;

        ButtonMap.get(Action.Jump).Set(XInputState.getFromCurrent(XInputButton.A), XInputState.getFromPrevious(XInputButton.A));
        ButtonMap.get(Action.Clear).Set(XInputState.getFromCurrent(XInputButton.B), XInputState.getFromPrevious(XInputButton.B));
        ButtonMap.get(Action.Squat).Set(XInputState.getFromCurrent(XInputButton.X), XInputState.getFromPrevious(XInputButton.X));
        ButtonMap.get(Action.PlaceBlock).Set(XInputState.getFromCurrent(XInputButton.RIGHT_SHOULDER), XInputState.getFromPrevious(XInputButton.RIGHT_SHOULDER));
        ButtonMap.get(Action.ShowBlock).Set(XInputState.getFromCurrent(XInputButton.RIGHT_THUMBSTICK), XInputState.getFromPrevious(XInputButton.RIGHT_THUMBSTICK));
        ButtonMap.get(Action.Home).Set(XInputState.getFromCurrent(XInputButton.START), XInputState.getFromPrevious(XInputButton.START));
        ButtonMap.get(Action.ShowGUI).Set(XInputState.getFromCurrent(XInputButton.LEFT_THUMBSTICK), XInputState.getFromPrevious(XInputButton.LEFT_THUMBSTICK));
    }

    public static void updateKeyboard()
    {
        // I don't know if you want XInput to use the same button map and analog stick variables,
        // so I've get them together for now.

        ButtonMap.get(Action.Jump).Set(KeyboardHandler.isKeyDown(GLFW_KEY_SPACE));
        ButtonMap.get(Action.Clear).Set(KeyboardHandler.isKeyDown(GLFW_KEY_C));
        ButtonMap.get(Action.Squat).Set(KeyboardHandler.isKeyDown(GLFW_KEY_LEFT_SHIFT));
        ButtonMap.get(Action.ShowBlock).Set(KeyboardHandler.isKeyDown(GLFW_KEY_Q));
        ButtonMap.get(Action.PlaceBlock).Set(KeyboardHandler.isKeyDown(GLFW_KEY_E));

        Left_Analog_Stick.set((KeyboardHandler.isKeyDown(GLFW_KEY_D).state?1:0) - (KeyboardHandler.isKeyDown(GLFW_KEY_A).state?1:0), 0);
    }

    private static float mouseSensitivity = 4f;
    public static void updateMouse(double x, double y)
    {
        x -= Main.getGameWidth() / 2;
        y -= Main.getGameHeight() / 2;
        x /= Main.getGameWidth();
        y /= Main.getGameHeight();

        Right_Analog_Stick.set(x * mouseSensitivity, y * mouseSensitivity);
    }
}
