package com.rb2750.lwjgl.input;

import com.ivan.xinput.XInputAxes;

public class InputAxes extends XInputAxes
{
    public InputAxes(XInputAxes copy)
    {
        super();

        lx = copy.lx;
        ly = copy.ly;
        rx = copy.rx;
        ry = copy.ry;

        lxRaw = copy.lxRaw;
        lyRaw = copy.lyRaw;
        rxRaw = copy.rxRaw;
        ryRaw = copy.ryRaw;

        lt = copy.lt;
        rt = copy.rt;

        ltRaw = copy.ltRaw;
        rtRaw = copy.rtRaw;

        dpad = copy.dpad;
    }
}
