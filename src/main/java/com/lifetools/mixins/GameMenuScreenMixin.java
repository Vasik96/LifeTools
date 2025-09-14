package com.lifetools.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.lifetools.util.Disconnect.reason;

@Mixin(GameMenuScreen.class)
abstract class GameMenuScreenMixin extends Screen {

    protected GameMenuScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "initWidgets", at = @At("TAIL"))
    private void addCustomButton(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return; // Guard clause to prevent null pointer exception
        }

        final Screen parentScreen = client.currentScreen != null ? client.currentScreen : new TitleScreen();

        this.addDrawableChild(new ButtonWidget.Builder(
                Text.literal("Multiplayer"),
                button -> {
                    // Open the Multiplayer screen with the current screen as the parent
                    client.setScreen(new MultiplayerScreen(parentScreen) {
                        @Override
                        public void close() {
                            assert client != null;
                            if (client.currentScreen instanceof MultiplayerScreen) {
                                if (client.player == null) {
                                    // Player is null, disconnect from server and return to the TitleScreen
                                    client.disconnect(new DisconnectedScreen(null, title, Text.of(reason)), false);
                                    client.setScreen(new TitleScreen());
                                } else if (client.currentScreen == parentScreen) {
                                    client.setScreen(parentScreen);
                                } else {
                                    super.close();
                                }
                            }
                        }
                    });
                })
                .dimensions(10, 10, 150, 20) // Set position and size
                .build()
        );
    }
}
