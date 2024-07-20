package com.lifetools;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import static com.lifetools.LifeTools.INFO_PREFIX;

public class Scaffold implements ClientModInitializer {
    private static KeyBinding toggleScaffoldKey;
    private boolean scaffoldEnabled = false;
    @Override
    public void onInitializeClient() {
        //keybind implementation
        toggleScaffoldKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Toggle Scaffold",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_U,
                "Scaffold"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            if (toggleScaffoldKey.wasPressed()) {
                scaffoldEnabled = !scaffoldEnabled;
                client.player.sendMessage(Text.of(INFO_PREFIX + "Scaffold has been " + (scaffoldEnabled ? "§aenabled" : "§cdisabled")), false);
            }

            if (scaffoldEnabled) {
                ScaffoldLogic(client);
            }
        });
    }

    private void ScaffoldLogic(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player != null) {
            BlockPos belowPos = player.getBlockPos().down();
            ItemStack itemStack = player.getMainHandStack();
            Block block = Block.getBlockFromItem(itemStack.getItem());

            if (block != Blocks.AIR && itemStack.getItem() instanceof BlockItem && client.world != null && client.world.getBlockState(belowPos).isAir()) {
                assert client.interactionManager != null;
                client.interactionManager.interactBlock(player, Hand.MAIN_HAND,
                        new BlockHitResult(Vec3d.ofCenter(belowPos), Direction.DOWN, belowPos, false));
            }
        }
    }
}
