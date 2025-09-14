package com.lifetools;

import com.lifetools.annotations.Feature;
import com.lifetools.commandsystem.LifeToolsCmd;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.text.Text;

public class NoFall implements ClientModInitializer {

    public static boolean noFallEnabled = false;

    @Override
    public void onInitializeClient() {
        // Register the /nofall command using the custom command system
        registerCommands();

        // Continuously check for fall damage and prevent it if toggled on
        ServerTickEvents.START_SERVER_TICK.register(client -> handleNoFall());
        ClientTickEvents.END_CLIENT_TICK.register(client -> handleNoFall());
    }

    private void registerCommands() {
        // Register the /nofall command using custom CommandSystem
        LifeToolsCmd.addCmd("nofall", args -> {
            toggleNoFall(); // Toggle the no fall state
        });
    }

    @Feature(methodName = "toggleNoFall", actionType = "toggle", featureName = "No Fall", booleanField = "noFallEnabled")
    public void toggleNoFall() {
        noFallEnabled = !noFallEnabled; // Toggle the noFallEnabled state

        // Update the message based on the new state
        String message = noFallEnabled ?
                LifeTools.INFO_PREFIX + "Fall Damage has been §aenabled" :
                LifeTools.INFO_PREFIX + "Fall Damage has been §cdisabled";

        // Send the message to the player
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            player.sendMessage(Text.of(message), false);
        }
    }

    private void handleNoFall() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (noFallEnabled && client.player != null) {
            // Send the packet to cancel fall damage
            ClientPlayerEntity player = client.player;
            player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true, false));
            player.fallDistance = 0; // Reset fall distance to prevent fall damage
        }
    }
}
