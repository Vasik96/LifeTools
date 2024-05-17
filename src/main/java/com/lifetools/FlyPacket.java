package com.lifetools;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

public class FlyPacket {
    public static void sendPosition(Vec3d pos) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            boolean onGround = client.player.isOnGround();
            PlayerMoveC2SPacket.PositionAndOnGround packet = new PlayerMoveC2SPacket.PositionAndOnGround(
                    pos.getX(), pos.getY(), pos.getZ(), onGround
            );

            client.player.networkHandler.sendPacket(packet);
        }
    }
}