package com.epicspymain.isrealanything.block;

import com.epicspymain.isrealanything.block.entity.ImageFrameBlockEntity;
import com.epicspymain.isrealanything.file.FrameFileManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * ImageFrameBlock - Displays screenshots from FrameFileManager
 * Wall-mounted frame that shows captured images
 */
public class ImageFrameBlock extends BlockWithEntity {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    // Frame shapes for each direction (thin like a painting)
    private static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(1.0, 4.0, 14.0, 15.0, 12.0, 16.0);
    private static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(1.0, 4.0, 0.0, 15.0, 12.0, 2.0);
    private static final VoxelShape EAST_SHAPE = Block.createCuboidShape(0.0, 4.0, 1.0, 2.0, 12.0, 15.0);
    private static final VoxelShape WEST_SHAPE = Block.createCuboidShape(14.0, 4.0, 1.0, 16.0, 12.0, 15.0);

    public ImageFrameBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction side = ctx.getSide();

        // If placed on floor/ceiling, use player facing
        if (side == Direction.UP || side == Direction.DOWN) {
            return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
        }

        // Otherwise use the side it's placed on
        return getDefaultState().with(FACING, side);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getShapeForDirection(state.get(FACING));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getShapeForDirection(state.get(FACING));
    }

    private VoxelShape getShapeForDirection(Direction direction) {
        return switch (direction) {
            case NORTH -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case EAST -> EAST_SHAPE;
            case WEST -> WEST_SHAPE;
            default -> NORTH_SHAPE;
        };
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
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

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ImageFrameBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    /**
     * Get the current texture identifier for this frame
     * Used by renderer to display the image
     */
    public static String getCurrentTexture() {
        return FrameFileManager.CURRENT_FRAME_TEXTURE;
    }
}