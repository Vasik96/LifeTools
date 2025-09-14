/*
package com.lifetools.mixins;

import com.lifetools.XrayList;
import com.lifetools.RendererInfo;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.vulkanmod.render.chunk.build.thread.BuilderResources;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.vulkanmod.render.vertex.TerrainRenderType;
import net.vulkanmod.render.vertex.TerrainBuilder;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;

import static com.lifetools.Xray.xrayEnabled;

@Mixin(targets = "net.vulkanmod.render.chunk.build.renderer.BlockRenderer")
public class MixinVulkanBlockRenderer {

    @Mutable
    @Shadow @Final
    boolean backFaceCulling;

    @Shadow private BuilderResources resources;

    // Static block to set the renderer name when the mixin is loaded
    static {
        RendererInfo.setCurrentRenderer("§cVulkan"); // Set the current renderer to Vulkan
    }

    @Inject(at = @At("HEAD"), method = "renderBlock", cancellable = true)
    private void renderBlock(BlockState blockState, BlockPos blockPos, Vector3f pos, CallbackInfo ci) {
        if (xrayEnabled) {
            // Disable back-face culling to render all sides
            this.backFaceCulling = false;

            if (XrayList.isXrayBlock(blockState.getBlock())) {
                // Modify the render type to ensure transparency for X-ray blocks
                TerrainRenderType renderType = TerrainRenderType.get(BlendMode.SOLID.blockRenderLayer);

                // Get the TerrainBuilder for this render type (TRANSLUCENT)
                TerrainBuilder terrainBuilder = this.resources.builderPack.builder(renderType);
                terrainBuilder.setBlockAttributes(blockState);

                // Continue rendering the block normally
            } else {
                // Cancel rendering for non-X-ray blocks
                ci.cancel();
            }
        }
    }
}
*/