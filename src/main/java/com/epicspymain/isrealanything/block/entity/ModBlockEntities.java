package com.epicspymain.isrealanything.block.entity;

import com.epicspymain.isrealanything.IsRealAnything;
import com.epicspymain.isrealanything.block.ModBlocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * Registers all custom block entities
 */
public class ModBlockEntities {

    public static final BlockEntityType<ImageFrameBlockEntity> IMAGE_FRAME_BLOCK_ENTITY =
            Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(IsRealAnything.MOD_ID, "image_frame"),
                    BlockEntityType.Builder.create(
                            ImageFrameBlockEntity::new,
                            ModBlocks.IMAGE_FRAME
                    ).build()
            );

    /**
     * Initialize block entities
     */
    public static void registerBlockEntities() {
        IsRealAnything.LOGGER.info("Registering block entities for " + IsRealAnything.MOD_ID);
    }
}