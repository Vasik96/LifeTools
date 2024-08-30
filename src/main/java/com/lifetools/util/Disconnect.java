package com.lifetools.util;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.Objects;

public class Disconnect {

    private static final MinecraftClient client = MinecraftClient.getInstance();

    // Static string to store the reason for disconnecting
    public static String reason = "Manual command";

    public void handleDisconnect(FabricClientCommandSource source) {
        String customDisconnectMessage = """
                §8■ ■ ■ ■ ■ ■ ■ ■  §cDisconnected  §8■ ■ ■ ■ ■ ■ ■

                §7Reason: §c""" + reason + """
                
                
                §8■ ■ ■ ■ ■ ■ ■ ■ ■ ■ ■ ■ ■ ■ ■ ■ ■ ■ ■ ■ ■ ■ ■
                """;

        Objects.requireNonNull(client.getNetworkHandler()).getConnection().disconnect(Text.literal(customDisconnectMessage));
        reason = "Manual command";
    }
}
