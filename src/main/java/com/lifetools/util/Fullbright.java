package com.lifetools.util;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;

import static com.lifetools.LifeTools.ERROR_PREFIX;
import static com.lifetools.LifeTools.INFO_PREFIX;

public class Fullbright {

    public static boolean isFullbright = false;
    private double originalGamma = 1.0; // Default gamma value in Minecraft

    public void toggleFullbright(FabricClientCommandSource source) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) {
            source.sendFeedback(Text.literal(ERROR_PREFIX + "Minecraft client or player not available."));
            return;
        }

        GameOptions options = client.options;
        if (options == null) {
            source.sendFeedback(Text.literal(ERROR_PREFIX + "Game options not available."));
            return;
        }

        // Toggle fullbright mode
        isFullbright = !isFullbright;

        if (isFullbright) {
            // Store the original gamma value
            originalGamma = options.getGamma().getValue();

            // Set gamma to a very high value for fullbright
            setGamma(client, 10000.0);
            source.sendFeedback(Text.literal(INFO_PREFIX + "Fullbright has been §aenabled"));
        } else {
            // Restore the original gamma value
            setGamma(client, originalGamma);
            source.sendFeedback(Text.literal(INFO_PREFIX + "Fullbright has been §cdisabled"));
        }
    }

    private void setGamma(MinecraftClient client, double gammaValue) {
        GameOptions options = client.options;
        if (options != null) {
            options.getGamma().setValue(gammaValue);
        }
    }
}
