package com.lifetools;

import com.lifetools.annotations.Feature;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class NoFall implements ClientModInitializer {

    public static boolean noFallEnabled = false;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> handleNoFall());

        // Register the command
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> registerCommands(dispatcher));
    }

    private void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        LiteralArgumentBuilder<FabricClientCommandSource> command = ClientCommandManager.literal("nofall")
                .executes(context -> {
                    toggleNoFall(); // Call the toggle method without arguments
                    return SINGLE_SUCCESS; // Return 1 for success
                });

        dispatcher.register(command);
    }

    @Feature(methodName = "toggleNoFall", actionType = "toggle", featureName = "No Fall", booleanField = "noFallEnabled")
    public void toggleNoFall() {
        noFallEnabled = !noFallEnabled; // Toggle the state

        // Update the message based on the new state
        String message = noFallEnabled ?
                LifeTools.INFO_PREFIX + "Fall Damage has been §aenabled" : // ON state message
                LifeTools.INFO_PREFIX + "Fall Damage has been §cdisabled"; // OFF state message

        // Send the message to the player
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            player.sendMessage(Text.of(message), false);
        }
    }



    private void handleNoFall() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (noFallEnabled && client.player != null && client.player.fallDistance > 2.0F) {
            ClientPlayerEntity player = client.player;
            player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true));
            player.fallDistance = 0;
        }
    }
}
