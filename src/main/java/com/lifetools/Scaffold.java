package com.lifetools;

import com.lifetools.annotations.Feature;
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
    public static boolean scaffoldEnabled = false;

    @Override
    public void onInitializeClient() {
        // Keybind implementation
        toggleScaffoldKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Toggle Scaffold",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_U,
                "Scaffold"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            checkToggleKey(); // No parameters needed
            if (scaffoldEnabled) {
                ScaffoldLogic(client);
            }
        });
    }



    public void checkToggleKey() {
        if (toggleScaffoldKey.wasPressed()) {
            toggleScaffold(); // Call the new method to toggle the scaffold state
        }
    }



    @Feature(methodName = "toggleScaffold", actionType = "toggle", featureName = "Scaffold", booleanField = "scaffoldEnabled")
    public void toggleScaffold() {
        scaffoldEnabled = !scaffoldEnabled;
        ClientPlayerEntity player = MinecraftClient.getInstance().player; // Get the player instance
        assert player != null; // Ensure player instance is not null
        player.sendMessage(
                Text.of(INFO_PREFIX + "Scaffold has been " + (scaffoldEnabled ? "§aenabled" : "§cdisabled")), false);
    }

    private void ScaffoldLogic(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player != null) {
            BlockPos belowPos = player.getBlockPos().down();
            ItemStack itemStack = player.getMainHandStack();
            Block block = Block.getBlockFromItem(itemStack.getItem());

            // Check if the block is not air and is a BlockItem, and if the space below is air
            if (block != Blocks.AIR && itemStack.getItem() instanceof BlockItem && client.world != null && client.world.getBlockState(belowPos).isAir()) {
                assert client.interactionManager != null;
                client.interactionManager.interactBlock(player, Hand.MAIN_HAND,
                        new BlockHitResult(Vec3d.ofCenter(belowPos), Direction.DOWN, belowPos, false));
            }
        }
    }
}
