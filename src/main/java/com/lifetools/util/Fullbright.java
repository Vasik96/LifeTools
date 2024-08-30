package com.lifetools.util;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;

import static com.lifetools.LifeTools.INFO_PREFIX;

public class Fullbright {

    private static boolean isFullbright = false;
    private static final double FULLBRIGHT_VALUE = 100.0;
    private static final double NORMAL_VALUE = 1.0;

    public void toggleFullbright(FabricClientCommandSource source) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.options == null) {
            source.sendFeedback(Text.literal(INFO_PREFIX + "Error: Minecraft client or options not available."));
            return;
        }

        GameOptions options = client.options;

        // Toggle fullbright mode
        isFullbright = !isFullbright;

        // Adjust gamma value
        double newGammaValue = isFullbright ? FULLBRIGHT_VALUE : NORMAL_VALUE;
        options.getGamma().setValue(newGammaValue);

        // Notify user of the status change
        String status = isFullbright ? "§aenabled" : "§cdisabled";
        source.sendFeedback(Text.literal(INFO_PREFIX + "Fullbright has been " + status));
    }
}
