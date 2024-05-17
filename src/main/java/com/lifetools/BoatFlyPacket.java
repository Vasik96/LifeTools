package com.lifetools;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;

public class BoatFlyPacket {
    public static void moveBoatDown(BoatEntity boat) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && boat != null) {
            double newY = boat.getY() - 0.0433D;

            // Update the boat's position
            boat.updatePosition(boat.getX(), newY, boat.getZ());

            // Send the updated position to the server
            VehicleMoveC2SPacket packet = new VehicleMoveC2SPacket(boat);
            client.player.networkHandler.sendPacket(packet);

            // Update player position relative to the boat
            client.player.updatePosition(boat.getX(), newY, boat.getZ());
        }
    }
}
