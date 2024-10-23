package com.lifetools.util;

import com.lifetools.ClickableChatHelper;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.List;

public class Utility implements ClientModInitializer {

    private static final List<String> GAMEMODES = Arrays.asList("sp", "spectator", "s", "survival", "c", "creative", "a", "adventure");

    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                LiteralArgumentBuilder.<FabricClientCommandSource>literal("util")
                        .executes(context -> {
                            FabricClientCommandSource source = context.getSource();

                            source.sendFeedback(Text.literal("§aLifeTools §7- §2§lUtility features"));
                            source.sendFeedback(Text.literal("§8-------------------------------------------"));
                            source.sendFeedback(Text.literal(" §3§lSubcommands:"));
                            source.sendFeedback(ClickableChatHelper.createClickableText("    §8- §7§ldisconnect §8(disconnects you from the current server)", "/util disconnect"));
                            source.sendFeedback(ClickableChatHelper.createClickableText("    §8- §7§lgm <gamemode> §8(switches your gamemode if you have permissions)", "/util gm "));
                            source.sendFeedback(ClickableChatHelper.createClickableText("    §8- §7§llaunch §8(launches you)", "/util launch"));
                            source.sendFeedback(ClickableChatHelper.createClickableText("    §8- §7§lfullbright §8(toggles fullbright)", "/util fullbright"));
                            source.sendFeedback(ClickableChatHelper.createClickableText("    §8- §7§lnickname <name> §8(changes your minecraft username)", "/util nickname "));
                            source.sendFeedback(Text.literal("§8-------------------------------------------"));
                            return 1;
                        })
                        .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("disconnect")
                                .executes(context -> {
                                    new Disconnect().handleDisconnect(context.getSource());
                                    return 1;
                                }))
                        .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("gm")
                                .then(RequiredArgumentBuilder.<FabricClientCommandSource, String>argument("mode", StringArgumentType.word())
                                        .suggests((context, builder) -> {
                                            for (String gamemode : GAMEMODES) {
                                                builder.suggest(gamemode);
                                            }
                                            return builder.buildFuture();
                                        })
                                        .executes(context -> {
                                            new GamemodeSwitcher().handleGamemodeSwitch(context.getSource(), StringArgumentType.getString(context, "mode"));
                                            return 1;
                                        })))
                        .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("launch")
                                .executes(context -> {
                                    new Launch().handleLaunch(context.getSource());
                                    return 1;
                                }))
                        .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("fullbright")
                                .executes(context -> {
                                    new Fullbright().toggleFullbright();
                                    return 1;
                                }))
                        .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("nickname")
                                .then(RequiredArgumentBuilder.<FabricClientCommandSource, String>argument("name", StringArgumentType.word())
                                        .executes(context -> {
                                                new NameChanger().changeName(context.getSource(), StringArgumentType.getString(context, "name"));
                                            return 1;
                                        })))
        ));
    }
}
