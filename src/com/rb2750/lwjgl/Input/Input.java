package com.rb2750.lwjgl.Input;

import com.ivan.xinput.XInputDevice;
import com.ivan.xinput.enums.XInputButton;
import com.rb2750.lwjgl.util.Location;
import se.albin.steamcontroller.SteamController;

import java.util.HashMap;

public class Input {
    public static SteamController state;
    public static SteamController last;

    public static HashMap<Action, Button> ButtonMap;
    public static Location Left_Analog_Stick;
    public static Location Right_Analog_Stick;

    public static void Setup() {
        ButtonMap = new HashMap<>();

        for (Action a : Action.values()) {
            ButtonMap.put(a, new Button(false, false));
        }

        Left_Analog_Stick = new Location( null, 0,0);
        Right_Analog_Stick = new Location(null, 0, 0);
    }


    public static void updateSteamController(SteamController state, SteamController last) {
        Input.state = state;
        Input.last = last;

        Left_Analog_Stick.set(state.getAnalogStickPosition().x(),state.getAnalogStickPosition().y());
        Right_Analog_Stick.set(state.getRightTouchPosition().x(),state.getRightTouchPosition().y());

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
        // I don't know if you want XInput to use the same button map and analog stick variables,
        // so I've get them together for now.

        Left_Analog_Stick.set(XInputState.axes.lx, XInputState.axes.ly);
        Right_Analog_Stick.set(XInputState.axes.rx, XInputState.axes.ry);

        ButtonMap.get(Action.Jump).Set(XInputState.getFromCurrent(XInputButton.A), XInputState.getFromPrevious(XInputButton.A));
        ButtonMap.get(Action.Clear).Set(XInputState.getFromCurrent(XInputButton.B), XInputState.getFromPrevious(XInputButton.B));
        ButtonMap.get(Action.Squat).Set(XInputState.getFromCurrent(XInputButton.X), XInputState.getFromPrevious(XInputButton.X));
        ButtonMap.get(Action.PlaceBlock).Set(XInputState.getFromCurrent(XInputButton.RIGHT_SHOULDER), XInputState.getFromPrevious(XInputButton.RIGHT_SHOULDER));
        ButtonMap.get(Action.ShowBlock).Set(XInputState.getFromCurrent(XInputButton.RIGHT_THUMBSTICK), XInputState.getFromPrevious(XInputButton.RIGHT_THUMBSTICK));
        ButtonMap.get(Action.Home).Set(XInputState.getFromCurrent(XInputButton.START), XInputState.getFromPrevious(XInputButton.START));
        ButtonMap.get(Action.ShowGUI).Set(XInputState.getFromCurrent(XInputButton.LEFT_THUMBSTICK), XInputState.getFromPrevious(XInputButton.LEFT_THUMBSTICK));
    }
}
