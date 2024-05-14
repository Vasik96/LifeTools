package com.lifetools;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.text.Text;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class NoFall implements ClientModInitializer {

    private static boolean noFallEnabled = false;

    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, dedicated) ->
                registerCommands(dispatcher));

        ClientTickEvents.END_CLIENT_TICK.register(client -> handleNoFall());
    }

    private void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        LiteralArgumentBuilder<FabricClientCommandSource> command = ClientCommandManager.literal("nofall")
                .executes(context -> {
                    toggleNoFall();
                    return 1;
                });

        dispatcher.register(command);
    }

    private void toggleNoFall() {
        noFallEnabled = !noFallEnabled;
        String message = noFallEnabled ? "§8[§2LifeTools§8] §7Fall Damage has been §cdisabled" : "§8[§2LifeTools§8] §7Fall Damage has been §aenabled";
        assert MinecraftClient.getInstance().player != null;
        MinecraftClient.getInstance().player.sendMessage(Text.of(message), false);
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
