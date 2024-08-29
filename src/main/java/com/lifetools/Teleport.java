package com.lifetools;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import static com.lifetools.LifeTools.*;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class Teleport implements ClientModInitializer {

    private static KeyBinding tpForwardKey;

    @Override
    public void onInitializeClient() {
        registerCommands();
        tpForwardKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Teleport forward",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                "Teleport"
        ));
        registerKeyBindingHandler();
    }

    private void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(
                literal("tpmod")
                        .executes(this::correctUsage)
                        .then(ClientCommandManager.argument("distance", IntegerArgumentType.integer())
                                .executes(this::onTpmodCommand))
        ));
    }

    private void registerKeyBindingHandler() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (tpForwardKey.wasPressed()) {
                teleportForward();
            }
        });
    }

    private int correctUsage(CommandContext<FabricClientCommandSource> context) {
        context.getSource().sendFeedback(Text.literal(INFO_PREFIX + "Correct usage:\n"
                + "   §7/tpmod <value>"));
        return 1;
    }

    private int onTpmodCommand(CommandContext<FabricClientCommandSource> context) {
        int totalDistance = IntegerArgumentType.getInteger(context, "distance");
        teleportForward(totalDistance, context.getSource());
        return 1;
    }

    private void teleportForward(int totalDistance, FabricClientCommandSource source) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        ClientPlayerEntity player = minecraftClient.player;

        if (player != null) {
            double yaw = Math.toRadians(player.getYaw());
            double pitch = Math.toRadians(player.getPitch());

            // Calculate the direction vector based on yaw and pitch.
            double x = -Math.sin(yaw) * Math.cos(pitch);
            double y = -Math.sin(pitch);
            double z = Math.cos(yaw) * Math.cos(pitch);

            // Ensure the distance is within the teleport limit (1-150 blocks)
            int teleportLimit = 150;

            if (totalDistance >= 1 && totalDistance <= teleportLimit) {
                // Use the TeleportPacket class to handle the teleportation steps
                TeleportPacket.spamPacketsAndTeleport(player, totalDistance, x, y, z);

                // Notify the player that they have teleported.
                Text message = Text.of(INFO_PREFIX + "Teleported§a " + player.getName().getString() + " §7forward §a" + totalDistance + " §7blocks");
                sendMessage(source, player, message);
            } else {
                Text errorMessage = Text.of(WARNING_PREFIX + "§6Teleport distance must be between §a1 §6and §a150 §6blocks");
                sendMessage(source, player, errorMessage);
            }
        } else {
            // Send an error message if the player is not in-game.
            Text errorMessage = Text.of(ERROR_PREFIX + "§cError teleporting, please try again");
            sendMessage(source, null, errorMessage);
        }
    }

    private void sendMessage(FabricClientCommandSource source, ClientPlayerEntity player, Text message) {
        if (source != null) {
            source.sendFeedback(message);
        } else if (player != null) {
            player.sendMessage(message, false);
        }
    }
    private void teleportForward() {
        teleportForward(10, null);
    }
}
