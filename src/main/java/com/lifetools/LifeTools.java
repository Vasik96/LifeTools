package com.lifetools;

import com.lifetools.commandsystem.LifeToolsCmd;
import com.lifetools.util.Fullbright;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.MinecraftVersion;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;

import java.lang.reflect.Method;
import java.util.Optional;

public class LifeTools implements ClientModInitializer {

    private static final String MOD_ID = "lifetools";
    private String version;

    //used in messages
    public static final String INFO_PREFIX = "§8[§9Info§8] §7";
    public static final String ERROR_PREFIX = "§8[§cError§8] §7";
    public static final String WARNING_PREFIX = "§8[§eWarning§8] §7";

    public static boolean menu_shown = true;
    String gameVersion = MinecraftVersion.create().getName();

    @Override
    public void onInitializeClient() {
        registerFeatures();
        this.version = getModVersion().orElse("unknown");
        ClientCommandRegistrationCallback.EVENT.register(this::registerCommands);
    }

    private void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        // Register the command using the custom command system
        LifeToolsCmd.addCmd("lifetools", args -> run());
    }

    private void run() {
        // Get the Minecraft client and player
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            return; // Exit if the player is null (e.g., player is not in the game)
        }

        // Send the informational message with the list of available commands
        ClientPlayerEntity player = client.player;
        player.sendMessage(Text.literal(INFO_PREFIX + "Running LifeTools §a" + version + "§r§7, Minecraft: §a" + gameVersion), false);
        player.sendMessage(Text.literal("§8-------------------------------------------"), false);
        player.sendMessage(Text.literal(" §3§lCommands:"), false);

        // List commands with clickable links that will use the '!' instead of '/'
        player.sendMessage(ClickableChatHelper.createClickableText("    §8- §7!fly", "!fly"), false);
        player.sendMessage(ClickableChatHelper.createClickableText("    §8- §7!flyspeed §a<1-30>", "!flyspeed "), false);
        player.sendMessage(ClickableChatHelper.createClickableText("    §8- §7!tpmod §a<1-150>", "!tpmod "), false);
        player.sendMessage(ClickableChatHelper.createClickableText("    §8- §7!speed §a<1-20>", "!speed "), false);
        player.sendMessage(ClickableChatHelper.createClickableText("    §8- §7!speed §2reset", "!speed reset"), false);
        player.sendMessage(ClickableChatHelper.createClickableText("    §8- §7!nofall", "!nofall"), false);
        player.sendMessage(ClickableChatHelper.createClickableText("    §8- §7!boatfly", "!boatfly"), false);
        player.sendMessage(ClickableChatHelper.createClickableText("    §8- §7!clienteffect give §2<effect> §a<1-255>", "!clienteffect give "), false);
        player.sendMessage(ClickableChatHelper.createClickableText("    §8- §7!clienteffect clear §2<effect>", "!clienteffect clear "), false);
        player.sendMessage(ClickableChatHelper.createClickableText("    §8- §7!clienteffect clear", "!clienteffect clear"), false);
        player.sendMessage(ClickableChatHelper.createClickableText("    §8- §7!jesus", "!jesus"), false);
        player.sendMessage(ClickableChatHelper.createClickableText("    §8- §7!esp", "!esp"), false);
        player.sendMessage(ClickableChatHelper.createClickableText("    §8- §7!util", "!util"), false);
        player.sendMessage(ClickableChatHelper.createClickableText("    §8- §7!util <subcommand>", "!util "), false);
        player.sendMessage(ClickableChatHelper.createClickableText("    §8- §7!criticals", "!criticals"), false);
        player.sendMessage(ClickableChatHelper.createClickableText("    §8- §7!jetpack", "!jetpack"), false);

        player.sendMessage(Text.literal("§8-------------------------------------------"), false);
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
        registerFeature(AirJump.class);
        registerFeature(Strafe.class);
        registerFeature(BlockSlipperiness.class);
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
