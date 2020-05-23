package com.rb2750.lwjgl.input;

import com.ivan.xinput.XInputAxes;
import com.ivan.xinput.enums.XInputButton;
import lombok.Getter;

public class XInputState {
    public static boolean[] buttons = new boolean[XInputButton.values().length];
    private static boolean[] prevButtons = new boolean[XInputButton.values().length];

    @Getter
    private static InputAxes axes;
    @Getter
    private static InputAxes prevAxes;

    public static void setButton(XInputButton button, boolean pressed) {
        buttons[button.ordinal()] = pressed;
    }

    public static void update() {
        for (int i = 0; i < buttons.length; i++) {
            prevButtons[i] = buttons[i];
        }

        if (axes.lx != prevAxes.lx || axes.ly != prevAxes.ly || axes.rx != prevAxes.rx || axes.ry != prevAxes.ry) {
            prevAxes.lx = axes.lx;
            prevAxes.ly = axes.ly;
            prevAxes.rx = axes.rx;
            prevAxes.ry = axes.ry;
        }
    }

    public static void setAxes(XInputAxes newAxes, float leftStickDeadzone, float rightStickDeadzone) {
        if (axes == null) {
            axes = new InputAxes(newAxes);
            prevAxes = new InputAxes(newAxes);
        }

        if ((newAxes.lx > leftStickDeadzone || newAxes.lx < -leftStickDeadzone) || (newAxes.ly > leftStickDeadzone || newAxes.ly < -leftStickDeadzone)) {
            axes.lx = newAxes.lx;
            axes.ly = newAxes.ly;
        } else {
            axes.lx = 0.0f;
            axes.ly = 0.0f;
        }

        if ((newAxes.rx > rightStickDeadzone || newAxes.rx < -rightStickDeadzone) || (newAxes.ry > rightStickDeadzone || newAxes.ry < -rightStickDeadzone)) {
            axes.rx = newAxes.rx;
            axes.ry = newAxes.ry;
        } else {
            axes.rx = 0.0f;
            axes.ry = 0.0f;
        }

        axes.rt = newAxes.rtRaw;
        axes.rt = newAxes.rt;
        axes.lt = newAxes.lt;
        axes.ltRaw = newAxes.ltRaw;
    }

    public static boolean getFromCurrent(XInputButton button) {
        return buttons[button.ordinal()];
    }

    public static boolean getFromPrevious(XInputButton button) {
        return prevButtons[button.ordinal()];
    }

    public static boolean isButtonDown(XInputButton button) {
        return buttons[button.ordinal()];
    }

    public static boolean isButtonPressed(XInputButton button) {
        return (buttons[button.ordinal()] && !prevButtons[button.ordinal()]);
    }

    public static boolean isButtonUp(XInputButton button) {
        return !buttons[button.ordinal()];
    }

    public static boolean isButtonReleased(XInputButton button) {
        return (!buttons[button.ordinal()] && prevButtons[button.ordinal()]);
    }
}
