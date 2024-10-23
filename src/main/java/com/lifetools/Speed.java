package com.lifetools;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.entity.attribute.EntityAttributes;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import java.util.Objects;

import static com.lifetools.LifeTools.INFO_PREFIX;
import static com.lifetools.LifeTools.WARNING_PREFIX;

public class Speed implements ClientModInitializer {
    private float scaledSpeed = 0.1f; // Default speed
    private boolean speedChanged = false; // Flag to check if speed was changed
    private float storedSpeed = 0.1f; // To store the player's original speed

    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(ClientCommandManager.literal("speed")
                .then(ClientCommandManager.argument("speed", FloatArgumentType.floatArg())
                        .executes(this::executeSpeed))
                .then(ClientCommandManager.literal("reset")
                        .executes(this::resetSpeed))));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (MinecraftClient.getInstance().player != null && speedChanged) {
                // Continuously set the player's speed
                Objects.requireNonNull(MinecraftClient.getInstance().player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(scaledSpeed);
            }
        });
    }

    private int executeSpeed(CommandContext<FabricClientCommandSource> context) {
        float newSpeed = FloatArgumentType.getFloat(context, "speed");

        if (newSpeed < 1 || newSpeed > 30) {
            // Custom error message for out of range speed
            context.getSource().sendError(Text.of(WARNING_PREFIX + "§6Speed must be between §a1 §6and §a30"));
            return 0;
        }

        assert MinecraftClient.getInstance().player != null;

        if (!speedChanged) {
            // Store the player's original speed before any changes are made
            storedSpeed = (float) Objects.requireNonNull(MinecraftClient.getInstance().player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).getBaseValue();
        }

        scaledSpeed = newSpeed / 20.0f * 0.95f + 0.05f;

        // Set the player's movement speed attributes
        Objects.requireNonNull(MinecraftClient.getInstance().player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(scaledSpeed);

        MinecraftClient.getInstance().player.sendMessage(Text.of(INFO_PREFIX + "Walking speed has been set to §a" + newSpeed), false);
        speedChanged = true;
        return 1;
    }

    private int resetSpeed(CommandContext<FabricClientCommandSource> context) {
        resetSpeedToDefault();
        assert MinecraftClient.getInstance().player != null;
        MinecraftClient.getInstance().player.sendMessage(Text.of(INFO_PREFIX + "Walking speed has been reset"), false);
        return 1;
    }

    private void resetSpeedToDefault() {
        // Reset to the original stored speed
        assert MinecraftClient.getInstance().player != null;
        Objects.requireNonNull(MinecraftClient.getInstance().player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(storedSpeed);
        speedChanged = false; // Stop continuously changing the speed
    }
}
