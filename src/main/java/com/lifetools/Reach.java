package com.lifetools;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.text.Text;

import java.util.Objects;

import static com.lifetools.LifeTools.INFO_PREFIX;

public class Reach implements ClientModInitializer {

    public static boolean reachToggled = false;
    private static final double DEFAULT_REACH = 3.0; // Default player interaction reach
    private static final double TOGGLED_REACH = 12.0; // Toggled reach value

    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(ClientCommandManager.literal("reach")
                .executes(this::toggleReach)
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (MinecraftClient.getInstance().player != null && reachToggled) {
                // Continuously set the player's interaction range
                Objects.requireNonNull(MinecraftClient.getInstance().player.getAttributeInstance(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE)).setBaseValue(TOGGLED_REACH);
            }
        });
    }

    private int toggleReach(CommandContext<FabricClientCommandSource> context) {
        assert MinecraftClient.getInstance().player != null;

        reachToggled = !reachToggled;

        if (reachToggled) {
            // Set the player's interaction range to the toggled value
            Objects.requireNonNull(MinecraftClient.getInstance().player.getAttributeInstance(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE)).setBaseValue(TOGGLED_REACH);
            MinecraftClient.getInstance().player.sendMessage(Text.of(INFO_PREFIX + "Reach has been §aenabled§7 - Note that this feature is limited to the server's configuration"), false);
        } else {
            // Reset the player's interaction range to the default value
            Objects.requireNonNull(MinecraftClient.getInstance().player.getAttributeInstance(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE)).setBaseValue(DEFAULT_REACH);
            MinecraftClient.getInstance().player.sendMessage(Text.of(INFO_PREFIX + "Reach has been §cdisabled"), false);
        }

        return 1;
    }

    public double getCurrentReach() {
        return reachToggled ? TOGGLED_REACH : DEFAULT_REACH;
    }
}
