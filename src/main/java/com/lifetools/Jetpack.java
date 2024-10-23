package com.lifetools;

import com.lifetools.annotations.Feature;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager; // Use ClientCommandManager for client-side commands

import static com.lifetools.LifeTools.INFO_PREFIX;

public class Jetpack implements ClientModInitializer {

    private final MinecraftClient mc = MinecraftClient.getInstance();
    public static boolean airJumpEnabled = false; // Toggle for air jumping

    @Override
    public void onInitializeClient() {
        // Register the /jetpack command as client-side
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                ClientCommandManager.literal("jetpack")
                        .executes(context -> {
                            // Call the method to toggle air jump functionality
                            toggleAirJump(); // Call the annotated method
                            return 1;
                        })
        ));

        // Register a client tick event to check for key presses
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!airJumpEnabled || mc.player == null || mc.player.isOnGround() || mc.currentScreen != null) return;

            // Check if the jump key is pressed
            if (mc.options.jumpKey.isPressed()) {
                // Trigger the jump action
                mc.player.jump();
            }
        });
    }

    @Feature(methodName = "toggleAirJump", actionType = "toggle", featureName = "Jetpack", booleanField = "airJumpEnabled")
    public void toggleAirJump() {
        airJumpEnabled = !airJumpEnabled; // Toggle the state

        // Prepare the feedback message based on the new state
        String message = airJumpEnabled ?
                INFO_PREFIX + "Jetpack has been §aenabled" :
                INFO_PREFIX + "Jetpack has been §cdisabled";

        // Send feedback to the player directly
        if (mc.player != null) {
            mc.player.sendMessage(Text.literal(message), false);
        }
    }
}
