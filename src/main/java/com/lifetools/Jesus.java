package com.lifetools;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import static com.lifetools.LifeTools.INFO_PREFIX;

public class Jesus implements ClientModInitializer {

    private static boolean jesusModeEnabled = false;
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private boolean pendingJesusReenable = false;
    private int reenableCooldown = 0;


    @Override
    public void onInitializeClient() {
        // Register the /jesus command
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal("jesus")
                .executes(context -> {
                    toggleJesusMode();
                    return 1;
                })
        ));

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (jesusModeEnabled) {
                jesusModeEnabled = false;  // Disable Jesus mode when switching worlds

                // Set flag to re-enable after the world is loaded
                pendingJesusReenable = true;
                reenableCooldown = 100;  // 5 seconds * 20 ticks (100 ticks total)
            }
        });

        // Register a tick event to handle the cooldown and re-enable Jesus mode
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (pendingJesusReenable && mc.player != null) {
                if (reenableCooldown > 0) {
                    reenableCooldown--;  // Decrease cooldown timer
                } else {
                    jesusModeEnabled = true;  // Re-enable Jesus mode after cooldown
                    pendingJesusReenable = false;  // Reset the flag
                    mc.player.sendMessage(Text.literal("§aJesus mode re-enabled after cooldown"));
                }
            }
        });

    }

    public void toggleJesusMode() {
        jesusModeEnabled = !jesusModeEnabled;
        if (mc.player != null) {
            String status = jesusModeEnabled ? "§aenabled" : "§cdisabled";
            mc.player.sendMessage(Text.literal(INFO_PREFIX + "Jesus mode has been " + status), false);
        }
    }



    public static boolean isJesusModeEnabled() {
        return jesusModeEnabled;
    }
}
