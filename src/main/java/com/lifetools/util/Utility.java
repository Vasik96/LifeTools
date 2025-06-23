package com.lifetools.util;

import com.lifetools.ClickableChatHelper;
import com.lifetools.commandsystem.LifeToolsCmd;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.List;

public class Utility implements ClientModInitializer {

    private static final List<String> GAMEMODES = Arrays.asList("sp", "spectator", "s", "survival", "c", "creative", "a", "adventure");

    @Override
    public void onInitializeClient() {
        // Register the "util" base command
        LifeToolsCmd.addCmd("util", args -> {
            if (args.length == 0) {
                showUtilityHelp();
                return;
            }

            String subCommand = args[0].toLowerCase();
            switch (subCommand) {
                case "disconnect":
                    handleDisconnect();
                    break;
                case "gm":
                    handleGamemode(args);
                    break;
                case "launch":
                    handleLaunch();
                    break;
                case "fullbright":
                    handleFullbright();
                    break;
                case "nickname":
                    handleNickname(args);
                    break;
                default:
                    showUtilityHelp();
            }
        });
    }

    // Show help message for "util" command
    private void showUtilityHelp() {
        sendFeedback(Text.literal("§aLifeTools §7- §2§lUtility features"));
        sendFeedback(Text.literal("§8-------------------------------------------"));
        sendFeedback(Text.literal(" §3§lSubcommands:"));
        sendFeedback(ClickableChatHelper.createClickableText("    §8- §7§ldisconnect §8(disconnects you from the current server)", "!util disconnect"));
        sendFeedback(ClickableChatHelper.createClickableText("    §8- §7§lgm <gamemode> §8(switches your gamemode if you have permissions)", "!util gm "));
        sendFeedback(ClickableChatHelper.createClickableText("    §8- §7§llaunch §8(launches you in the air)", "!util launch"));
        sendFeedback(ClickableChatHelper.createClickableText("    §8- §7§lfullbright §8(toggles fullbright)", "!util fullbright"));
        sendFeedback(ClickableChatHelper.createClickableText("    §8- §7§lnickname <name> §8(changes your minecraft username)", "!util nickname "));
        sendFeedback(Text.literal("§8-------------------------------------------"));
    }


    private void handleDisconnect() {
        new Disconnect().handleDisconnect(); // Make sure the disconnect logic is appropriate
    }

    private void handleGamemode(String[] args) {
        if (args.length < 2) {
            sendFeedback(Text.literal("§cUsage: §7!util gm <gamemode>"));
            return;
        }
        String mode = args[1];
        if (GAMEMODES.contains(mode)) {
            new GamemodeSwitcher().handleGamemodeSwitch(mode);
        } else {
            sendFeedback(Text.literal("§cInvalid gamemode. Valid options are: " + String.join(", ", GAMEMODES)));
        }
    }

    private void handleLaunch() {
        new Launch().handleLaunch(); // Make sure the launch logic is appropriate
    }

    private void handleFullbright() {
        new Fullbright().toggleFullbright(); // Make sure the fullbright toggle logic is appropriate
    }

    private void handleNickname(String[] args) {
        if (args.length < 2) {
            sendFeedback(Text.literal("§cUsage: §7!util nickname <name>"));
            return;
        }
        String newName = args[1];
        new NameChanger().changeName(newName); // Ensure the name change logic is correct
    }

    // Updated sendFeedback method to accept Text type
    public static void sendFeedback(Text message) {
        if (MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().player.sendMessage(message, false);
        }
    }
}
