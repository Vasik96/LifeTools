package com.lifetools;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import static com.lifetools.LifeTools.INFO_PREFIX;

public class Jesus implements ClientModInitializer {

    private final MinecraftClient mc = MinecraftClient.getInstance();
    private boolean jesusModeEnabled = false;

    @Override
    public void onInitializeClient() {
        // Register the /jesus command
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal("jesus")
                .executes(context -> {
                    toggleJesusMode();
                    return 1;
                })
        ));

        // Add a tick event listener to apply the Jesus effect when enabled
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (jesusModeEnabled && mc.player != null && mc.world != null && !mc.isPaused()) {
                onTick();
            }
        });
    }

    private void toggleJesusMode() {
        jesusModeEnabled = !jesusModeEnabled;
        if (mc.player != null) {
            String status = jesusModeEnabled ? "§aenabled" : "§cdisabled";
            mc.player.sendMessage(Text.literal(INFO_PREFIX + "Jesus mode has been " + status), false);

            // Ensure the player is reset to normal behavior when Jesus mode is disabled
            if (!jesusModeEnabled) {
                resetPlayer();
            }
        }
    }

    public void onTick() {
        if (mc.player == null || mc.world == null) return;  // Safeguard against null references

        Entity e = mc.player.getRootVehicle();  // Gets the vehicle the player is riding, or the player itself
        if (e == null || e.isSneaking() || e.fallDistance > 3f) return;

        // Default speed, can be adjusted or made configurable
        int speed = 1;

        if (isSubmerged(e.getPos().add(0, 0.3, 0))) {
            e.setVelocity(e.getVelocity().x, 0.08, e.getVelocity().z);
        } else if (isSubmerged(e.getPos().add(0, 0.1, 0))) {
            e.setVelocity(e.getVelocity().x, 0.05, e.getVelocity().z);
        } else if (isSubmerged(e.getPos().add(0, 0.05, 0))) {
            e.setVelocity(e.getVelocity().x, 0.01, e.getVelocity().z);
        } else if (isSubmerged(e.getPos())) {
            e.setVelocity(e.getVelocity().x, -0.005, e.getVelocity().z);
            e.setOnGround(true);
            if (mc.options.forwardKey.isPressed()) {
                movePlayerForward((PlayerEntity) e, speed / 3F);
            }
        }
    }

    private boolean isSubmerged(Vec3d pos) {
        BlockPos bp = BlockPos.ofFloored(pos);
        if (mc.world == null) return false;  // Safeguard against null references
        FluidState state = mc.world.getFluidState(bp);

        return !state.isEmpty() && pos.getY() - bp.getY() <= state.getHeight();
    }

    public static void movePlayerForward(PlayerEntity player, float speed) {
        if (player == null) return;  // Safeguard against null references
        float yaw = player.getYaw();
        double radians = Math.toRadians(yaw);
        double deltaX = (-speed / 5) * Math.sin(radians);
        double deltaZ = (speed / 5) * Math.cos(radians);
        player.addVelocity(deltaX, 0, deltaZ);
    }

    // Method to reset any player changes when Jesus mode is disabled
    private void resetPlayer() {
        if (mc.player == null) return;

        // Reset any modified properties like velocity or position here
        mc.player.setVelocity(0, 0, 0);
        mc.player.setOnGround(false);
    }
}
