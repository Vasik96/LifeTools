package com.lifetools;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.util.HashSet;
import java.util.Set;

import static com.lifetools.LifeTools.INFO_PREFIX;

public class ESP implements ClientModInitializer {

    private static final Set<LivingEntity> glowingEntities = new HashSet<>();
    private static boolean isEspEnabled = false; // Track the state of the ESP feature

    @Override
    public void onInitializeClient() {
        // Register the commands
        registerCommands();
    }

    private void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, dedicated) ->
                dispatcher.register(ClientCommandManager.literal("esp")
                        .executes(this::toggleEspEffect)
                )
        );
    }

    private int toggleEspEffect(CommandContext<FabricClientCommandSource> context) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;

        if (player == null) {
            return 0;
        }

        Text message;
        if (isEspEnabled) {
            disableEspEffect();
            message = Text.literal(INFO_PREFIX + "ESP has been §cdisabled");
        } else {
            enableEspEffect();
            message = Text.literal(INFO_PREFIX + "ESP has been §aenabled");
        }

        context.getSource().sendFeedback(message);
        isEspEnabled = !isEspEnabled;

        return 1;
    }

    private void enableEspEffect() {
        MinecraftClient client = MinecraftClient.getInstance();
        assert client.world != null;

        for (Entity entity : client.world.getEntities()) {
            if (entity instanceof LivingEntity livingEntity && shouldGlow(livingEntity)) {
                livingEntity.setGlowing(true);
                glowingEntities.add(livingEntity);
            }
        }
    }

    private void disableEspEffect() {
        MinecraftClient client = MinecraftClient.getInstance();
        assert client.world != null;

        // Reset glowing state for entities
        for (LivingEntity entity : glowingEntities) {
            entity.setGlowing(false);
        }
        glowingEntities.clear();
    }

    public static boolean shouldGlow(Entity entity) {
        return entity instanceof PlayerEntity || entity instanceof SlimeEntity;
    }

    public static boolean isEspEnabled() {
        return isEspEnabled;
    }
}