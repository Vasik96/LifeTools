package com.lifetools.mixins;

import com.lifetools.screens.QuickConnectScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

    public TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void addQuickConnectButton(CallbackInfo ci) {
        int buttonHeight = 20;
        int spacing = 5;

        // Get the MinecraftClient instance to use the text renderer
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;

        // Define the button text and calculate its width
        Text quickConnectText = Text.literal("Quick Join");
        int buttonWidth = textRenderer.getWidth(quickConnectText) + 20; // Adding some padding for the button width

        // Find the existing Multiplayer button
        ButtonWidget multiplayerButton = this.children().stream()
                .filter(child -> child instanceof ButtonWidget)
                .map(child -> (ButtonWidget) child)
                .filter(button -> button.getMessage().getString().equals("Multiplayer"))
                .findFirst()
                .orElse(null);

        if (multiplayerButton != null) {
            // Add QuickConnect button next to the existing Multiplayer button
            ButtonWidget quickConnectButton = new ButtonWidget.Builder(
                    quickConnectText,
                    button -> client.setScreen(new QuickConnectScreen(this))
            )
                    .position(multiplayerButton.getX() + multiplayerButton.getWidth() + spacing, multiplayerButton.getY())
                    .size(buttonWidth, buttonHeight)
                    .build();

            this.addDrawableChild(quickConnectButton);
        }
    }
}
