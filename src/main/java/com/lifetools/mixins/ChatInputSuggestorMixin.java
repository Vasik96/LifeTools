package com.lifetools.mixins;

import com.lifetools.commandsystem.LifeToolsCmd;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@Mixin(ChatInputSuggestor.class)
public abstract class ChatInputSuggestorMixin {

    @Shadow
    @Final
    TextFieldWidget textField; // this is package-private, keep it that way.

    @Shadow
    private CompletableFuture<Suggestions> pendingSuggestions;

    @Shadow
    protected abstract void showCommandSuggestions();

    @Shadow
    @Final
    MinecraftClient client;

    @Unique
    private boolean updatingTextField = false;
    @Shadow
    @Nullable
    private ParseResults<ClientCommandSource> parse;

    @Shadow
    @Nullable
    private ChatInputSuggestor.SuggestionWindow window;

    @Unique
    private String lastInput = ""; // Track the previous input to avoid unnecessary updates

    @Inject(method = "refresh", at = @At("HEAD"), cancellable = true)
    private void injectCustomSuggestions(CallbackInfo ci) {
        if (updatingTextField) {
            return; // Avoid re-entering
        }

        String currentInput = textField.getText();

        if (currentInput.startsWith("!")) {
            if (!currentInput.equals(lastInput)) {
                // Prevent infinite recursion
                updatingTextField = true;
                try {
                    // Set the cursor color to gray
                    textField.setEditableColor(0xff808080);

                    // Create a gray color for the text
                    TextColor grayColor = TextColor.fromRgb(0x808080);
                    Style grayStyle = Style.EMPTY.withColor(grayColor);
                    Text styledText = Text.literal(currentInput).setStyle(grayStyle);

                    // Update the textField with styled text
                    textField.setText(styledText.getString());

                    // Track the last input to avoid unnecessary updates
                    lastInput = currentInput;
                } finally {
                    updatingTextField = false; // Ensure flag is reset
                }
            }

            String partialCommand = currentInput.substring(1);

            // Initialize parse if null or input changed
            if (this.parse == null || !this.parse.getReader().getString().equals(currentInput)) {
                StringReader stringReader = new StringReader(currentInput);
                assert this.client.player != null;
                // Use the client dispatcher type
                CommandDispatcher<ClientCommandSource> dispatcher = this.client.player.networkHandler.getCommandDispatcher();
                this.parse = dispatcher.parse(stringReader, this.client.player.networkHandler.getCommandSource());
            }


            // Fetch custom suggestions
            Collection<String> suggestions = LifeToolsCmd.getSuggestions(partialCommand);

            // Build Mojang-style suggestions
            int startOfCommand = currentInput.indexOf('!') + 1;
            SuggestionsBuilder builder = new SuggestionsBuilder(currentInput, startOfCommand);

            for (String suggestion : suggestions) {
                builder.suggest(suggestion);
            }

            Suggestions newSuggestions = builder.build();

            // Keep suggestions visible after auto-completion
            this.pendingSuggestions = CompletableFuture.completedFuture(newSuggestions);
            this.pendingSuggestions.thenRun(() -> {
                if (!newSuggestions.getList().isEmpty()) {
                    this.showCommandSuggestions();
                }
            });

            // Update placeholder in text field
            String firstSuggestion = newSuggestions.getList().isEmpty() ? null : newSuggestions.getList().getFirst().getText();
            if (firstSuggestion != null && !currentInput.equals("!" + firstSuggestion)) {
                textField.setSuggestion(firstSuggestion.substring(partialCommand.length()));
            } else {
                textField.setSuggestion(null);
            }

            // Prevent default Mojang behavior
            ci.cancel();
        }
    }
}