package com.lifetools;

import com.lifetools.commandsystem.LifeToolsCmd;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.fabricmc.api.ClientModInitializer;

import java.util.HashMap;
import java.util.Map;

import static com.lifetools.LifeTools.*;

public class ClientEffects implements ClientModInitializer {

    private static final Map<String, RegistryEntry<StatusEffect>> EFFECT_MAP = new HashMap<>();




    public ClientEffects() {
        initializeEffectMap();
        registerCommands();
    }

    @Override
    public void onInitializeClient() {
        initializeEffectMap();
        registerCommands();
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

    private void registerCommands() {
        // Register the "clienteffect" base command
        LifeToolsCmd.addCmd("clienteffect", args -> {
            if (args.length == 0) {
                correctUsage();
                return;
            }

            String subCommand = args[0].toLowerCase();
            if ("give".equals(subCommand)) {
                handleGiveEffect(args);
            } else if ("clear".equals(subCommand)) {
                handleClearEffect(args);
            } else if ("list".equals(subCommand)) {
                handleListEffects();
            } else {
                correctUsage();
            }
        });
    }


    private void correctUsage() {
        sendFeedback(INFO_PREFIX + "Correct usage:\n"
                + "   §7!clienteffect give [effect] <value>\n"
                + "   §7!clienteffect clear [effect]\n"
                + "   §7!clienteffect list");
    }


    private void handleListEffects() {
        if (EFFECT_MAP.isEmpty()) {
            sendFeedback(INFO_PREFIX + "No effects found.");
            return;
        }

        StringBuilder effectsList = new StringBuilder(INFO_PREFIX + "Available Effects:\n");
        EFFECT_MAP.keySet().stream()
                .sorted() // Sort effects alphabetically
                .forEach(effectName -> effectsList.append("   §7").append(effectName).append("\n"));

        sendFeedback(effectsList.toString());
    }



    private void handleGiveEffect(String[] args) {
        if (args.length < 3) {
            sendFeedback(WARNING_PREFIX + "Usage: §7!clienteffect give [effect] <value>");
            return;
        }

        String effectName = args[1].toLowerCase();
        int value;

        try {
            value = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sendFeedback(ERROR_PREFIX + "§cInvalid value. Please enter a valid integer.");
            return;
        }

        RegistryEntry<StatusEffect> entry = EFFECT_MAP.get(effectName);
        if (entry != null) {
            applyEffect(entry, value, effectName);
        } else {
            sendFeedback(WARNING_PREFIX + "§6Invalid effect name");
        }
    }

    private void handleClearEffect(String[] args) {
        if (args.length == 1) {
            clearAllEffects();
        } else {
            String effectName = args[1].toLowerCase();
            RegistryEntry<StatusEffect> entry = EFFECT_MAP.get(effectName);
            if (entry != null) {
                removeEffect(entry, effectName);
            } else {
                sendFeedback(WARNING_PREFIX + "§6Invalid effect name");
            }
        }
    }

    private void applyEffect(RegistryEntry<StatusEffect> entry, int value, String effectName) {
        if (value < 1 || value > 255) {
            sendFeedback(WARNING_PREFIX + "Value must be between 1 and 255.");
            return;
        }

        int amplifier = (value - 1) / 2;

        if (MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().player.addStatusEffect(new StatusEffectInstance(entry, 600, amplifier));
            sendFeedback(String.format(INFO_PREFIX + "Effect §a%s §7has been set to §2%s§7",
                    formatEffectName(effectName), value));
        } else {
            sendFeedback(ERROR_PREFIX + "§cAn Error occurred.");
        }
    }

    private void removeEffect(RegistryEntry<StatusEffect> entry, String effectName) {
        if (MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().player.removeStatusEffect(entry);
            sendFeedback(String.format(INFO_PREFIX + "Effect §a%s §7has been cleared", formatEffectName(effectName)));
        } else {
            sendFeedback(ERROR_PREFIX + "§cAn Error occurred.");
        }
    }

    private void clearAllEffects() {
        if (MinecraftClient.getInstance().player != null) {
            for (RegistryEntry<StatusEffect> entry : EFFECT_MAP.values()) {
                MinecraftClient.getInstance().player.removeStatusEffect(entry);
            }
            sendFeedback(INFO_PREFIX + "All effects have been cleared");
        } else {
            sendFeedback(ERROR_PREFIX + "§cAn Error occurred.");
        }
    }

    private void sendFeedback(String message) {
        if (MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().player.sendMessage(Text.of(message), false);
        }
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
