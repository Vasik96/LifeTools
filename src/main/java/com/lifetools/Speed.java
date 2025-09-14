package com.lifetools;

import com.lifetools.commandsystem.LifeToolsCmd;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.text.Text;

import java.util.Objects;

import static com.lifetools.LifeTools.INFO_PREFIX;
import static com.lifetools.LifeTools.WARNING_PREFIX;

public class Speed implements ClientModInitializer {
    private float scaledSpeed = 0.1f; // Default speed
    public static boolean speedChanged = false; // Flag to check if speed was changed
    private float storedSpeed = 0.1f; // To store the player's original speed

    @Override
    public void onInitializeClient() {
        // Register the custom command
        LifeToolsCmd.addCmd("speed", args -> {
            if (args.length == 1 && "reset".equalsIgnoreCase(args[0])) {
                resetSpeed();
            } else if (args.length == 1) {
                try {
                    float newSpeed = Float.parseFloat(args[0]);
                    executeSpeed(newSpeed);
                } catch (NumberFormatException e) {
                    sendErrorMessage("§6Invalid speed value. Please enter a number.");
                }
            } else {
                sendErrorMessage("§6Usage: !speed <value> or !speed reset");
            }
        });

        // Continuously set the player's speed if it was changed
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (MinecraftClient.getInstance().player != null && speedChanged) {
                Objects.requireNonNull(MinecraftClient.getInstance().player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED)).setBaseValue(scaledSpeed);
            }
        });
    }

    private void executeSpeed(float newSpeed) {
        if (newSpeed < 1 || newSpeed > 100) {
            sendErrorMessage("§6Speed must be between §a1 §6and §a80");
            return;
        }

        assert MinecraftClient.getInstance().player != null;

        if (!speedChanged) {
            // Store the player's original speed before any changes
            storedSpeed = (float) Objects.requireNonNull(MinecraftClient.getInstance().player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED)).getBaseValue();
        }

        scaledSpeed = newSpeed / 20.0f * 0.95f + 0.05f;

        // Set the player's movement speed attributes
        Objects.requireNonNull(MinecraftClient.getInstance().player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED)).setBaseValue(scaledSpeed);

        sendInfoMessage("Walking speed has been set to §a" + newSpeed);
        speedChanged = true;
    }

    private void resetSpeed() {
        assert MinecraftClient.getInstance().player != null;

        // Reset to the original stored speed
        Objects.requireNonNull(MinecraftClient.getInstance().player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED)).setBaseValue(storedSpeed);
        speedChanged = false;

        sendInfoMessage("Walking speed has been reset");
    }

    private void sendInfoMessage(String message) {
        MinecraftClient.getInstance().player.sendMessage(Text.of(INFO_PREFIX + message), false);
    }

    private void sendErrorMessage(String message) {
        MinecraftClient.getInstance().player.sendMessage(Text.of(WARNING_PREFIX + message), false);
    }
}
