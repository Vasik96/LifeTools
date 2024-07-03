package com.lifetools;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class Fly implements ClientModInitializer {

    private KeyBinding toggleFlyingKey;
    private double oldY = 0.0D;
    private int floatingTickCount = 0;
    private boolean isFlying = false;


    @Override
    public void onInitializeClient() {
        toggleFlyingKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Toggle Flying",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                "Fly"
        ));

        registerCommands();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            checkToggleKey();
            checkAntiFlyKick();
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> isFlying = false);
    }
    private void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(
                    literal("fly").executes(context -> onFlyCommand())
            );

            dispatcher.register(
                    literal("flyspeed")
                            .then(ClientCommandManager.argument("speed", IntegerArgumentType.integer())
                                    .executes(context -> onFlySpeedCommand(IntegerArgumentType.getInteger(context, "speed"))))
            );
        });
    }
    private int onFlyCommand() {
        toggleFlying(MinecraftClient.getInstance().player);
        return 1;
    }
    private int onFlySpeedCommand(int speed) {
        if (speed < 1 || speed > 30) {
            String errorMessage = "§8[§2LifeTools§8] §7Fly speed can only be set from §a1 §7to §a30";
            assert MinecraftClient.getInstance().player != null;
            MinecraftClient.getInstance().player.sendMessage(Text.of(errorMessage), false);
            return 0;
        }

        setFlyingSpeed(MinecraftClient.getInstance().player, speed);
        String message = String.format("§8[§2LifeTools§8] §7Fly speed has been set to §a%d", speed);
        assert MinecraftClient.getInstance().player != null;
        MinecraftClient.getInstance().player.sendMessage(Text.of(message), false);
        return 1;
    }
    private void toggleFlying(ClientPlayerEntity player) {
        if (player != null) {
            isFlying = !isFlying;
            setFlying(player, isFlying);
        }
    }

    private void setFlying(ClientPlayerEntity player, boolean enabled) {
        if (player != null) {
            isFlying = enabled;

            PlayerAbilities abilities = player.getAbilities();
            abilities.allowFlying = isFlying;
            if (!isFlying) {
                abilities.flying = false;
            }
            player.sendAbilitiesUpdate();

            String status = isFlying ? "§aenabled" : "§cdisabled";
            String playerName = player.getName().getString();

            String message = String.format("§8[§2LifeTools§8] §7Fly mode has been %s §7for §a%s", status, playerName);

            player.sendMessage(Text.of(message), false);
        }
    }

    private void setFlyingSpeed(ClientPlayerEntity player, int speed) {
        if (player != null) {
            float defaultSpeed = 0.05f;

            float newSpeed = defaultSpeed * speed;

            PlayerAbilities abilities = player.getAbilities();
            abilities.setFlySpeed(newSpeed);
            player.sendAbilitiesUpdate();
        }
    }

    private void checkToggleKey() {
        while (toggleFlyingKey.wasPressed()) {
            toggleFlying(MinecraftClient.getInstance().player);
        }
    }

    private void checkAntiFlyKick() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && isFlying) {
            if (client.player.getPos().getY() >= oldY - 0.0433D) {
                floatingTickCount += 1;
            }

            oldY = client.player.getPos().getY();

            if (floatingTickCount > 20) {
                FlyPacket.sendPosition(client.player.getPos().subtract(0.0, 0.0433D, 0.5));
                floatingTickCount = 0;
            }
        }
    }
}
