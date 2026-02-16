package com.epicspymain.isrealanything.events;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EVENT 2: RandomBlockReplace - Subtle block swapping
 * Swaps blocks in/near base (stone↔dirt, torch↔redstone torch)
 * If no base exists, affects ground player is standing on
 */
public class RandomBlockReplaceEvent {
    
    // Block swap pairs
    private static final Map<Block, Block> BLOCK_SWAPS = new HashMap<>();
    
    static {
        // Bidirectional swaps
        BLOCK_SWAPS.put(Blocks.STONE, Blocks.DIRT);
        BLOCK_SWAPS.put(Blocks.DIRT, Blocks.STONE);
        BLOCK_SWAPS.put(Blocks.TORCH, Blocks.REDSTONE_TORCH);
        BLOCK_SWAPS.put(Blocks.REDSTONE_TORCH, Blocks.TORCH);
        BLOCK_SWAPS.put(Blocks.WALL_TORCH, Blocks.REDSTONE_WALL_TORCH);
        BLOCK_SWAPS.put(Blocks.REDSTONE_WALL_TORCH, Blocks.WALL_TORCH);
        BLOCK_SWAPS.put(Blocks.COBBLESTONE, Blocks.GRAVEL);
        BLOCK_SWAPS.put(Blocks.GRAVEL, Blocks.COBBLESTONE);
        BLOCK_SWAPS.put(Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS);
        BLOCK_SWAPS.put(Blocks.SPRUCE_PLANKS, Blocks.OAK_PLANKS);
    }
    
    /**
     * Trigger block swap event near player
     */
    public static void trigger(ServerWorld world, ServerPlayerEntity player) {
        BlockPos playerPos = player.getBlockPos();
        
        // Detect if player has a "base" (player-placed blocks nearby)
        boolean hasBase = detectBase(world, playerPos);
        
        int radius = hasBase ? 20 : 8; // Larger radius if base detected
        int swapCount = hasBase ? 5 : 3; // More swaps if base detected
        
        // Find swappable blocks
        List<BlockPos> swappablePositions = findSwappableBlocks(world, playerPos, radius);
        
        if (swappablePositions.isEmpty()) {
            return; // No valid blocks to swap
        }
        
        // Perform swaps
        for (int i = 0; i < Math.min(swapCount, swappablePositions.size()); i++) {
            BlockPos pos = swappablePositions.get(world.random.nextInt(swappablePositions.size()));
            swapBlock(world, pos);
            swappablePositions.remove(pos); // Don't swap same block twice
        }
    }
    
    /**
     * Detect if player has a base nearby (many player-placed blocks)
     */
    private static boolean detectBase(ServerWorld world, BlockPos center) {
        int playerBlockCount = 0;
        
        for (int x = -15; x <= 15; x++) {
            for (int y = -5; y <= 5; y++) {
                for (int z = -15; z <= 15; z++) {
                    BlockPos pos = center.add(x, y, z);
                    Block block = world.getBlockState(pos).getBlock();
                    
                    // Check for common building blocks
                    if (block == Blocks.OAK_PLANKS || 
                        block == Blocks.SPRUCE_PLANKS ||
                        block == Blocks.COBBLESTONE ||
                        block == Blocks.STONE_BRICKS ||
                        block == Blocks.TORCH ||
                        block == Blocks.CRAFTING_TABLE ||
                        block == Blocks.FURNACE ||
                        block == Blocks.CHEST) {
                        playerBlockCount++;
                    }
                }
            }
        }
        
        return playerBlockCount > 30; // Base detected if 30+ building blocks
    }
    
    /**
     * Find all swappable blocks in radius
     */
    private static List<BlockPos> findSwappableBlocks(ServerWorld world, BlockPos center, int radius) {
        List<BlockPos> swappable = new ArrayList<>();
        
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = center.add(x, y, z);
                    Block block = world.getBlockState(pos).getBlock();
                    
                    // Check if block can be swapped
                    if (BLOCK_SWAPS.containsKey(block)) {
                        swappable.add(pos);
                    }
                }
            }
        }
        
        return swappable;
    }
    
    /**
     * Swap a block with its pair
     */
    private static void swapBlock(ServerWorld world, BlockPos pos) {
        Block currentBlock = world.getBlockState(pos).getBlock();
        Block newBlock = BLOCK_SWAPS.get(currentBlock);
        
        if (newBlock != null) {
            world.setBlockState(pos, newBlock.getDefaultState(), Block.NOTIFY_ALL);
        }
    }
}
