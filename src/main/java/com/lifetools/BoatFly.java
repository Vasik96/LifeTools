package com.lifetools;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class BoatFly implements ClientModInitializer {

    private boolean boatFlyEnabled = false;
    private double boatFlySpeed = 1.0;
    private double oldY = 0.0;
    private int floatingTickCount = 0;

    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, dedicated) ->
                registerCommands(dispatcher));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            handleBoatFly(client);
            checkAntiFlyKick(client);
        });
    }

    private void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        LiteralArgumentBuilder<FabricClientCommandSource> boatflyCommand = ClientCommandManager.literal("boatfly")
                .executes(context -> {
                    toggleBoatFly();
                    return 1;
                });

        RequiredArgumentBuilder<FabricClientCommandSource, Integer> speedArgument = ClientCommandManager.argument("speed", IntegerArgumentType.integer())
                .executes(context -> {
                    int speed = IntegerArgumentType.getInteger(context, "speed");
                    return setBoatFlySpeed(context, speed);
                });

        boatflyCommand.then(ClientCommandManager.literal("speed").then(speedArgument));

        dispatcher.register(boatflyCommand);
    }

    private void toggleBoatFly() {
        boatFlyEnabled = !boatFlyEnabled;
        String status = boatFlyEnabled ? "§aenabled" : "§cdisabled";
        assert MinecraftClient.getInstance().player != null;
        MinecraftClient.getInstance().player.sendMessage(
                Text.of("§8[§2LifeTools§8] §7Boat Fly has been " + status), false);
    }

    private int setBoatFlySpeed(CommandContext<FabricClientCommandSource> context, int speed) {
        if (speed < 1 || speed > 10) {
            String errorMessage = "§8[§2LifeTools§8] §7Boat Fly speed can only be set from §a1 §7to §a10";
            context.getSource().sendFeedback(Text.of(errorMessage));
            return 0; // Return 0 to indicate failure
        }

        boatFlySpeed = speed;
        assert MinecraftClient.getInstance().player != null;
        MinecraftClient.getInstance().player.sendMessage(
                Text.of(String.format("§8[§2LifeTools§8] §7Boat Fly speed has been set to §a%d", speed)), false);
        return 1; // Return 1 to indicate success
    }

    private void handleBoatFly(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player != null && player.getVehicle() instanceof BoatEntity boat) {
            if (client.options.jumpKey.isPressed()) {
                boat.addVelocity(0, 0.05, 0); // Fly up
            }
            if (client.options.forwardKey.isPressed()) {
                double horizontalSpeed = boatFlySpeed * 0.05;
                double verticalSpeed = boatFlySpeed * 0.02; // Smaller vertical speed
                Vec3d rotationVector = boat.getRotationVector().normalize();
                boat.addVelocity(rotationVector.x * horizontalSpeed, verticalSpeed, rotationVector.z * horizontalSpeed); // Move forward with some vertical lift
            }

            // Apply slight drag for realism
            boat.setVelocity(boat.getVelocity().multiply(0.98, 1.0, 0.98));
        }
    }

    private void checkAntiFlyKick(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player != null && player.getVehicle() instanceof BoatEntity boat && boatFlyEnabled) {
            double currentY = boat.getPos().getY();
            if (currentY >= oldY - 0.0433D) {
                floatingTickCount += 1;
            } else {
                floatingTickCount = 0;
            }

            oldY = currentY;

            if (floatingTickCount > 35) {
                BoatFlyPacket.moveBoatDown(boat);
                floatingTickCount = 0;
            }
        }
    }
}
