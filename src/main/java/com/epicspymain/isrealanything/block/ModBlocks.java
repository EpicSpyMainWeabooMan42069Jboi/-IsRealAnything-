package com.epicspymain.isrealanything.block;

import com.epicspymain.isrealanything.IsRealAnything;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {

    public static final Block IMAGE_FRAME = registerBlock("image_frame",
            new ImageFrameBlock(
                    AbstractBlock.Settings.create()
                            .strength(2.0f)
                            .sounds(BlockSoundGroup.WOOD)
                            .nonOpaque()
            )
    );

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(IsRealAnything.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(
                Registries.ITEM,
                Identifier.of(IsRealAnything.MOD_ID, name),
                new BlockItem(block, new Item.Settings())
        );
    }

    public static void registerModBlocks() {
        IsRealAnything.LOGGER.info("Registering mod blocks for " + IsRealAnything.MOD_ID);
    }
}