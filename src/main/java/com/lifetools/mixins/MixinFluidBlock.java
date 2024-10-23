package com.lifetools.mixins;

import com.lifetools.Jesus;
import net.minecraft.block.BlockState;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FluidBlock.class)
public class MixinFluidBlock {

    @Inject(method = "getCollisionShape", at = @At("HEAD"), cancellable = true)
    public void getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        // Check if Jesus mode is enabled and if the collision context is for a player
        if (Jesus.isJesusModeEnabled() && context instanceof EntityShapeContext entityContext) {
            if (entityContext.getEntity() != null && entityContext.getEntity().isPlayer()) {
                // Get the player's position
                double playerX = entityContext.getEntity().getX();
                double playerY = entityContext.getEntity().getY();
                double playerZ = entityContext.getEntity().getZ();

                // Calculate the 4x4x6 area around the player (increased vertical range for falling)
                double minX = playerX - 2.0;
                double maxX = playerX + 2.0;
                double minY = playerY - 2.0;
                double maxY = playerY + 4.0; // Larger vertical range for falling
                double minZ = playerZ - 2.0;
                double maxZ = playerZ + 2.0;

                // Ensure that only fluid blocks in this 4x4 area are treated as solid
                if (pos.getX() >= minX && pos.getX() <= maxX &&
                        pos.getY() >= minY && pos.getY() <= maxY &&
                        pos.getZ() >= minZ && pos.getZ() <= maxZ) {

                    // Check if the block at this position is a fluid block
                    if (state.getBlock() instanceof FluidBlock) {
                        // Treat the fluid block as solid, excluding air or other non-fluid blocks
                        cir.setReturnValue(VoxelShapes.fullCube());
                    }
                }
            }
        }
    }
}
