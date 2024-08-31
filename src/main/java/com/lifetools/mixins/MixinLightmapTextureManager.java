package com.lifetools.mixins;

import com.lifetools.XrayConfig;
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
    private static final double FULLBRIGHT_GAMMA = 1.0; // Fullbright gamma level
    @Unique
    private static final double NORMAL_GAMMA = 0.5; // Default gamma level

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;getGamma()Lnet/minecraft/client/option/SimpleOption;", opcode = Opcodes.INVOKEVIRTUAL), method = "update(F)V")
    private SimpleOption<Double> redirectGamma(GameOptions options) {
        // Create a SimpleOption for gamma adjustment
        return new SimpleOption<>(
                "options.gamma",
                SimpleOption.emptyTooltip(),
                (optionText, value) -> optionText,
                SimpleOption.DoubleSliderCallbacks.INSTANCE,
                XrayConfig.FULLBRIGHT ? FULLBRIGHT_GAMMA : NORMAL_GAMMA,
                value -> {} // No-op lambda for slider value callback
        );
    }
}
