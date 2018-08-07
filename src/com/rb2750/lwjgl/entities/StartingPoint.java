package com.rb2750.lwjgl.entities;

import com.rb2750.lwjgl.Main;
import com.rb2750.lwjgl.gui.fonts.fontcreator.GUIText;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class StartingPoint extends Tile {
    private GUIText text;

    public StartingPoint() {
        super();
//        text = new GUIText("SP", 3, Main.instance.getFont(), new Vector2f(0.0f, 0.0f), size.getWidth() / Main.getGameWidth(), true);
        setBaseColour(new Vector4f(247, 160, 9, 100));
        setCanInteract(false);
    }

    @Override
    public void update() {
        super.update();
//        text.setPosition(new Vector2f(location.getX(), Main.getGameHeight() - location.getY() - size.getHeight()).mul(1f / Main.getGameWidth(), 1f / Main.getGameHeight()));
    }
}
