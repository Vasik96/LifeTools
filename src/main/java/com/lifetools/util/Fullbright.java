package com.lifetools.util;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.text.Text;

import static com.lifetools.LifeTools.INFO_PREFIX;

public class Fullbright {

    private static boolean isFullbright = false;

    public void toggleFullbright(FabricClientCommandSource source) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) {
            source.sendFeedback(Text.literal(INFO_PREFIX + "Error: Minecraft client or player not available."));
            return;
        }

        // Toggle fullbright mode
        isFullbright = !isFullbright;

        // Apply or remove night vision based on the fullbright state
        if (isFullbright) {
            applyNightVision(client);
            source.sendFeedback(Text.literal(INFO_PREFIX + "Fullbright has been §aenabled"));
        } else {
            removeNightVision(client);
            source.sendFeedback(Text.literal(INFO_PREFIX + "Fullbright has been §cdisabled"));
        }
    }

    private void applyNightVision(MinecraftClient client) {
        if (client.player != null) {
            // Apply night vision effect
            client.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, StatusEffectInstance.INFINITE, 0, false, false, false));
        }
    }

    private void removeNightVision(MinecraftClient client) {
        if (client.player != null) {
            // Remove night vision effect
            client.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
        }
    }
}
