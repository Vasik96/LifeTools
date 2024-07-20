package com.lifetools;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

import static com.lifetools.LifeTools.*;

public class ClientEffects implements ClientModInitializer {

    private static final Map<String, RegistryEntry<StatusEffect>> EFFECT_MAP = new HashMap<>();

    @Override
    public void onInitializeClient() {
        initializeEffectMap();
        ClientCommandRegistrationCallback.EVENT.register(this::registerCommands);
    }

    private void initializeEffectMap() {
        Registries.STATUS_EFFECT.stream().forEach(statusEffect -> {
            Identifier id = Registries.STATUS_EFFECT.getId(statusEffect);
            if (id != null) {
                String effectName = id.getPath();
                RegistryEntry<StatusEffect> entry = Registries.STATUS_EFFECT.getEntry(statusEffect);
                if (entry != null) {
                    EFFECT_MAP.put(effectName, entry);
                }
            }
        });
    }

    private void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        LiteralArgumentBuilder<FabricClientCommandSource> command = LiteralArgumentBuilder
                .<FabricClientCommandSource>literal("clienteffect")
                .executes(this::correctUsage)
                .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("clear")
                        .executes(this::clearEffects)
                        .then(com.mojang.brigadier.builder.RequiredArgumentBuilder.<FabricClientCommandSource, String>argument("effect", StringArgumentType.string())
                                .suggests(EFFECT_SUGGESTIONS)
                                .executes(this::clearSpecificEffect)))
                .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("give")
                        .then(com.mojang.brigadier.builder.RequiredArgumentBuilder.<FabricClientCommandSource, String>argument("effect", StringArgumentType.string())
                                .suggests(EFFECT_SUGGESTIONS)
                                .then(com.mojang.brigadier.builder.RequiredArgumentBuilder.<FabricClientCommandSource, Integer>argument("value", IntegerArgumentType.integer(1, 255))
                                        .executes(this::setEffect))));

        dispatcher.register(command);
    }

    private static final SuggestionProvider<FabricClientCommandSource> EFFECT_SUGGESTIONS = (context, builder) ->
            CommandSource.suggestMatching(EFFECT_MAP.keySet().stream(), builder);


    private int correctUsage(CommandContext<FabricClientCommandSource> context) {
        context.getSource().sendFeedback(Text.literal(INFO_PREFIX + "Correct usage:\n"
                + "   §7/clienteffect give [effect] <value>\n"
                + "   §7/clienteffect clear [effect]\n"
                + "   §7/clienteffect clear"));
        return 1;
    }


    private int clearEffects(CommandContext<FabricClientCommandSource> context) {
        FabricClientCommandSource source = context.getSource();
        if (source.getPlayer() != null) {
            for (RegistryEntry<StatusEffect> entry : EFFECT_MAP.values()) {
                source.getPlayer().removeStatusEffect(entry);
            }
            source.sendFeedback(Text.literal(INFO_PREFIX + "All effects have been cleared"));
        } else {
            source.sendFeedback(Text.literal(ERROR_PREFIX + "§cAn Error occurred"));
        }
        return 1;
    }

    private int clearSpecificEffect(CommandContext<FabricClientCommandSource> context) {
        FabricClientCommandSource source = context.getSource();
        if (source.getPlayer() != null) {
            String effectName = StringArgumentType.getString(context, "effect").toLowerCase();
            RegistryEntry<StatusEffect> entry = EFFECT_MAP.get(effectName);
            if (entry != null) {
                source.getPlayer().removeStatusEffect(entry);
                source.sendFeedback(Text.literal(String.format(INFO_PREFIX + "Effect §a%s §7has been cleared", formatEffectName(effectName))));
            } else {
                source.sendFeedback(Text.literal(WARNING_PREFIX + "§6Invalid effect name"));
            }
        } else {
            source.sendFeedback(Text.literal(ERROR_PREFIX + "§cAn Error occurred"));
        }
        return 1;
    }

    private int setEffect(CommandContext<FabricClientCommandSource> context) {
        FabricClientCommandSource source = context.getSource();
        if (source.getPlayer() != null) {
            String effectName = StringArgumentType.getString(context, "effect").toLowerCase();
            int value = IntegerArgumentType.getInteger(context, "value");

            RegistryEntry<StatusEffect> entry = EFFECT_MAP.get(effectName);
            if (entry != null) {
                source.getPlayer().removeStatusEffect(entry);

                int amplifier = (value - 1) / 2;
                source.getPlayer().addStatusEffect(new StatusEffectInstance(entry, 600, amplifier));
                source.sendFeedback(Text.literal(String.format(INFO_PREFIX + "Effect §a%s §7has been set to §2%s§7", formatEffectName(effectName), value)));
            } else {
                source.sendFeedback(Text.literal(WARNING_PREFIX + "§6Invalid effect name"));
            }
        } else {
            source.sendFeedback(Text.literal(ERROR_PREFIX + "§cAn Error occurred"));
        }
        return 1;
    }

    private String formatEffectName(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        String[] words = input.split("_");
        StringBuilder formattedName = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                formattedName.append(word.substring(0, 1).toUpperCase())
                        .append(word.substring(1))
                        .append(" ");
            }
        }
        return formattedName.toString().trim();
    }
}
