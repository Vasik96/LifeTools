package com.lifetools;

import com.lifetools.annotations.Feature;
import com.lifetools.commandsystem.LifeToolsCmd;
import net.fabricmc.api.ClientModInitializer;
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
        registerCommands();

        // Continuously update the player's reach on client ticks
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (MinecraftClient.getInstance().player != null && reachToggled) {
                // Set the player's interaction range to the toggled value
                Objects.requireNonNull(MinecraftClient.getInstance().player.getAttributeInstance(EntityAttributes.ENTITY_INTERACTION_RANGE))
                        .setBaseValue(TOGGLED_REACH);
            }
        });
    }

    private void registerCommands() {
        // Register the /reach command using custom CommandSystem
        LifeToolsCmd.addCmd("reach", args -> {
            toggleReach();
        });
    }

    @Feature(methodName = "toggleReach", actionType = "toggle", featureName = "Reach", booleanField = "reachToggled")
    private void toggleReach() {
        MinecraftClient client = MinecraftClient.getInstance(); // Get the client instance
        assert client.player != null; // Ensure player instance is not null

        reachToggled = !reachToggled; // Toggle the reach state

        if (reachToggled) {
            // Set the player's interaction range to the toggled value
            Objects.requireNonNull(client.player.getAttributeInstance(EntityAttributes.ENTITY_INTERACTION_RANGE))
                    .setBaseValue(TOGGLED_REACH);
            client.player.sendMessage(Text.of(INFO_PREFIX + "Reach has been §aenabled§7 - Note that this feature is limited to the server's configuration"), false);
        } else {
            // Reset the player's interaction range to the default value
            Objects.requireNonNull(client.player.getAttributeInstance(EntityAttributes.ENTITY_INTERACTION_RANGE))
                    .setBaseValue(DEFAULT_REACH);
            client.player.sendMessage(Text.of(INFO_PREFIX + "Reach has been §cdisabled"), false);
        }
    }

    public double getCurrentReach() {
        return reachToggled ? TOGGLED_REACH : DEFAULT_REACH; // Return the current reach value
    }
}
