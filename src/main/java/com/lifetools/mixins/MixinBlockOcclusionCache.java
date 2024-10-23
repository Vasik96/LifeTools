package com.lifetools.mixins;

import com.lifetools.RendererInfo;
import com.lifetools.XrayList;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.lifetools.Xray.xrayEnabled;

@Pseudo
@Mixin(targets = "me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockOcclusionCache")
public class MixinBlockOcclusionCache {

    static {
        RendererInfo.setCurrentRenderer("§aSodium");
    }

    @Inject(at = @At("HEAD"), method = "shouldDrawSide", cancellable = true)
    private void shouldDrawSide(BlockState state, BlockView reader, BlockPos pos, Direction face, CallbackInfoReturnable<Boolean> ci) {
        if (xrayEnabled) {
            if (XrayList.isXrayBlock(state.getBlock())) {
                // Force the side to be rendered if it's an X-ray block
                ci.setReturnValue(true);
                ci.cancel();
            } else {
                // Hide non-X-ray blocks
                ci.setReturnValue(false);
                ci.cancel();
            }
        }
    }
}
