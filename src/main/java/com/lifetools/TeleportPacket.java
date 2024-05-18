package com.lifetools;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

public class TeleportPacket {

    public static void send(ClientConnection connection, Vec3d targetPosition, boolean isOnGround) {
        PlayerMoveC2SPacket.PositionAndOnGround packet = new PlayerMoveC2SPacket.PositionAndOnGround(
                targetPosition.x,
                targetPosition.y,
                targetPosition.z,
                isOnGround
        );
        connection.send(packet);
    }

    public static void spamPacketsAndTeleport(ClientPlayerEntity player, int totalDistance, double x, double y, double z) {
        int spamPacketCount = 15; // Number of packets to spam
        ClientConnection connection = player.networkHandler.getConnection();
        Vec3d currentPosition = player.getPos();

        // Spam packets with the current position
        for (int i = 0; i < spamPacketCount; i++) {
            send(connection, currentPosition, player.isOnGround());
        }

        // Calculate the new position
        double newX = player.getX() + x * totalDistance;
        double newY = player.getY() + y * totalDistance;
        double newZ = player.getZ() + z * totalDistance;

        Vec3d newPosition = new Vec3d(newX, newY, newZ);

        // Send the final teleport packet
        send(connection, newPosition, player.isOnGround());

        // Update player's position immediately for client-side effects
        player.updatePosition(newX, newY, newZ);
    }
}
