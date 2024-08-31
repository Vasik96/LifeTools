package com.lifetools;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import static com.lifetools.LifeTools.INFO_PREFIX;
import static com.lifetools.util.Fullbright.isFullbright;

public class Xray implements ClientModInitializer {
    public static final String MOD_ID = "xraymod";
    public static final KeyBinding TOGGLE_XRAY_KEY = new KeyBinding(
            "key.xraymod.toggle",
            GLFW.GLFW_KEY_X,
            "category.xraymod"
    );

    private static final RegistryEntry<StatusEffect> NIGHT_VISION_EFFECT = StatusEffects.NIGHT_VISION;

    @Override
    public void onInitializeClient() {
        // Register the keybinding
        KeyBindingHelper.registerKeyBinding(TOGGLE_XRAY_KEY);

        // Register a tick event listener to handle key press
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (TOGGLE_XRAY_KEY.wasPressed()) {
                toggleXray();
            }
        });
    }

    public static void toggleXray() {
        // Toggle the X-ray mode
        XrayConfig.ENABLED = !XrayConfig.ENABLED;

        // Send a chat message to the player
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.sendMessage(
                    Text.of(INFO_PREFIX + "X-ray has been " + (XrayConfig.ENABLED ? "§aenabled" : "§cdisabled")),
                    false
            );


            // Apply or remove the night vision effect
            if (XrayConfig.ENABLED) {
                applyNightVision(client);
            } else {
                removeNightVision(client);
            }

            // Reload chunks to apply X-ray effect
            client.worldRenderer.reload();
        }
    }

    private static void applyNightVision(MinecraftClient client) {
        if (client.player != null) {
            // Apply night vision effect until xray is disabled
            client.player.addStatusEffect(new StatusEffectInstance(NIGHT_VISION_EFFECT, StatusEffectInstance.INFINITE, 0, false, false, false));
        }
    }

    private static void removeNightVision(MinecraftClient client) {
        if (client.player != null) {
            if (!isFullbright) {
                // Remove the night vision effect
                client.player.removeStatusEffect(NIGHT_VISION_EFFECT);
            }
        }
    }
}
