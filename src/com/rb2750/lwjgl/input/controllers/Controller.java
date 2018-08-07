package com.rb2750.lwjgl.input.controllers;

import static com.ivan.xinput.XInputAxes.*;
import com.ivan.xinput.enums.XInputButton;
import com.rb2750.lwjgl.input.XInputState;
import lombok.Getter;
import se.albin.steamcontroller.SteamController;

import java.lang.reflect.Field;

public class Controller {
    @Getter
    private Analog2D analogLeft = new Analog2D();
    @Getter
    private Analog2D analogRight = new Analog2D();
    @Getter
    private Analog2D analogStick = new Analog2D();
    @Getter
    private boolean aHeld;
    @Getter
    private boolean bHeld;
    @Getter
    private boolean xHeld;
    @Getter
    private boolean yHeld;
    @Getter
    private boolean leftButtonHeld;
    @Getter
    private boolean rightButtonHeld;
    @Getter
    private boolean leftTriggerHeld;
    @Getter
    private boolean rightTriggerHeld;
    @Getter
    private boolean leftGripHeld;
    @Getter
    private boolean rightGripHeld;
    @Getter
    private boolean leftCenterHeld;
    @Getter
    private boolean rightCenterHeld;
    @Getter
    private boolean homeHeld;
    @Getter
    private boolean leftPadPressed;
    @Getter
    private boolean rightPadPressed;
    @Getter
    private boolean stickPressed;
    @Getter
    private boolean leftPadTouched;
    @Getter
    private boolean rightPadTouched;
    @Getter
    private double leftTrigger;
    @Getter
    private double rightTrigger;

    public Controller updateSteam(SteamController controller) {
        analogLeft = new Analog2D(controller.getLeftTouchPosition());
        analogRight = new Analog2D(controller.getRightTouchPosition());
        analogStick = new Analog2D(controller.getAnalogStickPosition());
        aHeld = controller.isAHeld();
        bHeld = controller.isBHeld();
        xHeld = controller.isXHeld();
        yHeld = controller.isYHeld();
        leftButtonHeld = controller.isLBHeld();
        rightButtonHeld = controller.isRBHeld();
        leftTriggerHeld = controller.isLTHeld();
        rightTriggerHeld = controller.isRTHeld();
        leftGripHeld = controller.isLGHeld();
        rightGripHeld = controller.isRGHeld();
        leftCenterHeld = controller.isCenterLeftHeld();
        rightCenterHeld = controller.isCenterRightHeld();
        homeHeld = controller.isHomeHeld();
        leftPadPressed = controller.isLeftPadPressed();
        rightPadPressed = controller.isRightPadPressed();
        stickPressed = controller.isAnalogStickPressed();
        leftPadTouched = controller.isLeftPadTouched();
        rightPadTouched = controller.isRightPadTouched();
        leftTrigger = controller.getLeftTrigger();
        rightTrigger = controller.getRightTrigger();

        return this;
    }

    public boolean isKeyDown() {
        boolean keyDown = false;

        for (Field f : getClass().getDeclaredFields()) {
            if (f.getType().equals(boolean.class)) {
                try {
                    if ((boolean) f.get(this)) keyDown = true;
                    break;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return keyDown || isAnalogueTouched();
    }

    public boolean isPadTouched() {
        return isLeftPadTouched() || isRightPadTouched();
    }

    public boolean isAnalogueTouched() {
        return analogLeft.x() != 0 || analogLeft.y() != 0 || analogRight.x() != 0 || analogRight.y() != 0 || analogStick.x() != 0 || analogStick.y() != 0 || leftTrigger != 0 || rightTrigger != 0;
    }

    private Analog2D getDpad(int dpad) {
        int x;
        int y;

        switch (dpad) {
            case DPAD_CENTER:
                x = 0;
                y = 0;
                break;
            case DPAD_UP_LEFT:
                x = -1;
                y = 1;
                break;
            case DPAD_UP:
                x = 0;
                y = 1;
                break;
            case DPAD_UP_RIGHT:
                x = 1;
                y = -1;
                break;
            case DPAD_DOWN_LEFT:
                x = -1;
                y = -1;
                break;
            case DPAD_DOWN:
                x = 0;
                y = -1;
                break;
            case DPAD_DOWN_RIGHT:
                x = 1;
                y = -1;
                break;
            case DPAD_LEFT:
                x = -1;
                y = 0;
                break;
            case DPAD_RIGHT:
                x = 1;
                y = 0;
                break;
            default:
                x = 0;
                y = 0;
                break;
        }

        return new Analog2D(x, y);
    }

    public Controller updateXInput() {
        analogLeft = new Analog2D(XInputState.getAxes().lx, XInputState.getAxes().ly);
        analogRight = new Analog2D(XInputState.getAxes().rx, XInputState.getAxes().ry);
        analogStick = new Analog2D(XInputState.getAxes().lx, XInputState.getAxes().ly);
        aHeld = XInputState.getFromCurrent(XInputButton.A);
        bHeld = XInputState.getFromCurrent(XInputButton.B);
        xHeld = XInputState.getFromCurrent(XInputButton.X);
        yHeld = XInputState.getFromCurrent(XInputButton.Y);
        leftButtonHeld = XInputState.getFromCurrent(XInputButton.LEFT_SHOULDER);
        rightButtonHeld = XInputState.getFromCurrent(XInputButton.RIGHT_SHOULDER);
        leftTriggerHeld = XInputState.getFromCurrent(XInputButton.LEFT_SHOULDER);
        rightTriggerHeld = XInputState.getFromCurrent(XInputButton.RIGHT_SHOULDER);
        leftGripHeld = false;
        rightGripHeld = false;
        leftCenterHeld = XInputState.getAxes().dpad == DPAD_CENTER;
        rightCenterHeld = XInputState.getFromCurrent(XInputButton.RIGHT_THUMBSTICK);
        homeHeld = XInputState.getFromCurrent(XInputButton.GUIDE_BUTTON);
        leftPadPressed = XInputState.getAxes().dpad != DPAD_CENTER;
        rightPadPressed = XInputState.getFromCurrent(XInputButton.RIGHT_THUMBSTICK);
        stickPressed = XInputState.getFromCurrent(XInputButton.LEFT_THUMBSTICK);
        leftPadTouched = XInputState.getAxes().dpad != DPAD_CENTER;
        rightPadTouched = analogRight.x() != 0 || analogRight.y() != 0;
        leftTrigger = XInputState.getAxes().lt;
        rightTrigger = XInputState.getAxes().rt;

        return this;
    }

    public Controller updateLastXInput() {
        analogLeft = getDpad(XInputState.getAxes().dpad);
        analogRight = new Analog2D(XInputState.getAxes().rx, XInputState.getAxes().ry);
        analogStick = new Analog2D(XInputState.getAxes().lx, XInputState.getAxes().ly);
        aHeld = XInputState.getFromPrevious(XInputButton.A);
        bHeld = XInputState.getFromPrevious(XInputButton.B);
        xHeld = XInputState.getFromPrevious(XInputButton.X);
        yHeld = XInputState.getFromPrevious(XInputButton.Y);
        leftButtonHeld = XInputState.getFromPrevious(XInputButton.LEFT_SHOULDER);
        rightButtonHeld = XInputState.getFromPrevious(XInputButton.RIGHT_SHOULDER);
        leftTriggerHeld = XInputState.getFromPrevious(XInputButton.LEFT_SHOULDER);
        rightTriggerHeld = XInputState.getFromPrevious(XInputButton.RIGHT_SHOULDER);
        leftGripHeld = false;
        rightGripHeld = false;
        leftCenterHeld = XInputState.getAxes().dpad == DPAD_CENTER;
        rightCenterHeld = XInputState.getFromPrevious(XInputButton.RIGHT_THUMBSTICK);
        homeHeld = XInputState.getFromPrevious(XInputButton.GUIDE_BUTTON);
        leftPadPressed = XInputState.getAxes().dpad != DPAD_CENTER;
        rightPadPressed = XInputState.getFromPrevious(XInputButton.RIGHT_THUMBSTICK);
        stickPressed = XInputState.getFromPrevious(XInputButton.LEFT_THUMBSTICK);
        leftPadTouched = XInputState.getAxes().dpad != DPAD_CENTER;
        rightPadTouched = analogRight.x() != 0 || analogRight.y() != 0;
        leftTrigger = XInputState.getAxes().lt;
        rightTrigger = XInputState.getAxes().rt;

        return this;
    }
}
