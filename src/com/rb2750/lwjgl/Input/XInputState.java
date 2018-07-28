package com.rb2750.lwjgl.Input;

import com.ivan.xinput.XInputAxes;
import com.ivan.xinput.enums.XInputButton;
import lombok.Getter;

public class XInputState
{
    public static boolean[] buttons = new boolean[XInputButton.values().length];
    private static boolean[] prevButtons = new boolean[XInputButton.values().length];

    @Getter
    private static XInputAxesCopy axes;
    private static XInputAxesCopy prevAxes;

    public static void setButton(XInputButton button, boolean pressed)
    {
        if (pressed)
        {
            Input.currentInputMode = InputMode.XINPUT_CONTROLLER;
        }

        buttons[button.ordinal()] = pressed;
    }

    public static void update()
    {
        for (int i = 0; i < buttons.length; i++)
        {
            prevButtons[i] = buttons[i];
        }

        if (axes.lx != prevAxes.lx || axes.ly != prevAxes.ly || axes.rx != prevAxes.rx || axes.ry != prevAxes.ry)
        {
            prevAxes.lx = axes.lx;
            prevAxes.ly = axes.ly;
            prevAxes.rx = axes.rx;
            prevAxes.ry = axes.ry;

            Input.currentInputMode = InputMode.XINPUT_CONTROLLER;
        }
    }

    public static void setAxes(XInputAxes newAxes, float leftStickDeadzone, float rightStickDeadzone)
    {
        if (axes == null)
        {
            axes = new XInputAxesCopy(newAxes);
            prevAxes = new XInputAxesCopy(newAxes);
        }

        if ((newAxes.lx > leftStickDeadzone || newAxes.lx < -leftStickDeadzone) || (newAxes.ly > leftStickDeadzone || newAxes.ly < -leftStickDeadzone))
        {
            axes.lx = newAxes.lx;
            axes.ly = newAxes.ly;
        }
        else
        {
            axes.lx = 0.0f;
            axes.ly = 0.0f;
        }

        if ((newAxes.rx > rightStickDeadzone || newAxes.rx < -rightStickDeadzone) || (newAxes.ry > rightStickDeadzone || newAxes.ry < -rightStickDeadzone))
        {
            axes.rx = newAxes.rx;
            axes.ry = newAxes.ry;
        }
        else
        {
            axes.rx = 0.0f;
            axes.ry = 0.0f;
        }
    }

    public static boolean getFromCurrent(XInputButton button)
    {
        return buttons[button.ordinal()];
    }

    public static boolean getFromPrevious(XInputButton button)
    {
        return buttons[button.ordinal()];
    }

    public static boolean isButtonDown(XInputButton button)
    {
        return buttons[button.ordinal()];
    }

    public static boolean isButtonPressed(XInputButton button)
    {
        return (buttons[button.ordinal()] && !prevButtons[button.ordinal()]);
    }

    public static boolean isButtonUp(XInputButton button)
    {
        return !buttons[button.ordinal()];
    }

    public static boolean isButtonReleased(XInputButton button)
    {
        return (!buttons[button.ordinal()] && prevButtons[button.ordinal()]);
    }
}
