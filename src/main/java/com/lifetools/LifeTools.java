package com.lifetools;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;

public class LifeTools implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register(this::registerCommands);
    }

    private void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        LiteralArgumentBuilder<FabricClientCommandSource> command = LiteralArgumentBuilder
                .<FabricClientCommandSource>literal("lifetools")
                .executes(this::run);

        dispatcher.register(command);
    }

    private int run(CommandContext<FabricClientCommandSource> context) {
        context.getSource().sendFeedback(Text.literal("§8[§2LifeTools§8] §7Running LifeTools §a1.2"));
        context.getSource().sendFeedback(Text.literal("§8-------------------------------------------"));
        context.getSource().sendFeedback(Text.literal("§7Current commands:"));
        context.getSource().sendFeedback(Text.literal("   §8- §7/fly"));
        context.getSource().sendFeedback(Text.literal("   §8- §7/flyspeed §a<1-30>"));
        context.getSource().sendFeedback(Text.literal("   §8- §7/tpmod §a<1-8>"));
        context.getSource().sendFeedback(Text.literal("   §8- §7/speed §a<1-20>"));
        context.getSource().sendFeedback(Text.literal("   §8- §7/speed §2reset"));
        context.getSource().sendFeedback(Text.literal("§8-------------------------------------------"));
        // Add more information or functionality as needed
        return 1;
    }
}
