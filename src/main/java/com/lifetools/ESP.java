package com.lifetools;

import com.lifetools.commandsystem.LifeToolsCmd;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.BreezeEntity;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.joml.Matrix4f;

import static com.lifetools.LifeTools.INFO_PREFIX;


public class ESP implements ClientModInitializer {

    public static boolean isEspEnabled = false; // Track ESP state

    @Override
    public void onInitializeClient() {
        registerCommands();
        WorldRenderEvents.AFTER_TRANSLUCENT.register(ESP::renderESPBoxes);
    }

    private void registerCommands() {
        LifeToolsCmd.addCmd("esp", args -> toggleEsp());
    }


    public void toggleEsp() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;

        if (player == null) return;

        isEspEnabled = !isEspEnabled;
        String message = isEspEnabled ? "ESP has been §aenabled" : "ESP has been §cdisabled";
        player.sendMessage(Text.literal(INFO_PREFIX + message), false);
    }


    private static void renderESPBoxes(WorldRenderContext context) {
        if (!isEspEnabled) return;

        MatrixStack matrices = context.matrixStack();
        Camera camera = context.camera();

        if (matrices == null || camera == null) return; // Prevent NullPointerException

        matrices.push();

        MinecraftClient client = MinecraftClient.getInstance();
        assert client.world != null;


        float tickDelta = client.getRenderTickCounter().getTickDelta(true);

        client.world.getEntities().forEach(entity -> {
            if (entity == null) return;

            if (
                            !(entity instanceof EnderDragonEntity) &&
                            !(entity instanceof WitherEntity) &&
                            !(entity instanceof PlayerEntity) && // this also includes otherplayerentity and clientplayerentity
                            !(entity instanceof ElderGuardianEntity) &&
                            !(entity instanceof BreezeEntity)) {
                return; // Return if the entity is not one of the specified types
            }

            // Skip the local player (or other entities you don't want)
            if (entity == client.player) return;

            // Get entity position with interpolation for smoother movement
            double prevX = entity.prevX;
            double prevY = entity.prevY;
            double prevZ = entity.prevZ;

            // Interpolate entity's position based on tickDelta
            double interpolatedX = prevX + (entity.getX() - prevX) * tickDelta;
            double interpolatedY = prevY + (entity.getY() - prevY) * tickDelta;
            double interpolatedZ = prevZ + (entity.getZ() - prevZ) * tickDelta;

            // Apply the correct transformations for the entity's position
            matrices.push();
            RenderSystem.disableDepthTest();

            matrices.translate(interpolatedX - camera.getPos().x, interpolatedY - camera.getPos().y, interpolatedZ - camera.getPos().z);

            // Get position matrix for rendering
            Matrix4f positionMatrix = matrices.peek().getPositionMatrix();

            // Initialize Tessellator and BufferBuilder for line strip (outline)
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

            var boundingBox = entity.getBoundingBox();

            // Define cube edges for the outline (green edges)
            float halfWidth = (float) (boundingBox.getLengthX() / 2.0);  // Half of the box's width
            float height = (float) boundingBox.getLengthY();

            final int red = 255;
            final int green = 255;
            final int blue = 255;

            // Define the 12 edges of the cube (using line strips)
            // Front face edges
            buffer.vertex(positionMatrix, -halfWidth, 0, -halfWidth).color(red, green, blue, 255);  // Front bottom-left
            buffer.vertex(positionMatrix, halfWidth, 0, -halfWidth).color(red, green, blue, 255);   // Front bottom-right

            buffer.vertex(positionMatrix, halfWidth, 0, -halfWidth).color(red, green, blue, 255);   // Front bottom-right
            buffer.vertex(positionMatrix, halfWidth, height, -halfWidth).color(red, green, blue, 255); // Front top-right

            buffer.vertex(positionMatrix, halfWidth, height, -halfWidth).color(red, green, blue, 255); // Front top-right
            buffer.vertex(positionMatrix, -halfWidth, height, -halfWidth).color(red, green, blue, 255); // Front top-left

            buffer.vertex(positionMatrix, -halfWidth, height, -halfWidth).color(red, green, blue, 255); // Front top-left
            buffer.vertex(positionMatrix, -halfWidth, 0, -halfWidth).color(red, green, blue, 255);  // Front bottom-left

// Back face edges
            buffer.vertex(positionMatrix, -halfWidth, 0, halfWidth).color(red, green, blue, 255);  // Back bottom-left
            buffer.vertex(positionMatrix, halfWidth, 0, halfWidth).color(red, green, blue, 255);   // Back bottom-right

            buffer.vertex(positionMatrix, halfWidth, 0, halfWidth).color(red, green, blue, 255);   // Back bottom-right
            buffer.vertex(positionMatrix, halfWidth, height, halfWidth).color(red, green, blue, 255); // Back top-right

            buffer.vertex(positionMatrix, halfWidth, height, halfWidth).color(red, green, blue, 255); // Back top-right
            buffer.vertex(positionMatrix, -halfWidth, height, halfWidth).color(red, green, blue, 255); // Back top-left

            buffer.vertex(positionMatrix, -halfWidth, height, halfWidth).color(red, green, blue, 255); // Back top-left
            buffer.vertex(positionMatrix, -halfWidth, 0, halfWidth).color(red, green, blue, 255);  // Back bottom-left

// Vertical edges connecting front and back faces
            buffer.vertex(positionMatrix, -halfWidth, 0, -halfWidth).color(red, green, blue, 255);  // Front bottom-left
            buffer.vertex(positionMatrix, -halfWidth, 0, halfWidth).color(red, green, blue, 255);   // Back bottom-left

            buffer.vertex(positionMatrix, halfWidth, 0, -halfWidth).color(red, green, blue, 255);   // Front bottom-right
            buffer.vertex(positionMatrix, halfWidth, 0, halfWidth).color(red, green, blue, 255);    // Back bottom-right

            buffer.vertex(positionMatrix, -halfWidth, height, -halfWidth).color(red, green, blue, 255); // Front top-left
            buffer.vertex(positionMatrix, -halfWidth, height, halfWidth).color(red, green, blue, 255);  // Back top-left

            buffer.vertex(positionMatrix, halfWidth, height, -halfWidth).color(red, green, blue, 255); // Front top-right
            buffer.vertex(positionMatrix, halfWidth, height, halfWidth).color(red, green, blue, 255);  // Back top-right

            // Finalizing and rendering the lines
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);
            RenderSystem.setShaderColor((float) red, (float) green, (float) blue, 1.0F); // Green color for the cube edges

            // Draw the cube
            BufferRenderer.drawWithGlobalProgram(buffer.end());

            // Reset the shader color to avoid affecting other rendering operations
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            matrices.pop(); // Restore previous matrix state after rendering
        });

        matrices.pop(); // Restore original matrix state after the loop
        RenderSystem.enableDepthTest();
    }




}