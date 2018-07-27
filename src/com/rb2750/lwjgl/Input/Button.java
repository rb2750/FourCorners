package com.rb2750.lwjgl.Input;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Button {
    public boolean state;
    public boolean last;

    public void Set(boolean state, boolean last) {
        this.state = state;
        this.last = last;
    }

    public void Set(Button input) {
        this.state = input.state;
        this.last = input.last;
    }
}

