package com.lifetools;

import com.lifetools.commandsystem.LifeToolsCmd;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.platform.PolygonMode;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.BreezeEntity;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer.MultiPhaseParameters;
import net.minecraft.client.render.RenderPhase;
import org.lwjgl.opengl.GL11;

import java.util.Optional;
import java.util.OptionalDouble;

import static com.lifetools.LifeTools.INFO_PREFIX;
import static net.minecraft.client.gl.RenderPipelines.GLOBALS_SNIPPET;
import static net.minecraft.client.gl.RenderPipelines.TRANSFORMS_PROJECTION_FOG_SNIPPET;


public class ESP implements ClientModInitializer {

    public static boolean isEspEnabled = false; // Track ESP state

    // First, get the standard LINES pipeline shaders
    static Identifier vs = RenderPipelines.LINES.getVertexShader();
    static Identifier fs = RenderPipelines.LINES.getFragmentShader();
    static Identifier LINES_NO_DEPTH_ID = Identifier.of("lifetools", "lines_no_depth");

    // Build a new pipeline
    static RenderPipeline.Snippet RENDERTYPE_LINES_SNIPPET = RenderPipeline.builder(
                    TRANSFORMS_PROJECTION_FOG_SNIPPET,
                    GLOBALS_SNIPPET)
            .withVertexShader(vs)
            .withFragmentShader(fs)
            .withBlend(BlendFunction.TRANSLUCENT)
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST) // render through walls
            .withCull(false)
            .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.DEBUG_LINES) // debug lines = consistent lines always, no normals
            .buildSnippet();

    private static final RenderLayer ESP_LINES = RenderLayer.of(
            "lifetools:esp_lines",
            256,
            RenderPipeline.builder(RENDERTYPE_LINES_SNIPPET).withLocation(LINES_NO_DEPTH_ID).build(),
            RenderLayer.MultiPhaseParameters.builder()
                    .target(RenderPhase.Target.MAIN_TARGET)
                    .lineWidth(RenderPhase.LineWidth.FULL_LINE_WIDTH)
                    .layering(RenderPhase.Layering.NO_LAYERING)
                    .build(false)
    );



    @Override
    public void onInitializeClient() {
        registerCommands();
        WorldRenderEvents.BEFORE_ENTITIES.register(ESP::renderESPBoxes);
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
        if (matrices == null || camera == null) return;

        matrices.push();

        MinecraftClient client = MinecraftClient.getInstance();
        assert client.world != null;

        float tickDelta = client.getRenderTickCounter().getTickProgress(true);
        VertexConsumerProvider.Immediate vcp = client.getBufferBuilders().getEntityVertexConsumers();
        //RenderLayer layer = RenderLayer.getLines();

        client.world.getEntities().forEach(entity -> {
            if (entity == null) return;
            if (!(entity instanceof EnderDragonEntity) &&
                    !(entity instanceof WitherEntity) &&
                    !(entity instanceof PlayerEntity) &&
                    !(entity instanceof ElderGuardianEntity) &&
                    !(entity instanceof SlimeEntity) &&
                    !(entity instanceof BreezeEntity)) return;

            if (entity == client.player) return;

            // Interpolated position
            double x = entity.lastX + (entity.getX() - entity.lastX) * tickDelta;
            double y = entity.lastY + (entity.getY() - entity.lastY) * tickDelta;
            double z = entity.lastZ + (entity.getZ() - entity.lastZ) * tickDelta;

            matrices.push();
            matrices.translate(x - camera.getPos().x, y - camera.getPos().y, z - camera.getPos().z);

            Matrix4f matrix = matrices.peek().getPositionMatrix();
            VertexConsumer buffer = vcp.getBuffer(ESP_LINES);

            var box = entity.getBoundingBox();
            float hw = (float) (box.getLengthX() / 2.0);
            float hh = (float) box.getLengthY();
            float hd = (float) (box.getLengthZ() / 2.0);

            int r = 255, g = 255, b = 255;

            // Draw edges as lines (12 edges)
            // Front face

            buffer.vertex(matrix, -hw, 0, -hd).color(r, g, b, 255);
            buffer.vertex(matrix, hw, 0, -hd).color(r, g, b, 255);

            buffer.vertex(matrix, hw, 0, -hd).color(r, g, b, 255);
            buffer.vertex(matrix, hw, hh, -hd).color(r, g, b, 255);

            buffer.vertex(matrix, hw, hh, -hd).color(r, g, b, 255);
            buffer.vertex(matrix, -hw, hh, -hd).color(r, g, b, 255);

            buffer.vertex(matrix, -hw, hh, -hd).color(r, g, b, 255);
            buffer.vertex(matrix, -hw, 0, -hd).color(r, g, b, 255);

            // Back face
            buffer.vertex(matrix, -hw, 0, hd).color(r, g, b, 255);
            buffer.vertex(matrix, hw, 0, hd).color(r, g, b, 255);

            buffer.vertex(matrix, hw, 0, hd).color(r, g, b, 255);
            buffer.vertex(matrix, hw, hh, hd).color(r, g, b, 255);

            buffer.vertex(matrix, hw, hh, hd).color(r, g, b, 255);
            buffer.vertex(matrix, -hw, hh, hd).color(r, g, b, 255);

            buffer.vertex(matrix, -hw, hh, hd).color(r, g, b, 255);
            buffer.vertex(matrix, -hw, 0, hd).color(r, g, b, 255);

            // Vertical edges
            buffer.vertex(matrix, -hw, 0, -hd).color(r, g, b, 255);
            buffer.vertex(matrix, -hw, 0, hd).color(r, g, b, 255);

            buffer.vertex(matrix, hw, 0, -hd).color(r, g, b, 255);
            buffer.vertex(matrix, hw, 0, hd).color(r, g, b, 255);

            buffer.vertex(matrix, -hw, hh, -hd).color(r, g, b, 255);
            buffer.vertex(matrix, -hw, hh, hd).color(r, g, b, 255);

            buffer.vertex(matrix, hw, hh, -hd).color(r, g, b, 255);
            buffer.vertex(matrix, hw, hh, hd).color(r, g, b, 255);

            matrices.pop();
        });

        // Draw all wireframes at once
        vcp.draw(ESP_LINES);

        matrices.pop();
    }

}