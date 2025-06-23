package com.lifetools.commandsystem;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class LifeToolsCmd implements ClientModInitializer {

    private static final Map<String, Command> commands = new HashMap<>();

    @Override
    public void onInitializeClient() {
        // Intercept chat messages
        ClientSendMessageEvents.ALLOW_CHAT.register((message) -> {
            if (message.startsWith("!")) {
                // Treat any message starting with "!" as a command
                if (!executeCommand(message)) {
                    // Send "Unknown command" message if command is not registered
                    sendUnknownCommandMessage(message);
                }
                return false; // Cancel the chat message
            }
            return true; // Allow normal chat messages
        });

        // Example command with suggestions enabled (default behavior)
        LifeToolsCmd.addCmd("help", args -> {
            MinecraftClient.getInstance().player.sendMessage(Text.of("this is an example"), false);
        });
    }

    public static void addCmd(String command, Consumer<String[]> action) {
        addCmd(command, action, true); // Default: suggestions enabled
    }

    public static void addCmd(String command, Consumer<String[]> action, boolean suggestionsEnabled) {
        commands.put(command.toLowerCase(), new Command(action, suggestionsEnabled));
    }


    public static boolean isValidCommand(String command) {
        // Implement your logic to check if a command is valid
        return getSuggestions(command).contains(command);
    }


    public static Collection<String> getSuggestions(String partialCommand) {
        String lowerPartial = partialCommand.toLowerCase();
        if (lowerPartial.isEmpty()) {
            return commands.entrySet().stream()
                    .filter(entry -> entry.getValue().isSuggestionsEnabled())
                    .map(Map.Entry::getKey)
                    .toList(); // Return all commands with suggestions enabled if no specific input
        }
        return commands.entrySet().stream()
                .filter(entry -> entry.getValue().isSuggestionsEnabled() && entry.getKey().startsWith(lowerPartial))
                .map(Map.Entry::getKey)
                .toList();
    }

    public static boolean executeCommand(String input) {
        String[] parts = input.substring(1).split(" ");
        if (parts.length == 0) return false;

        String mainCommand = parts[0].toLowerCase();
        Command cmd = commands.get(mainCommand);

        if (cmd != null) {
            String[] args = new String[parts.length - 1];
            System.arraycopy(parts, 1, args, 0, args.length);
            cmd.execute(args);
            return true;
        }

        return false;
    }

    private static void sendUnknownCommandMessage(String input) {
        MinecraftClient.getInstance().player.sendMessage(Text.of("§cUnknown command: §7" + input), false);
    }

    private static class Command {
        private final Consumer<String[]> action;
        private final boolean suggestionsEnabled;

        public Command(Consumer<String[]> action, boolean suggestionsEnabled) {
            this.action = action;
            this.suggestionsEnabled = suggestionsEnabled;
        }

        public void execute(String[] args) {
            action.accept(args);
        }

        public boolean isSuggestionsEnabled() {
            return suggestionsEnabled;
        }
    }
}
