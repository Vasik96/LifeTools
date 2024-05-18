package com.lifetools.mixins;

import com.lifetools.Xray;
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
public class XrayMixin {
    @Inject(at = @At("RETURN"), method = "shouldDrawSide(" + "Lnet/minecraft/block/BlockState;" + // state
            "Lnet/minecraft/world/BlockView;" + // reader
            "Lnet/minecraft/util/math/BlockPos;" + // pos
            "Lnet/minecraft/util/math/Direction;" + // face
            "Lnet/minecraft/util/math/BlockPos;" + // blockPosaaa
            ")Z", // ci
            cancellable = true)
    private static void shouldDrawSide(BlockState state, BlockView reader, BlockPos pos, Direction face,
                                       BlockPos blockPos, CallbackInfoReturnable<Boolean> ci) {
        Xray.shouldSideBeRendered(state, reader, pos, face, ci);
    }
}
