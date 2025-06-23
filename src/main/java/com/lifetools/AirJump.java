package com.lifetools;

import com.lifetools.annotations.Feature;
import com.lifetools.commandsystem.LifeToolsCmd;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import static com.lifetools.LifeTools.INFO_PREFIX;

public class AirJump implements ClientModInitializer {

    private final MinecraftClient mc = MinecraftClient.getInstance();
    public static boolean airJumpEnabled = false; // Toggle for air jumping
    private boolean jumpKeyPreviouslyPressed = false; // Track previous state of jump key
    private boolean wasOnGroundLastTick = false; // Track whether player was on the ground in the previous tick
    private boolean wasOnGroundTwoTicksAgo = false; // Track whether player was on the ground two ticks ago

    @Override
    public void onInitializeClient() {
        // Register the airjump command using the custom command system
        LifeToolsCmd.addCmd("airjump", args -> toggleAirJump());

        // Register a client tick event to check for key presses
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!airJumpEnabled || mc.player == null || mc.currentScreen != null) return;

            // Check if the jump key is pressed and wasn't pressed in the last tick
            if (mc.options.jumpKey.isPressed() && !jumpKeyPreviouslyPressed) {
                // Trigger jump if player has not been on the ground for the last two ticks
                if (!mc.player.isOnGround() && !wasOnGroundLastTick && !wasOnGroundTwoTicksAgo) {
                    // Perform the jump (vanilla behavior)
                    mc.player.jump();
                }
            }

            // Update jump key state for the next tick
            jumpKeyPreviouslyPressed = mc.options.jumpKey.isPressed();

            // Update the ground state tracking for the next two ticks
            wasOnGroundTwoTicksAgo = wasOnGroundLastTick;
            wasOnGroundLastTick = mc.player.isOnGround();
        });
    }

    @Feature(methodName = "toggleAirJump", actionType = "toggle", featureName = "AirJump", booleanField = "airJumpEnabled")
    public void toggleAirJump() {
        airJumpEnabled = !airJumpEnabled; // Toggle the state

        // Prepare the feedback message based on the new state
        String message = airJumpEnabled ?
                INFO_PREFIX + "Airjump has been §aenabled" :
                INFO_PREFIX + "Airjump has been §cdisabled";

        // Send feedback to the player directly
        if (mc.player != null) {
            mc.player.sendMessage(Text.literal(message), false);
        }
    }
}
