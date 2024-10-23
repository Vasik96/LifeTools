package com.lifetools;

import com.lifetools.annotations.Feature; // Ensure you have the correct import for your annotation
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import static com.lifetools.LifeTools.INFO_PREFIX;
import static com.lifetools.RendererInfo.currentRenderer;

public class Xray implements ClientModInitializer {
    public static boolean xrayEnabled = false; // X-ray mode
    public static final KeyBinding TOGGLE_XRAY_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "Toggle X-Ray",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_X,
            "X-Ray"
    ));

    @Override
    public void onInitializeClient() {
        // Register a tick event listener to handle key press
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (TOGGLE_XRAY_KEY.wasPressed()) {
                toggleXray();
            }
        });
    }

    @Feature(methodName = "toggleXray", actionType = "toggle", featureName = "X-Ray", booleanField = "xrayEnabled")
    public static void toggleXray() {
        // Toggle the X-ray mode
        xrayEnabled = !xrayEnabled;

        // Send a chat message to the player
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.sendMessage(
                    Text.of(INFO_PREFIX + "§8[" + RendererInfo.currentRenderer + "§8]§7 X-ray has been " +
                            (xrayEnabled ? "§aenabled" : "§cdisabled")),
                    false
            );

            // Reload chunks to apply X-ray effect
            client.worldRenderer.reload();
        }
    }
}
