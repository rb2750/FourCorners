package com.rb2750.lwjgl.input;

import com.ivan.xinput.XInputAxes;
import com.ivan.xinput.enums.XInputButton;

public class XInputState
{
    public static boolean[] buttons = new boolean[XInputButton.values().length];
    private static boolean[] prevButtons = new boolean[XInputButton.values().length];
    public static XInputAxes axes;

    public static void setButton(XInputButton button, boolean pressed)
    {
        buttons[button.ordinal()] = pressed;
    }

    public static void update()
    {
        for (int i = 0; i < buttons.length; i++)
        {
            prevButtons[i] = buttons[i];
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
