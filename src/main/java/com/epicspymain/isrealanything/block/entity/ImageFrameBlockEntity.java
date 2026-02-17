package com.epicspymain.isrealanything.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

/**
 * BlockEntity for ImageFrameBlock
 * Stores frame data and handles rendering
 */
public class ImageFrameBlockEntity extends BlockEntity {

    private String currentImagePath = null;

    public ImageFrameBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.IMAGE_FRAME_BLOCK_ENTITY, pos, state);
    }

    /**
     * Set the image to display in this frame
     */
    public void setImagePath(String path) {
        this.currentImagePath = path;
        markDirty();
    }

    /**
     * Get the current image path
     */
    public String getImagePath() {
        return currentImagePath;
    }
}