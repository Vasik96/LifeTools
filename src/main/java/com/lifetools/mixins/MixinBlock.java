package com.lifetools.mixins;

import com.lifetools.XrayConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class MixinBlock {

    @Inject(at = @At("RETURN"), method = "shouldDrawSide", cancellable = true)
    private static void shouldDrawSide(BlockState state, BlockView reader, BlockPos pos, Direction face, BlockPos otherPos, CallbackInfoReturnable<Boolean> ci) {
        if (XrayConfig.ENABLED) {
            if (isXrayBlock(state)) {
                ci.setReturnValue(true);
                return;
            }
            ci.setReturnValue(false);
        }
    }

    @Unique
    private static boolean isXrayBlock(BlockState state) {
        return state.getBlock() == Blocks.DIAMOND_ORE ||
                state.getBlock() == Blocks.GOLD_ORE ||
                state.getBlock() == Blocks.IRON_ORE ||
                state.getBlock() == Blocks.COAL_ORE ||
                state.getBlock() == Blocks.COPPER_ORE ||
                state.getBlock() == Blocks.REDSTONE_ORE ||
                state.getBlock() == Blocks.LAPIS_ORE ||
                state.getBlock() == Blocks.EMERALD_ORE ||
                state.getBlock() == Blocks.DEEPSLATE_DIAMOND_ORE ||
                state.getBlock() == Blocks.DEEPSLATE_GOLD_ORE ||
                state.getBlock() == Blocks.DEEPSLATE_IRON_ORE ||
                state.getBlock() == Blocks.DEEPSLATE_COAL_ORE ||
                state.getBlock() == Blocks.DEEPSLATE_COPPER_ORE ||
                state.getBlock() == Blocks.DEEPSLATE_REDSTONE_ORE ||
                state.getBlock() == Blocks.DEEPSLATE_LAPIS_ORE ||
                state.getBlock() == Blocks.DEEPSLATE_EMERALD_ORE ||
                state.getBlock() == Blocks.LAVA ||
                state.getBlock() == Blocks.SPAWNER ||
                state.getBlock() == Blocks.TRIAL_SPAWNER ||
                state.getBlock() == Blocks.END_PORTAL_FRAME ||
                state.getBlock() == Blocks.END_PORTAL ||
                state.getBlock() == Blocks.NETHER_PORTAL ||
                state.getBlock() == Blocks.BEDROCK ||
                state.getBlock() == Blocks.BARREL ||
                state.getBlock() == Blocks.CHEST ||
                state.getBlock() == Blocks.TRAPPED_CHEST ||
                state.getBlock() == Blocks.NETHER_QUARTZ_ORE ||
                state.getBlock() == Blocks.NETHER_GOLD_ORE ||
                state.getBlock() == Blocks.GILDED_BLACKSTONE||


                state.getBlock() == Blocks.WHITE_SHULKER_BOX ||
                state.getBlock() == Blocks.ORANGE_SHULKER_BOX ||
                state.getBlock() == Blocks.MAGENTA_SHULKER_BOX ||
                state.getBlock() == Blocks.LIGHT_BLUE_SHULKER_BOX ||
                state.getBlock() == Blocks.YELLOW_SHULKER_BOX ||
                state.getBlock() == Blocks.LIME_SHULKER_BOX ||
                state.getBlock() == Blocks.PINK_SHULKER_BOX ||
                state.getBlock() == Blocks.GRAY_SHULKER_BOX ||
                state.getBlock() == Blocks.LIGHT_GRAY_SHULKER_BOX ||
                state.getBlock() == Blocks.CYAN_SHULKER_BOX ||
                state.getBlock() == Blocks.PURPLE_SHULKER_BOX ||
                state.getBlock() == Blocks.BLUE_SHULKER_BOX ||
                state.getBlock() == Blocks.BROWN_SHULKER_BOX ||
                state.getBlock() == Blocks.GREEN_SHULKER_BOX ||
                state.getBlock() == Blocks.RED_SHULKER_BOX ||
                state.getBlock() == Blocks.BLACK_SHULKER_BOX ||
                state.getBlock() == Blocks.SHULKER_BOX ||

                state.getBlock() == Blocks.ANCIENT_DEBRIS;
    }
}
