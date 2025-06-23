package com.lifetools;

import com.lifetools.annotations.Feature;
import com.lifetools.commandsystem.LifeToolsCmd;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

import static com.lifetools.LifeTools.INFO_PREFIX;

public class Criticals implements ClientModInitializer {
    public static boolean enabled = false;
    public static final MinecraftClient MC = MinecraftClient.getInstance(); // Define MC reference

    @Override
    public void onInitializeClient() {
        LifeToolsCmd.addCmd("criticals", args -> {
            toggle();
        });

        // Register the client tick event
        ClientTickEvents.END_CLIENT_TICK.register(client -> onUpdate());

        // Register the entity attack callback
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            onPlayerAttacksEntity(entity);
            return ActionResult.PASS; // Allow other handlers to process
        });
    }

    @Feature(methodName = "toggle", actionType = "toggle", featureName = "Criticals", booleanField = "enabled")
    private void toggle() {
        enabled = !enabled;
        String status = enabled ? "§aenabled" : "§cdisabled";
        assert MC.player != null;
        MC.player.sendMessage(Text.literal(INFO_PREFIX + "Criticals have been " + status), false);
    }

    private void onUpdate() {
        if (enabled && MC.player != null && MC.player.isOnGround()) {
            doPacketJump(); // Trigger the critical hit packet
        }
    }

    private void onPlayerAttacksEntity(Entity target) {
        // Check if criticals are enabled
        if (!enabled) {
            return; // Exit if criticals are not enabled
        }

        // Check if the target is a LivingEntity
        if (!(target instanceof LivingEntity)) {
            return;
        }

        assert MC.player != null;

        // Check if the player is on the ground and not in water or lava
        if (!MC.player.isOnGround() || MC.player.isTouchingWater() || MC.player.isInLava()) {
            return; // Exit if conditions for criticals are not met
        }

        // Trigger the critical hit jump
        doPacketJump(); // Perform the jump logic
    }

    private void doPacketJump() {
        sendFakeY(0.0625, true);
        sendFakeY(0, false);
        sendFakeY(1.1e-5, false);
        sendFakeY(0, false);
    }

    private void sendFakeY(double offset, boolean onGround) {
        assert MC.player != null;
        MC.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                MC.player.getX(),
                MC.player.getY() + offset,
                MC.player.getZ(),
                onGround
        ));
    }
}
