package com.lifetools.mixins;

import com.lifetools.XrayConfig;
import com.lifetools.XrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class MixinBlock {

    @Inject(at = @At("RETURN"), method = "shouldDrawSide", cancellable = true)
    private static void shouldDrawSide(BlockState state, BlockView reader, BlockPos pos, Direction face, BlockPos otherPos, CallbackInfoReturnable<Boolean> ci) {
        if (XrayConfig.ENABLED) {
            if (XrayList.isXrayBlock(state.getBlock())) {
                ci.setReturnValue(true);
                return;
            }
            ci.setReturnValue(false);
        }
    }
}
