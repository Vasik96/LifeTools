package com.lifetools.mixins;

import com.lifetools.XrayConfig;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

    @Inject(at = @At(value = "HEAD"), method = "isAmbientOcclusionEnabled()Z", cancellable = true)
    private static void isAmbientOcclusionEnabled(CallbackInfoReturnable<Boolean> ci) {
        if (XrayConfig.ENABLED) { // Adjust this line based on your actual config method
            ci.setReturnValue(false);
            ci.cancel();
        }
    }
}
