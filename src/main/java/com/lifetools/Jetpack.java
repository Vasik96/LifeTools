package com.lifetools;

import com.lifetools.annotations.Feature;
import com.lifetools.commandsystem.LifeToolsCmd;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import static com.lifetools.LifeTools.INFO_PREFIX;

public class Jetpack implements ClientModInitializer {

    private final MinecraftClient mc = MinecraftClient.getInstance();
    public static boolean JetpackEnabled = false; // Toggle for jetpack

    @Override
    public void onInitializeClient() {
        // Register the /jetpack command as client-side
        LifeToolsCmd.addCmd("jetpack", args -> {
            toggleJetpack();
        });

        // Register a client tick event to check for key presses
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!JetpackEnabled || mc.player == null || mc.player.isOnGround() || mc.currentScreen != null) return;

            // Check if the jump key is pressed
            if (mc.options.jumpKey.isPressed()) {
                // Trigger the jump action
                mc.player.jump();
            }
        });
    }

    @Feature(methodName = "toggleJetpack", actionType = "toggle", featureName = "Jetpack", booleanField = "JetpackEnabled")
    public void toggleJetpack() {
        JetpackEnabled = !JetpackEnabled; // Toggle the state

        // Prepare the feedback message based on the new state
        String message = JetpackEnabled ?
                INFO_PREFIX + "Jetpack has been §aenabled" :
                INFO_PREFIX + "Jetpack has been §cdisabled";

        // Send feedback to the player directly
        if (mc.player != null) {
            mc.player.sendMessage(Text.literal(message), false);
        }
    }
}
