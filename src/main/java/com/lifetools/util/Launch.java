package com.lifetools.util;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import static com.lifetools.LifeTools.INFO_PREFIX;

public class Launch {

    private static final MinecraftClient client = MinecraftClient.getInstance();

    public void handleLaunch(FabricClientCommandSource source) {
        if (client.player != null) {
            PlayerEntity player = client.player;

            // Define the fixed launch distance
            double forwardDistance = 3.0; // Forward distance in blocks
            double upwardDistance = 1.2;  // Upward distance in blocks

            // Calculate the motion based on fixed distances
            double xMotion = player.getRotationVector().x * forwardDistance;
            double zMotion = player.getRotationVector().z * forwardDistance;

            // Apply the motion to the player
            player.setVelocity(xMotion, upwardDistance, zMotion);
            player.sendMessage(Text.literal(INFO_PREFIX + "Launched!"), false);
        } else {
            source.sendFeedback(Text.literal(INFO_PREFIX + "Player not found."));
        }
    }
}
