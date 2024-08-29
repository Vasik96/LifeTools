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
import net.minecraft.MinecraftVersion;

import java.util.Optional;

public class LifeTools implements ClientModInitializer {

    private static final String MOD_ID = "lifetools";
    private String version;


    //used in messages
    public static final String INFO_PREFIX = "§8[§9Info§8] §7";
    public static final String ERROR_PREFIX = "§8[§cError§8] §7";
    public static final String WARNING_PREFIX = "§8[§eWarning§8] §7";



    String gameVersion = MinecraftVersion.create().getName();

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
        context.getSource().sendFeedback(Text.literal("§8[§9Info§8] §7Running LifeTools §a" + version + "§r§7, Minecraft: §a" + gameVersion));
        context.getSource().sendFeedback(Text.literal("§8-------------------------------------------"));
        context.getSource().sendFeedback(Text.literal(" §3§lCommands:"));
        context.getSource().sendFeedback(Text.literal("    §8- §7/fly"));
        context.getSource().sendFeedback(Text.literal("    §8- §7/flyspeed §a<1-30>"));
        context.getSource().sendFeedback(Text.literal("    §8- §7/tpmod §a<1-150>"));
        context.getSource().sendFeedback(Text.literal("    §8- §7/speed §a<1-20>"));
        context.getSource().sendFeedback(Text.literal("    §8- §7/speed §2reset"));
        context.getSource().sendFeedback(Text.literal("    §8- §7/nofall"));
        context.getSource().sendFeedback(Text.literal("    §8- §7/boatfly"));
        context.getSource().sendFeedback(Text.literal("    §8- §7/clienteffect give §2<effect> §a<1-255>"));
        context.getSource().sendFeedback(Text.literal("    §8- §7/clienteffect clear §2<effect>"));
        context.getSource().sendFeedback(Text.literal("    §8- §7/clienteffect clear"));
        context.getSource().sendFeedback(Text.literal("    §8- §7/jesus"));
        context.getSource().sendFeedback(Text.literal("    §8- §7/esp"));
        context.getSource().sendFeedback(Text.literal("    §8- §7/util"));
        context.getSource().sendFeedback(Text.literal("    §8- §7/util <subcommand>"));
        context.getSource().sendFeedback(Text.literal("§8-------------------------------------------"));
        return 1;
    }

    private Optional<String> getModVersion() {
        Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(LifeTools.MOD_ID);
        return modContainer.map(container -> container.getMetadata().getVersion().getFriendlyString());
    }
}
