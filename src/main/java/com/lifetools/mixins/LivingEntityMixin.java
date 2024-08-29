package com.lifetools.mixins;

import com.lifetools.ESP;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "isGlowing", at = @At("HEAD"), cancellable = true)
    public void isGlowing(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (ESP.isEspEnabled() && ESP.shouldGlow(entity)) {
            cir.setReturnValue(true);
        } else if (!ESP.isEspEnabled()) {
            // Set to false and let vanilla logic handle the rest
            cir.setReturnValue(false);
            cir.cancel(); // Cancel the original method call
        }
    }
}
