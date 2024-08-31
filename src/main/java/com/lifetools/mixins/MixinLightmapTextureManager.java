package com.lifetools.mixins;

import com.lifetools.XrayConfig;
import com.lifetools.util.Fullbright;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.LightmapTextureManager;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LightmapTextureManager.class)
public class MixinLightmapTextureManager {

    @Unique
    private static final double MAX_GAMMA = 10000.0; // Maximum gamma level for Xray
    @Unique
    private static final double NORMAL_GAMMA = 0.5; // Default gamma level

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;getGamma()Lnet/minecraft/client/option/SimpleOption;", opcode = Opcodes.INVOKEVIRTUAL), method = "update(F)V")
    private SimpleOption<Double> redirectGamma(GameOptions options) {
        if (XrayConfig.ENABLED) {
            // When Xray is enabled, set gamma to maximum
            return new SimpleOption<>(
                    "options.gamma",
                    SimpleOption.emptyTooltip(),
                    (optionText, value) -> optionText,
                    SimpleOption.DoubleSliderCallbacks.INSTANCE,
                    MAX_GAMMA,
                    value -> {}
            );
        } else {
            // When Xray is disabled, check if Fullbright is enabled
            if (Fullbright.isFullbright) {
                // Keep fullbright gamma level when Fullbright is enabled
                return new SimpleOption<>(
                        "options.gamma",
                        SimpleOption.emptyTooltip(),
                        (optionText, value) -> optionText,
                        SimpleOption.DoubleSliderCallbacks.INSTANCE,
                        MAX_GAMMA,
                        value -> {}
                );
            } else {
                // Apply default gamma when Xray and Fullbright are both off
                return new SimpleOption<>(
                        "options.gamma",
                        SimpleOption.emptyTooltip(),
                        (optionText, value) -> optionText,
                        SimpleOption.DoubleSliderCallbacks.INSTANCE,
                        NORMAL_GAMMA,
                        value -> {}
                );
            }
        }
    }
}
