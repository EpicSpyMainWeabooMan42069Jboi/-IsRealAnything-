package com.epicspymain.isrealanything.event.helpers;

import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

/**
 * Helper: ChunkDestroyer - Large-scale destruction
 * Removes 69x69 block areas for TheOverlook meltdown sequence
 */
public class ChunkDestroyer {

    /**
     * Called by TheOverlook - destroys chunks around center
     */
    public static void destroyChunks(ServerWorld world, BlockPos center, int radius, int depth) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = 0; y < depth; y++) {
                    BlockPos pos = center.add(x, -y, z);

                    if (!world.getBlockState(pos).isOf(Blocks.BEDROCK) &&
                            !world.getBlockState(pos).isOf(Blocks.END_PORTAL) &&
                            !world.getBlockState(pos).isOf(Blocks.END_PORTAL_FRAME)) {

                        world.setBlockState(pos, Blocks.AIR.getDefaultState());
                    }
                }
            }
        }
    }

    /**
     * Full depth destruction from surface to void
     */
    public static void destroyChunksFull(ServerWorld world, BlockPos center, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = world.getTopY(); y >= world.getBottomY() + 1; y--) {
                    BlockPos pos = center.add(x, y - center.getY(), z);

                    if (!world.getBlockState(pos).isOf(Blocks.BEDROCK)) {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState());
                    }
                }
            }
        }
    }
}