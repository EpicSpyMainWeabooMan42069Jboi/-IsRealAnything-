package com.epicspymain.isrealanything.block;

import com.epicspymain.isrealanything.IsRealAnything;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

/**
 * Registers all custom blocks
 */
public class ModBlocks {

    /**
     * IMAGE_FRAME block - Displays screenshots from FrameFileManager
     * Used to render captured images in-world for horror effects
     */
    public static final Block IMAGE_FRAME = registerBlock("image_frame",
            new ImageFrameBlock(
                    AbstractBlock.Settings.create()
                            .mapColor(MapColor.BROWN)
                            .strength(2.0f)
                            .sounds(BlockSoundGroup.WOOD)
                            .nonOpaque()
            )
    );

    /**
     * Register a block and its corresponding item
     */
    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(IsRealAnything.MOD_ID, name), block);
    }

    /**
     * Register the block item (so block can be held in inventory)
     */
    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(
                Registries.ITEM,
                Identifier.of(IsRealAnything.MOD_ID, name),
                new BlockItem(block, new Item.Settings())
        );
    }

    /**
     * Initialize all blocks
     */
    public static void registerModBlocks() {
        IsRealAnything.LOGGER.info("Registering mod blocks for " + IsRealAnything.MOD_ID);
    }
}