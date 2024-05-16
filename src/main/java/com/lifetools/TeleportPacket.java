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

    public static void teleportInSteps(ClientPlayerEntity player, int totalDistance, double x, double y, double z) {
        int maxStepDistance = 8;
        int remainingDistance = totalDistance;
        ClientConnection connection = player.networkHandler.getConnection();

        while (remainingDistance > 0) {
            int stepDistance = Math.min(remainingDistance, maxStepDistance);

            double newX = player.getX() + x * stepDistance;
            double newY = player.getY() + y * stepDistance;
            double newZ = player.getZ() + z * stepDistance;

            Vec3d newPosition = new Vec3d(newX, newY, newZ);

            // Send the PlayerMoveC2SPacket
            send(connection, newPosition, player.isOnGround());

            // Update player's position immediately for client-side effects
            player.updatePosition(newX, newY, newZ);

            remainingDistance -= stepDistance;
        }
    }
}
