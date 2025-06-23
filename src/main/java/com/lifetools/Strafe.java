package com.lifetools;

import com.lifetools.annotations.Feature;
import com.lifetools.commandsystem.LifeToolsCmd;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.joml.Vector2d;

import static com.lifetools.LifeTools.INFO_PREFIX;

public class Strafe implements ClientModInitializer {
    public static boolean strafeEnabled = false;
    private static int stage = 0;
    private static double speed = 0.1;
    private static double distance = 0;

    private long timer = 0L;
    private final MinecraftClient mc = MinecraftClient.getInstance();

    @Override
    public void onInitializeClient() {
        // Register the "strafe" command using the custom command system
        LifeToolsCmd.addCmd("strafe", args -> {
            toggleStrafe(); // Execute the toggleStrafe logic
        });

        // Register tick event to calculate distance and apply strafe each tick
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                calculateDistance();
                if (strafeEnabled) applyStrafe(client.player);
            }
        });
    }



    @Feature(methodName = "toggleStrafe", actionType = "toggle", featureName = "Strafe", booleanField = "strafeEnabled")
    public static void toggleStrafe() {
        strafeEnabled = !strafeEnabled;

        // Send feedback message to the player
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.sendMessage(Text.literal(INFO_PREFIX + "Strafe has been " + (strafeEnabled ? "§aenabled" : "§cdisabled")), false);
        }
    }

    private void applyStrafe(ClientPlayerEntity player) {
        switch (stage) {
            case 0: // Initial stage, set speed if moving
                if (isMoving(player)) {
                    stage++;
                    speed = 1.18 * getDefaultSpeed() - 0.01;
                }
                break;
            case 1: // Apply horizontal strafe without jump
                if (isMoving(player)) {
                    speed *= 1.2;
                    stage++;
                }
                break;
            case 2: // Slowdown after initial strafe
                speed = distance - 0.76 * (distance - getDefaultSpeed());
                stage++;
                break;
            case 3: // Reset if collision or decrease speed gradually
                if (player.verticalCollision) {
                    stage = 0;
                }
                speed = distance - (distance / 159.0);
                break;
        }

        // Speed control to prevent extreme values
        speed = Math.max(speed, getDefaultSpeed());
        if (System.currentTimeMillis() - timer > 2500L) {
            timer = System.currentTimeMillis();
        }
        speed = Math.min(speed, System.currentTimeMillis() - timer > 1250L ? 0.44 : 0.43);

        // Apply strafe transformation based on movement direction
        Vector2d change = transformStrafe(speed, player);
        player.setVelocity(change.x, player.getVelocity().y, change.y); // Keep y velocity unaffected
    }


    private Vector2d transformStrafe(double speed, ClientPlayerEntity player) {
        float forward = player.input.movementForward;
        float side = player.input.movementSideways;
        float yaw = player.getYaw();

        double velX, velZ;

        if (forward == 0.0f && side == 0.0f) return new Vector2d(0, 0);

        // Calculate yaw angle adjustments for diagonal movement
        if (forward != 0.0f) {
            if (side > 0.0f) {
                yaw += (forward > 0.0f) ? -45 : 45;
                side = 0.0f;
            } else if (side < 0.0f) {
                yaw += (forward > 0.0f) ? 45 : -45;
                side = 0.0f;
            }
            forward = forward > 0.0f ? 1.0f : -1.0f;
        }

        // Calculate velocity based on adjusted yaw
        double mx = Math.cos(Math.toRadians(yaw + 90.0f));
        double mz = Math.sin(Math.toRadians(yaw + 90.0f));

        velX = forward * speed * mx + side * speed * mz;
        velZ = forward * speed * mz - side * speed * mx;

        return new Vector2d(velX, velZ);
    }

    private double getDefaultSpeed() {
        return 0.2873;
    }

    private boolean isMoving(ClientPlayerEntity player) {
        return player.input.movementForward != 0 || player.input.movementSideways != 0;
    }

    private void calculateDistance() {
        if (mc.player != null) {
            distance = Math.sqrt((mc.player.getX() - mc.player.prevX) * (mc.player.getX() - mc.player.prevX) + (mc.player.getZ() - mc.player.prevZ) * (mc.player.getZ() - mc.player.prevZ));
        }
    }
}
