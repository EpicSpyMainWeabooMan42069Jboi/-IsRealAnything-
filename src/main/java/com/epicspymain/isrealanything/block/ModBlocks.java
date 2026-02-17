package com.epicspymain.isrealanything.block;

import com.epicspymain.isrealanything.IsRealAnything;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

/**
 * ModBlocks - Registers all custom blocks
 */
public class ModBlocks {
    

    public static final Block IMAGE_FRAME = registerBlock("image_frame",
        new ImageFrameBlock(FabricBlockSettings.of(Material.WOOD)
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
        return Registry.register(Registries.BLOCK, new Identifier(IsRealAnything.MOD_ID, name), block);
    }
    
    /**
     * Register the block item (so block can be held in inventory)
     */
    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, new Identifier(IsRealAnything.MOD_ID, name),
            new BlockItem(block, new FabricItemSettings()));
    }
    
    /**
     * Initialize all blocks
     */
    public static void registerModBlocks() {
        IsRealAnything.LOGGER.info("Registering mod blocks for " + IsRealAnything.MOD_ID);
    }
}
