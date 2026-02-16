package com.epicspymain.isrealanything.events.helpers;

import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

/**
 * Helper: ChunkDestroyer - Large-scale destruction
 * 69x69 block destruction pattern for final event
 */
public class ChunkDestroyer {
    
    /**
     * Execute 69x69 destruction centered on position
     */
    public static void execute(ServerWorld world, BlockPos center, int radius) {
        // Default 69x69 if no radius specified
        if (radius <= 0) {
            radius = 69 / 2;
        }
        
        int blocksDestroyed = 0;
        
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                // Destroy from surface down to bedrock level
                for (int y = world.getTopY(); y >= world.getBottomY() + 1; y--) {
                    BlockPos pos = center.add(x, y - center.getY(), z);
                    
                    // Don't destroy bedrock
                    if (world.getBlockState(pos).getBlock() != Blocks.BEDROCK &&
                        world.getBlockState(pos).getBlock() != Blocks.END_PORTAL &&
                        world.getBlockState(pos).getBlock() != Blocks.END_PORTAL_FRAME) {
                        
                        world.setBlockState(pos, Blocks.AIR.getDefaultState());
                        blocksDestroyed++;
                    }
                }
            }
        }
        
        return;
    }
    
    /**
     * Execute with custom depth
     */
    public static void executeWithDepth(ServerWorld world, BlockPos center, int radius, int depth) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = 0; y < depth; y++) {
                    BlockPos pos = center.add(x, -y, z);
                    
                    if (world.getBlockState(pos).getBlock() != Blocks.BEDROCK) {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState());
                    }
                }
            }
        }
    }
}
