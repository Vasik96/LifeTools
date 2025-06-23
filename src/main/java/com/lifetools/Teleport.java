package com.lifetools;

import com.lifetools.commandsystem.LifeToolsCmd;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import static com.lifetools.LifeTools.*;

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
        LifeToolsCmd.addCmd("tpmod", args -> {
            if (args.length == 0) {
                // Call correctUsage() method with the context (fabric source)
                correctUsage();  // or handle the feedback directly here
            } else if (args.length == 1) {
                try {
                    // Parse the distance argument and call onTpmodCommand
                    int distance = Integer.parseInt(args[0]);
                    onTpmodCommand(distance);
                } catch (NumberFormatException e) {
                    sendErrorMessage("§6Invalid distance. Please enter a valid integer.");
                }
            } else {
                sendErrorMessage("§6Usage: !tpmod or !tpmod <distance>");
            }
        });
    }

    private void sendErrorMessage(String message) {
        MinecraftClient.getInstance().player.sendMessage(Text.of(WARNING_PREFIX + message), false);
    }

    private void registerKeyBindingHandler() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (tpForwardKey.wasPressed()) {
                teleportForward();
            }
        });
    }

    private void correctUsage() {
        MinecraftClient.getInstance().player.sendMessage(Text.literal(INFO_PREFIX + "Correct usage: §7/tpmod <value>"), false);
    }

    private void onTpmodCommand(int totalDistance) {
        teleportForward(totalDistance);
    }

    public static void executeTeleportForward(int tpDistance) {
        Teleport teleport = new Teleport();
        teleport.teleportForward(tpDistance);
    }



    private void teleportForward(int totalDistance) {
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
                sendMessage(message);
            } else {
                Text errorMessage = Text.of(WARNING_PREFIX + "§6Teleport distance must be between §a1 §6and §a150 §6blocks");
                sendMessage(errorMessage);
            }
        } else {
            // Send an error message if the player is not in-game.
            Text errorMessage = Text.of(ERROR_PREFIX + "§cError teleporting, please try again");
            sendMessage(errorMessage);
        }
    }


    private void sendMessage(Text message) {
        MinecraftClient.getInstance().player.sendMessage(message, false);
    }

    private void teleportForward() {
        teleportForward(10);
    }
}
