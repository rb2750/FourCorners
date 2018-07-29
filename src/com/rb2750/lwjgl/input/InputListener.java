package com.rb2750.lwjgl.input;

import com.rb2750.lwjgl.input.controllers.*;

public interface InputListener {
    void handleControllerInput(Controller state, Controller last);

    void handleKeyboardInput(Keyboard keyboard);

    void handleMouseInput(Mouse mouse);
}
