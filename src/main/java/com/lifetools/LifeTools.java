package com.lifetools;

import com.lifetools.util.Fullbright;
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

import java.lang.reflect.Method;
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
        registerFeatures();
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
        context.getSource().sendFeedback(Text.literal(INFO_PREFIX + "Running LifeTools §a" + version + "§r§7, Minecraft: §a" + gameVersion));
        context.getSource().sendFeedback(Text.literal("§8-------------------------------------------"));
        context.getSource().sendFeedback(Text.literal(" §3§lCommands:"));

        context.getSource().sendFeedback(ClickableChatHelper.createClickableText("    §8- §7/fly", "/fly"));
        context.getSource().sendFeedback(ClickableChatHelper.createClickableText("    §8- §7/flyspeed §a<1-30>", "/flyspeed "));
        context.getSource().sendFeedback(ClickableChatHelper.createClickableText("    §8- §7/tpmod §a<1-150>", "/tpmod "));
        context.getSource().sendFeedback(ClickableChatHelper.createClickableText("    §8- §7/speed §a<1-20>", "/speed "));
        context.getSource().sendFeedback(ClickableChatHelper.createClickableText("    §8- §7/speed §2reset", "/speed reset"));
        context.getSource().sendFeedback(ClickableChatHelper.createClickableText("    §8- §7/nofall", "/nofall"));
        context.getSource().sendFeedback(ClickableChatHelper.createClickableText("    §8- §7/boatfly", "/boatfly"));
        context.getSource().sendFeedback(ClickableChatHelper.createClickableText("    §8- §7/clienteffect give §2<effect> §a<1-255>", "/clienteffect give "));
        context.getSource().sendFeedback(ClickableChatHelper.createClickableText("    §8- §7/clienteffect clear §2<effect>", "/clienteffect clear "));
        context.getSource().sendFeedback(ClickableChatHelper.createClickableText("    §8- §7/clienteffect clear", "/clienteffect clear"));
        context.getSource().sendFeedback(ClickableChatHelper.createClickableText("    §8- §7/jesus", "/jesus"));
        context.getSource().sendFeedback(ClickableChatHelper.createClickableText("    §8- §7/esp", "/esp"));
        context.getSource().sendFeedback(ClickableChatHelper.createClickableText("    §8- §7/util", "/util"));
        context.getSource().sendFeedback(ClickableChatHelper.createClickableText("    §8- §7/util <subcommand>", "/util "));
        context.getSource().sendFeedback(ClickableChatHelper.createClickableText("    §8- §7/criticals", "/criticals"));
        context.getSource().sendFeedback(ClickableChatHelper.createClickableText("    §8- §7/jetpack", "/jetpack"));


        context.getSource().sendFeedback(Text.literal("§8-------------------------------------------"));
        return 1;
    }

    private void registerFeatures() {
        registerFeature(NoFall.class);
        registerFeature(Jetpack.class);
        registerFeature(KillAura.class);
        registerFeature(Jesus.class);
        registerFeature(ESP.class);
        registerFeature(Criticals.class);
        registerFeature(BoatFly.class);
        registerFeature(Fly.class);
        registerFeature(Scaffold.class);
        registerFeature(Reach.class);
        registerFeature(Xray.class);
        registerFeature(Fullbright.class);
    }

    private void registerFeature(Class<?> featureClass) {
        for (Method method : featureClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(com.lifetools.annotations.Feature.class)) {
                com.lifetools.annotations.Feature feature = method.getAnnotation(com.lifetools.annotations.Feature.class);
                FeatureRegistry.registerFeatureMethod(feature.methodName(), feature.featureName(), method);
                System.out.println("Registered feature: " + feature.featureName());
            }
        }
    }

    private Optional<String> getModVersion() {
        Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(LifeTools.MOD_ID);
        return modContainer.map(container -> container.getMetadata().getVersion().getFriendlyString());
    }
}
