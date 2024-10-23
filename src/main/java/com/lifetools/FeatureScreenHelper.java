package com.lifetools;

import com.lifetools.screens.FeatureScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class FeatureScreenHelper implements ClientModInitializer {

    private static KeyBinding openFeatureScreenKeybind;

    @Override
    public void onInitializeClient() {
        // Register the keybind
        openFeatureScreenKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Open Feature Screen",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_M, // The key you want to use to open the screen
                "Toggle Features"
        ));

        // Add an event listener to open the pause menu and then the custom screen
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openFeatureScreenKeybind.wasPressed()) {
                // Open pause menu first
                if (client.currentScreen == null) {
                    client.setScreen(new GameMenuScreen(false)); // Open pause menu
                    // Delay the custom screen open by 1 tick
                    client.execute(() -> client.setScreen(new FeatureScreen()));
                }
            }
        });
    }
}
