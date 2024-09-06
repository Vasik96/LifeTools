package com.lifetools;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

import java.util.HashSet;
import java.util.Set;

public class XrayList {

    private static final Set<Block> XRAY_BLOCKS = new HashSet<>();

    static {
        XRAY_BLOCKS.add(Blocks.DIAMOND_ORE);
        XRAY_BLOCKS.add(Blocks.GOLD_ORE);
        XRAY_BLOCKS.add(Blocks.IRON_ORE);
        XRAY_BLOCKS.add(Blocks.COAL_ORE);
        XRAY_BLOCKS.add(Blocks.COPPER_ORE);
        XRAY_BLOCKS.add(Blocks.REDSTONE_ORE);
        XRAY_BLOCKS.add(Blocks.LAPIS_ORE);
        XRAY_BLOCKS.add(Blocks.EMERALD_ORE);
        XRAY_BLOCKS.add(Blocks.DEEPSLATE_DIAMOND_ORE);
        XRAY_BLOCKS.add(Blocks.DEEPSLATE_GOLD_ORE);
        XRAY_BLOCKS.add(Blocks.DEEPSLATE_IRON_ORE);
        XRAY_BLOCKS.add(Blocks.DEEPSLATE_COAL_ORE);
        XRAY_BLOCKS.add(Blocks.DEEPSLATE_COPPER_ORE);
        XRAY_BLOCKS.add(Blocks.DEEPSLATE_REDSTONE_ORE);
        XRAY_BLOCKS.add(Blocks.DEEPSLATE_LAPIS_ORE);
        XRAY_BLOCKS.add(Blocks.DEEPSLATE_EMERALD_ORE);
        XRAY_BLOCKS.add(Blocks.LAVA);
        XRAY_BLOCKS.add(Blocks.SPAWNER);
        XRAY_BLOCKS.add(Blocks.TRIAL_SPAWNER);
        XRAY_BLOCKS.add(Blocks.END_PORTAL_FRAME);
        XRAY_BLOCKS.add(Blocks.END_PORTAL);
        XRAY_BLOCKS.add(Blocks.END_GATEWAY);
        XRAY_BLOCKS.add(Blocks.NETHER_PORTAL);
        XRAY_BLOCKS.add(Blocks.BEDROCK);
        XRAY_BLOCKS.add(Blocks.BARREL);
        XRAY_BLOCKS.add(Blocks.CHEST);
        XRAY_BLOCKS.add(Blocks.TRAPPED_CHEST);
        XRAY_BLOCKS.add(Blocks.ENDER_CHEST);
        XRAY_BLOCKS.add(Blocks.NETHER_QUARTZ_ORE);
        XRAY_BLOCKS.add(Blocks.NETHER_GOLD_ORE);
        XRAY_BLOCKS.add(Blocks.GILDED_BLACKSTONE);
        XRAY_BLOCKS.add(Blocks.WHITE_SHULKER_BOX);
        XRAY_BLOCKS.add(Blocks.ORANGE_SHULKER_BOX);
        XRAY_BLOCKS.add(Blocks.MAGENTA_SHULKER_BOX);
        XRAY_BLOCKS.add(Blocks.LIGHT_BLUE_SHULKER_BOX);
        XRAY_BLOCKS.add(Blocks.YELLOW_SHULKER_BOX);
        XRAY_BLOCKS.add(Blocks.LIME_SHULKER_BOX);
        XRAY_BLOCKS.add(Blocks.PINK_SHULKER_BOX);
        XRAY_BLOCKS.add(Blocks.GRAY_SHULKER_BOX);
        XRAY_BLOCKS.add(Blocks.LIGHT_GRAY_SHULKER_BOX);
        XRAY_BLOCKS.add(Blocks.CYAN_SHULKER_BOX);
        XRAY_BLOCKS.add(Blocks.PURPLE_SHULKER_BOX);
        XRAY_BLOCKS.add(Blocks.BLUE_SHULKER_BOX);
        XRAY_BLOCKS.add(Blocks.BROWN_SHULKER_BOX);
        XRAY_BLOCKS.add(Blocks.GREEN_SHULKER_BOX);
        XRAY_BLOCKS.add(Blocks.RED_SHULKER_BOX);
        XRAY_BLOCKS.add(Blocks.BLACK_SHULKER_BOX);
        XRAY_BLOCKS.add(Blocks.SHULKER_BOX);
        XRAY_BLOCKS.add(Blocks.ANCIENT_DEBRIS);


        XRAY_BLOCKS.add(Blocks.COAL_BLOCK);
        XRAY_BLOCKS.add(Blocks.COPPER_BLOCK);
        XRAY_BLOCKS.add(Blocks.IRON_BLOCK);
        XRAY_BLOCKS.add(Blocks.GOLD_BLOCK);
        XRAY_BLOCKS.add(Blocks.DIAMOND_BLOCK);
        XRAY_BLOCKS.add(Blocks.EMERALD_BLOCK);
        XRAY_BLOCKS.add(Blocks.NETHERITE_BLOCK);
        XRAY_BLOCKS.add(Blocks.LAPIS_BLOCK);
        XRAY_BLOCKS.add(Blocks.REDSTONE_BLOCK);

        XRAY_BLOCKS.add(Blocks.RAW_COPPER_BLOCK);
        XRAY_BLOCKS.add(Blocks.RAW_IRON_BLOCK);
        XRAY_BLOCKS.add(Blocks.RAW_GOLD_BLOCK);

    }

    public static boolean isXrayBlock(Block block) {
        return XRAY_BLOCKS.contains(block);
    }
}
