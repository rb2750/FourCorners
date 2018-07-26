package com.rb2750.lwjgl.Input;

import com.rb2750.lwjgl.util.Location;
import se.albin.steamcontroller.SteamController;

import java.util.HashMap;

public class Input {
    public static SteamController state;
    public static SteamController last;

    public static HashMap<Action, Button> ButtonMap;
    public static Location Analog_Stick;

    public static void Setup() {
        ButtonMap = new HashMap<>();
        for (Action a : Action.values()) {
            ButtonMap.put(a, new Button(false, false));
        }
        Analog_Stick = new Location( null, 0,0);
    }


    public static void updateSteamController(SteamController state, SteamController last) {
        Input.state = state;
        Input.last = last;

        Analog_Stick.set(state.getAnalogStickPosition().x(),state.getAnalogStickPosition().y());

        ButtonMap.get(Action.Jump).Set(state.isAHeld(), last.isAHeld());
        ButtonMap.get(Action.Clear).Set(state.isBHeld(), last.isBHeld());
        ButtonMap.get(Action.Squat).Set(state.isXHeld(), last.isXHeld());
        ButtonMap.get(Action.PlaceBlock).Set(state.isRightPadPressed(), last.isRightPadPressed());
        ButtonMap.get(Action.ShowBlock).Set(state.isRightPadTouched(), last.isRightPadTouched());
        ButtonMap.get(Action.Home).Set(state.isHomeHeld(), last.isHomeHeld());
        ButtonMap.get(Action.ShowGUI).Set(state.isLeftPadTouched(), last.isLeftPadTouched());
    }
}
