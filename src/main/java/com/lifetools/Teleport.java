package com.lifetools;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

import static com.lifetools.LifeTools.*;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class Teleport implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        registerCommands();
    }

    private void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(
                literal("tpmod")
                        .executes(this::correctUsage)
                        .then(ClientCommandManager.argument("distance", IntegerArgumentType.integer())
                                .executes(this::onTpmodCommand))
        ));
    }


    private int correctUsage(CommandContext<FabricClientCommandSource> context) {
        context.getSource().sendFeedback(Text.literal(INFO_PREFIX + "Correct usage:\n"
                + "   §7/tpmod <value>"));
        return 1;
    }

    private int onTpmodCommand(CommandContext<FabricClientCommandSource> context) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        ClientPlayerEntity player = minecraftClient.player;

        if (player != null) {
            int totalDistance = IntegerArgumentType.getInteger(context, "distance");

            double yaw = Math.toRadians(player.getYaw());
            double pitch = Math.toRadians(player.getPitch());

            // Calculate the direction vector based on yaw and pitch.
            double x = -Math.sin(yaw) * Math.cos(pitch);
            double y = -Math.sin(pitch);
            double z = Math.cos(yaw) * Math.cos(pitch);

            // Ensure the distance is within the new teleport limit (1-150 blocks)
            int teleportLimit = 150;

            if (totalDistance >= 1 && totalDistance <= teleportLimit) {
                // Use the TeleportPacket class to handle the teleportation steps
                TeleportPacket.spamPacketsAndTeleport(player, totalDistance, x, y, z);

                // Notify the player that they have teleported.
                Text message = Text.of(INFO_PREFIX + "Teleported§a " + player.getName().getString() + " §7forward §a" + totalDistance + " §7blocks");
                context.getSource().sendFeedback(message);
            } else {
                Text errorMessage = Text.of(WARNING_PREFIX + "§6Teleport distance must be between §a1 §6and §a150 §6blocks");
                context.getSource().sendError(errorMessage);
            }

            return 1;
        } else {
            // Send an error message if the player is not in-game.
            Text errorMessage = Text.of(ERROR_PREFIX + "§cError teleporting, please try again");
            context.getSource().sendError(errorMessage);
            return 0;
        }
    }
}
