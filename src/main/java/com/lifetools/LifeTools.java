package com.lifetools;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;

import java.util.Optional;

public class LifeTools implements ClientModInitializer {

    private static final String MOD_ID = "lifetools";
    private String version;

    @Override
    public void onInitializeClient() {
        this.version = getModVersion().orElse("unknown");
        ClientCommandRegistrationCallback.EVENT.register(this::registerCommands);
    }

    private void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        LiteralArgumentBuilder<FabricClientCommandSource> command = LiteralArgumentBuilder
                .<FabricClientCommandSource>literal("lifetools")
                .executes(this::run);

        dispatcher.register(command);
    }

    private int run(CommandContext<FabricClientCommandSource> context) {
        context.getSource().sendFeedback(Text.literal("§8[§2LifeTools§8] §7Running LifeTools §a" + version));
        context.getSource().sendFeedback(Text.literal("§8-------------------------------------------"));
        context.getSource().sendFeedback(Text.literal("§7Current commands:"));
        context.getSource().sendFeedback(Text.literal("   §8- §7/fly"));
        context.getSource().sendFeedback(Text.literal("   §8- §7/flyspeed §a<1-30>"));
        context.getSource().sendFeedback(Text.literal("   §8- §7/tpmod §a<1-8>"));
        context.getSource().sendFeedback(Text.literal("   §8- §7/speed §a<1-20>"));
        context.getSource().sendFeedback(Text.literal("   §8- §7/speed §2reset"));
        context.getSource().sendFeedback(Text.literal("§8-------------------------------------------"));
        return 1;
    }

    private Optional<String> getModVersion() {
        Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(LifeTools.MOD_ID);
        return modContainer.map(container -> container.getMetadata().getVersion().getFriendlyString());
    }
}
