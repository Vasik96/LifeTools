package com.lifetools.util;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import static com.lifetools.LifeTools.INFO_PREFIX;

public class GamemodeSwitcher {

    private static final MinecraftClient client = MinecraftClient.getInstance();

    public void handleGamemodeSwitch(FabricClientCommandSource source, String mode) {
        String gamemode;

        switch (mode.toLowerCase()) {
            case "sp":
            case "spectator":
                gamemode = "spectator";
                break;
            case "s":
            case "survival":
                gamemode = "survival";
                break;
            case "c":
            case "creative":
                gamemode = "creative";
                break;
            case "a":
            case "adventure":
                gamemode = "adventure";
                break;
            case "0":
                gamemode = "survival";
                break;
            case "1":
                gamemode = "creative";
                break;
            case "2":
                gamemode = "adventure";
                break;
            case "3":
                gamemode = "spectator";
                break;
            default:
                source.sendFeedback(Text.literal(INFO_PREFIX + "Invalid gamemode"));
                return;
        }

        if (client.player != null) {
            // Send the command to change gamemode
            client.player.networkHandler.sendCommand("gamemode " + gamemode);
        }
    }
}
