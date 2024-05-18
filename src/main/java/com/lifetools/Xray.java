package com.lifetools;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Set;

public class Xray implements ClientModInitializer {
    public static final Set<Block> visibleBlocks = new HashSet<>();
    private static boolean xrayEnabled = false;
    private static KeyBinding keyBinding;

    @Override
    public void onInitializeClient() {
        visibleBlocks.add(Blocks.DIAMOND_ORE);
        visibleBlocks.add(Blocks.GOLD_ORE);
        visibleBlocks.add(Blocks.IRON_ORE);
        visibleBlocks.add(Blocks.COAL_ORE);
        visibleBlocks.add(Blocks.EMERALD_ORE);

        // Register key binding
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Toggle Xray",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_X,
                "Xray"
        ));

        // Register the client tick event to check for key press
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (getKeyBinding().wasPressed()) {
                toggleXray();
            }
        });
    }

    public static void toggleXray() {
        xrayEnabled = !xrayEnabled;
        MinecraftClient client = MinecraftClient.getInstance();
        assert client.player != null;
        if (xrayEnabled) {
            client.player.sendMessage(Text.of("§8[§2LifeTools§8] §7Xray has been §aenabled"), false);
        } else {
            client.player.sendMessage(Text.of("§8[§2LifeTools§8] §7Xray has been §cdisabled"), false);
        }
        client.worldRenderer.reload();
    }

    public static boolean isXrayEnabled() {
        return xrayEnabled;
    }

    public static KeyBinding getKeyBinding() {
        return keyBinding;
    }

    public static void shouldSideBeRendered(BlockState adjacentState, BlockView blockView, BlockPos blockPos,
                                            Direction direction, CallbackInfoReturnable<Boolean> ci) {
        if (xrayEnabled && visibleBlocks.contains(adjacentState.getBlock())) {
            ci.setReturnValue(true); // Ensure the side is rendered
        } else if (xrayEnabled) {
            ci.setReturnValue(false); // Ensure the side is not rendered
        } else {
            ci.setReturnValue(true); // Normal rendering when Xray is disabled
        }
    }
}
