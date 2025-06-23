package com.lifetools;

import com.lifetools.annotations.Feature;
import com.lifetools.commandsystem.LifeToolsCmd;
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

import static com.lifetools.LifeTools.INFO_PREFIX;
import static com.lifetools.LifeTools.WARNING_PREFIX;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class Fly implements ClientModInitializer {

    private KeyBinding toggleFlyingKey;
    private double oldY = 0.0D;
    private int floatingTickCount = 0;
    public static boolean isFlying = false;

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
        // Register the "fly" command
        LifeToolsCmd.addCmd("fly", args -> {
            onFlyCommand();
        });

        // Register the "flyspeed" command with integer parameter handling
        LifeToolsCmd.addCmd("flyspeed", args -> {
            if (args.length < 1) {
                // No speed provided, display usage information
                MinecraftClient.getInstance().player.sendMessage(Text.of(INFO_PREFIX + "Usage: !flyspeed <speed>"), false);
                return;
            }

            try {
                int speed = Integer.parseInt(args[0]); // Parse the first argument as an integer
                onFlySpeedCommand(speed); // Pass the parsed speed to the handler
            } catch (NumberFormatException e) {
                // Handle invalid number format
                MinecraftClient.getInstance().player.sendMessage(Text.of(INFO_PREFIX + "Invalid speed. Please enter a valid integer."), false);
            }
        });
    }

    @Feature(methodName = "onFlyCommand", actionType = "toggle", featureName = "Fly Mode", booleanField = "isFlying")
    private int onFlyCommand() {
        toggleFlying(MinecraftClient.getInstance().player);
        return 1;
    }



    private int onFlySpeedCommand(int speed) {
        if (speed < 1 || speed > 30) {
            String errorMessage = WARNING_PREFIX + "§6Fly speed can only be set from §a1 §6to §a30";
            assert MinecraftClient.getInstance().player != null;
            MinecraftClient.getInstance().player.sendMessage(Text.of(errorMessage), false);
            return 0;
        }

        setFlyingSpeed(MinecraftClient.getInstance().player, speed);
        String message = String.format(INFO_PREFIX + "Fly speed has been set to §a%d", speed);
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

    public static void setFlying(ClientPlayerEntity player, boolean enabled) {
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

            String message = String.format(INFO_PREFIX + "Fly mode has been %s §7for §a%s", status, playerName);

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
            if (client.player.getPos().getY() >= oldY - 0.0633D) {
                floatingTickCount += 1;
            }

            oldY = client.player.getPos().getY();

            if (floatingTickCount > 14) {
                FlyPacket.sendPosition(client.player.getPos().subtract(0.0, 0.0633D, 0.0));
                floatingTickCount = 0;
            }
        }
    }
}
