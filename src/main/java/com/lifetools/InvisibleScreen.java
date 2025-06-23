package com.lifetools;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class InvisibleScreen extends Screen {
    public InvisibleScreen() {
        super(Text.of("Invisible"));
    }

    @Override
    public void init() {
        // Disable the screen's default behavior, like drawing anything
    }
}
