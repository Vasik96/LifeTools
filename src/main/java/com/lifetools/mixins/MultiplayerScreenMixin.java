package com.lifetools.mixins;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public abstract class MultiplayerScreenMixin extends Screen {

    protected MultiplayerScreenMixin(Text title) {
        super(title); // This constructor is protected and is not intended for use in mixins.
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        ButtonWidget changeNicknameButton = new ButtonWidget.Builder(Text.literal("Change Nickname"), (buttonWidget) -> {
            // Use this as the previous screen
            Screen previousScreen = this;
            // Create the NicknameChangeScreen with the current screen as the previous screen
            com.lifetools.screens.NicknameChangeScreen nicknameChangeScreen = new com.lifetools.screens.NicknameChangeScreen(previousScreen);
            assert this.client != null;
            this.client.setScreen(nicknameChangeScreen);
        })
                .dimensions(this.width - 110, 5, 100, 20) // Adjusted size and position
                .build();

        this.addDrawableChild(changeNicknameButton);
    }
}
