package com.lifetools.mixins;

import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(SimpleOption.DoubleSliderCallbacks.class)
public class DoubleSliderCallbacksMixin {

    @Inject(method = "validate(Ljava/lang/Double;)Ljava/util/Optional;", at = @At("RETURN"), cancellable = true)
    public void removeValidation(Double value, CallbackInfoReturnable<Optional<Double>> cir) {
        // Allow any double value by bypassing validation
        cir.setReturnValue(value == null ? Optional.empty() : Optional.of(value));
    }
}
