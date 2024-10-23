package com.lifetools.util;

import com.lifetools.annotations.Feature;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;

import static com.lifetools.LifeTools.INFO_PREFIX;

public class Fullbright {

    public static boolean isFullbright = false;
    private double originalGamma = 1.0; // Default gamma value in Minecraft

    @Feature(methodName = "toggleFullbright", actionType = "toggle", featureName = "Fullbright", booleanField = "isFullbright")
    public void toggleFullbright() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) {
            return; // Exit if client or player is not available
        }

        GameOptions options = client.options;
        if (options == null) {
            return; // Exit if game options are not available
        }

        // Toggle fullbright mode
        isFullbright = !isFullbright;

        if (isFullbright) {
            // Store the original gamma value
            originalGamma = options.getGamma().getValue();

            // Set gamma to a very high value for fullbright
            setGamma(client, 10000.0);
            client.player.sendMessage(Text.literal(INFO_PREFIX + "Fullbright has been §aenabled"), false);
        } else {
            // Restore the original gamma value
            setGamma(client, originalGamma);
            client.player.sendMessage(Text.literal(INFO_PREFIX + "Fullbright has been §cdisabled"), false);
        }
    }

    private void setGamma(MinecraftClient client, double gammaValue) {
        GameOptions options = client.options;
        if (options != null) {
            options.getGamma().setValue(gammaValue);
        }
    }
}
