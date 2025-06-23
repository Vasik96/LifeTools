package com.lifetools;

import com.lifetools.annotations.Feature;
import com.lifetools.commandsystem.LifeToolsCmd;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import static com.lifetools.LifeTools.INFO_PREFIX;
import static com.lifetools.LifeTools.WARNING_PREFIX;

public class BoatFly implements ClientModInitializer {

    public static boolean boatFlyEnabled = false;
    public static double boatFlySpeed = 1.0;
    private double oldY = 0.0;
    private int floatingTickCount = 0;

    @Override
    public void onInitializeClient() {
        // Register the boatfly command with subcommands directly implemented
        LifeToolsCmd.addCmd("boatfly", args -> {
            if (args.length == 0) {
                // If no subcommand is provided, toggle the boat fly status
                toggleBoatFly();
            } else if (args.length == 1 && "speed".equalsIgnoreCase(args[0])) {
                // Subcommand: boatfly speed
                sendErrorMessage("Usage: !boatfly speed <value>");
            } else if (args.length == 2 && "speed".equalsIgnoreCase(args[0])) {
                // Subcommand: boatfly speed <value>
                try {
                    int speed = Integer.parseInt(args[1]);
                    setBoatFlySpeed(speed);
                } catch (NumberFormatException e) {
                    sendInvalidSpeedMessage();
                }
            } else {
                sendErrorMessage("Invalid usage. Example: !boatfly or !boatfly speed <value>");
            }
        });

        // Register a client tick event to check for key presses and handle the fly mechanics
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (boatFlyEnabled) {
                handleBoatFly(client);
                checkAntiFlyKick(client);
            }
        });
    }

    @Feature(methodName = "toggleBoatFly", actionType = "toggle", featureName = "Boat Fly", booleanField = "boatFlyEnabled")
    public void toggleBoatFly() {
        boatFlyEnabled = !boatFlyEnabled;
        String status = boatFlyEnabled ? "§aenabled" : "§cdisabled";
        assert MinecraftClient.getInstance().player != null;
        MinecraftClient.getInstance().player.sendMessage(
                Text.of(INFO_PREFIX + "Boat Fly has been " + status), false);
    }

    private void sendInvalidSpeedMessage() {
        assert MinecraftClient.getInstance().player != null;
        MinecraftClient.getInstance().player.sendMessage(
                Text.of(WARNING_PREFIX + "§6Boat Fly speed must be an integer between 1 and 10."), false);
    }

    private void setBoatFlySpeed(int speed) {
        if (speed < 1 || speed > 10) {
            sendInvalidSpeedMessage();
            return;
        }

        boatFlySpeed = speed;
        assert MinecraftClient.getInstance().player != null;
        MinecraftClient.getInstance().player.sendMessage(
                Text.of(String.format(INFO_PREFIX + "Boat Fly speed has been set to §a%d", speed)), false);
    }

    private void handleBoatFly(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player != null && player.getVehicle() instanceof BoatEntity boat) {
            if (client.options.jumpKey.isPressed()) {
                boat.addVelocity(0, boatFlySpeed * 0.05, 0); // Fly up with reduced speed
            } else {
                boat.addVelocity(0, 0.0000001, 0); // Faster glide effect
            }
            if (client.options.forwardKey.isPressed()) {
                double horizontalSpeed = boatFlySpeed * 0.1; // Adjust horizontal speed based on boatFlySpeed
                Vec3d rotationVector = boat.getRotationVector().normalize();
                boat.addVelocity(rotationVector.x * horizontalSpeed, 0, rotationVector.z * horizontalSpeed); // Move forward with speed
            }

            // Apply slight drag for realism
            boat.setVelocity(boat.getVelocity().multiply(0.98, 1, 0.98)); // Apply slight drag for realism
        }
    }

    private void checkAntiFlyKick(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player != null && player.getVehicle() instanceof BoatEntity boat) {
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

    private void sendErrorMessage(String message) {
        assert MinecraftClient.getInstance().player != null;
        MinecraftClient.getInstance().player.sendMessage(Text.of(WARNING_PREFIX + message), false);
    }
}
