package com.epicspymain.isrealanything.block;

import com.epicspymain.isrealanything.file.FrameFileManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/**
 * ImageFrameBlock - Custom block that displays screenshots
 * Integrates with FrameFileManager to show CURRENT_FRAME_TEXTURE
 */
public class ImageFrameBlock extends Block {
    
    // Frame is thin like a painting
    private static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 16, 1);
    
    public ImageFrameBlock(Settings settings) {
        super(settings);
    }
    
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }
    
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            // Show info about current frame
            String framePath = FrameFileManager.getCurrentFramePath();
            if (framePath != null) {
                player.sendMessage(
                    Text.literal("Frame displaying: " + framePath),
                    false
                );
            } else {
                player.sendMessage(
                    Text.literal("No frame loaded"),
                    false
                );
            }
        }
        return ActionResult.SUCCESS;
    }
    
    /**
     * Get the current texture identifier for this frame
     * Used by renderer to display the image
     */
    public static String getCurrentTexture() {
        return FrameFileManager.CURRENT_FRAME_TEXTURE;
    }
}
