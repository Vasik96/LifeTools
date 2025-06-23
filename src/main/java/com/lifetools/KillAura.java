package com.lifetools;

import com.lifetools.annotations.Feature;
import com.lifetools.commandsystem.LifeToolsCmd;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

import static com.lifetools.LifeTools.INFO_PREFIX;

public class KillAura implements ClientModInitializer {

    private KeyBinding toggleKillauraKey;
    public static boolean killauraEnabled = false;
    private Reach reach;
    public static String mode = "default";
    private long lastAttackTime = 0;
    private final Map<Entity, Long> lastAttackTimes = new HashMap<>();

    @Override
    public void onInitializeClient() {
        reach = new Reach();
        reach.onInitializeClient();

        toggleKillauraKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Toggle Killaura",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "Killaura"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKillauraKey.wasPressed()) {
                toggleKillaura();  // Call the annotated method
            }

            if (killauraEnabled) {
                performKillaura(client);
            }
        });

        registerCommands();
    }

    private void registerCommands() {
        // Register the "killaura" base command
            LifeToolsCmd.addCmd("killaura", args -> {
                if (args.length == 0) {
                    // No arguments provided, display a generic message
                    MinecraftClient.getInstance().player.sendMessage(Text.of(INFO_PREFIX + "Usage: !killaura mode <mode_name> \n" +
                            "Modes:\n" +
                            " - newcombat\n" +
                            " - avoid_too_much_packets\n" +
                            " - default\n" +
                            " - tpaura"), false);
                    return;
                }

                // Handle subcommands
                if ("mode".equalsIgnoreCase(args[0])) {
                    handleModeCommand(args);
                } else {
                    // Unknown subcommand
                    MinecraftClient.getInstance().player.sendMessage(Text.of(INFO_PREFIX + "Unknown subcommand: " + args[0]), false);
                }
            });

        // Add suggestions for the "mode" subcommand
        LifeToolsCmd.addCmd("killaura mode", args -> {
            // This is a helper for mode handling; logic is in the base command
            handleModeCommand(args);
        }, false);
    }

    private void handleModeCommand(String[] args) {
        MinecraftClient client = MinecraftClient.getInstance();
        assert client.player != null;

        if (args.length == 1) {
            // No mode specified, display the current mode
            client.player.sendMessage(Text.of(INFO_PREFIX + "Current Killaura mode: §a" + mode), false);
            return;
        }

        // Set the mode if specified
        String newMode = args[1].toLowerCase();
        switch (newMode) {
            case "newcombat":
            case "avoid_too_much_packets":
            case "default":
            case "tpaura":
                setMode(newMode);
                client.player.sendMessage(Text.of(INFO_PREFIX + "Killaura mode has been set to: §a" + newMode), false);
                break;
            default:
                // Invalid mode
                client.player.sendMessage(Text.of(INFO_PREFIX + "Invalid mode: §c" + newMode), false);
        }
    }

    @Feature(methodName = "toggleKillaura", actionType = "toggle", featureName = "Killaura", booleanField = "killauraEnabled")
    public void toggleKillaura() {
        killauraEnabled = !killauraEnabled; // Toggle the state

        // Prepare the feedback message based on the new state
        String message = INFO_PREFIX + "Killaura has been " + (killauraEnabled ? "§aenabled" : "§cdisabled");
        if (MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().player.sendMessage(Text.of(message), false);
        }
    }

    public static void setModeFromExternal(String mode) {
        KillAura killAura = new KillAura();
        killAura.setMode(mode);
    }

    public int setMode(String mode) {
        this.mode = mode;
        assert MinecraftClient.getInstance().player != null;
        MinecraftClient.getInstance().player.sendMessage(Text.of(INFO_PREFIX + "Killaura mode set to §a" + mode), false);
        return 1;
    }

    private void performKillaura(MinecraftClient client) {
        if (client.player == null || client.world == null) {
            return;
        }

        double reachDistance = reach.getCurrentReach();
        long currentTime = System.currentTimeMillis();

        // Find all entities in range
        for (Entity entity : client.world.getEntities()) {
            if (entity instanceof PlayerEntity || entity instanceof SlimeEntity) {
                if (entity != client.player && entity.isAlive() && client.player.squaredDistanceTo(entity) < reachDistance * reachDistance) {
                    // Determine if the attack should proceed based on mode and cooldown
                    switch (mode) {
                        case "newcombat":
                            if (currentTime - lastAttackTime >= 800) { // 1.5 seconds cooldown
                                attackEntities(client);
                                lastAttackTime = currentTime; // Update last attack time
                            }
                            break;
                        case "avoid_too_much_packets":
                            Long lastAttack = lastAttackTimes.get(entity);
                            if (lastAttack == null || currentTime - lastAttack >= 150) { // 150 milliseconds cooldown
                                attackEntities(client);
                                lastAttackTimes.put(entity, currentTime);
                            }
                            break;
                        case "TPAura":
                            performTPAura(client, entity, currentTime);
                            break;
                        default:
                            attackEntities(client);
                            break;
                    }
                    break; // After attacking, no need to check other entities
                }
            }
        }
    }

    private void performTPAura(MinecraftClient client, Entity targetEntity, long currentTime) {
        Long lastAttack = lastAttackTimes.get(targetEntity);
        if (lastAttack == null || currentTime - lastAttack >= 150) { // 150 milliseconds cooldown
            // Teleport around the target entity before attacking
            teleportAroundEntity(client, targetEntity);
            attackEntities(client);
            lastAttackTimes.put(targetEntity, currentTime);
        }
    }

    private void teleportAroundEntity(MinecraftClient client, Entity targetEntity) {
        if (client.player == null || targetEntity == null) {
            return;
        }

        // Base minimum and maximum teleport distances
        double minTeleportDistance = 1.75;
        double maxTeleportDistance = 3.0; //default max reach distance is 3

        // Adjust teleport distances based on reach
        if (Reach.reachToggled) {
            double currentReach = reach.getCurrentReach();
            minTeleportDistance += currentReach * 0.15;
            maxTeleportDistance += currentReach * 0.3;
        }

        // Set maximum limit for teleport distance
        double maxPossibleTeleportDistance = 4.5;
        if (maxTeleportDistance > maxPossibleTeleportDistance) {
            maxTeleportDistance = maxPossibleTeleportDistance;
        }

        double targetX = targetEntity.getX();
        double targetY = targetEntity.getY();
        double targetZ = targetEntity.getZ();
        boolean safeSpotFound = false;

        // Try multiple attempts to find a safe teleport location
        for (int attempt = 0; attempt < 10; attempt++) {  // Increase attempts for better reliability
            double teleportDistance = minTeleportDistance + Math.random() * (maxTeleportDistance - minTeleportDistance);
            double angle = Math.random() * 2 * Math.PI; // Random angle for teleportation

            double xOffset = teleportDistance * Math.cos(angle);
            double zOffset = teleportDistance * Math.sin(angle);

            // Check multiple height levels to find a safe spot
            for (int yOffset = -3; yOffset <= 3; yOffset++) {  // Increase height checks for uneven terrain
                double newX = targetEntity.getX() + xOffset;
                double newY = targetEntity.getY() + yOffset;
                double newZ = targetEntity.getZ() + zOffset;

                if (isSafeSpot(client, newX, newY, newZ) && !isNearPreviousLocation(client, newX, newY, newZ)) {
                    targetX = newX;
                    targetY = newY;
                    targetZ = newZ;
                    safeSpotFound = true;
                    break;
                }
            }

            if (safeSpotFound) {
                break;  // Stop trying if a safe spot is found
            }
        }

        // Perform the teleportation if a safe spot is found
        if (safeSpotFound) {
            double verticalDistance = targetY - client.player.getY();
            double horizontalDistanceX = targetX - client.player.getX();
            double horizontalDistanceZ = targetZ - client.player.getZ();

            // First, teleport vertically to avoid packet refusal
            if (verticalDistance != 0) {
                TeleportPacket.spamPacketsAndTeleport(client.player, 1, 0, verticalDistance, 0);
            }

            // Then, teleport horizontally
            TeleportPacket.spamPacketsAndTeleport(client.player, 1, horizontalDistanceX, 0, horizontalDistanceZ);
        } else {
            // Fallback: Try a small vertical hop to reset position if no spot was found
            TeleportPacket.spamPacketsAndTeleport(client.player, 1, 0, 1, 0);  // Small vertical hop
        }
    }

    private boolean isSafeSpot(MinecraftClient client, double x, double y, double z) {
        BlockPos blockPosBelow = new BlockPos((int) x, (int) y - 1, (int) z);
        BlockPos blockPosAbove = new BlockPos((int) x, (int) y, (int) z);

        // Ensure the block below is not air and there is enough space to stand
        assert client.world != null;
        return !client.world.getBlockState(blockPosBelow).isAir() && client.world.isAir(blockPosAbove);
    }

    private boolean isNearPreviousLocation(MinecraftClient client, double x, double y, double z) {
        // Check if the new location is too close to the previous location
        double threshold = 1.0;  // Define how close is considered "too close"
        assert client.player != null;
        double distanceSquared = (client.player.getX() - x) * (client.player.getX() - x) +
                (client.player.getY() - y) * (client.player.getY() - y) +
                (client.player.getZ() - z) * (client.player.getZ() - z);
        return distanceSquared < threshold * threshold;
    }

    private void attackEntities(MinecraftClient client) {
        // Attack all entities in range
        double reachDistance = reach.getCurrentReach();
        assert client.world != null;
        for (Entity entity : client.world.getEntities()) {
            if ((entity instanceof PlayerEntity || entity instanceof SlimeEntity) &&
                    entity != client.player && entity.isAlive()) {
                assert client.player != null;
                if (client.player.squaredDistanceTo(entity) < reachDistance * reachDistance) {
                    attackEntity(client, entity);
                }
            }
        }
    }

    private void attackEntity(MinecraftClient client, Entity entity) {
        if (client.interactionManager != null && entity != null) {
            client.interactionManager.attackEntity(client.player, entity);
            assert client.player != null;
            client.player.swingHand(client.player.getActiveHand());
        }
    }
}
