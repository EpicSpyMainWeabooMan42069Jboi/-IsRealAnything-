package com.epicspymain.isrealanything.event;

import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

/**
 * EVENT 32: AreYouLooking - Home destruction
 * Holes appear in player's home (missing blocks don't return)
 * Ground in 15x15 radius replaced with Soul Sand
 * Creates permanent damage and slowing effect
 */
public class AreYouLookingEvent {
    
    private static final int SOUL_SAND_RADIUS = 15;
    private static final int HOLE_COUNT = 20; // 15-20 holes
    
    /**
     * Trigger home destruction event
     */
    public static void trigger(ServerWorld world, ServerPlayerEntity player) {
        BlockPos playerPos = player.getBlockPos();

        // Send warning
        player.sendMessage(
                Text.literal("Are you looking?")
                        .formatted(Formatting.DARK_RED, Formatting.BOLD),
                false
        );

        // Create holes in nearby structures
        int holes = HOLE_COUNT + world.random.nextInt(6);
        for (int i = 0; i < holes; i++) {
            createHole(world, playerPos);
        }

        // Replace ground with soul sand
        replaceSoulSand(world, playerPos);

        new Thread(() -> {
            try {
                Thread.sleep(2000);
                world.getServer().execute(() ->
                        player.sendMessage(
                                Text.literal("Б")
                                        .formatted(Formatting.DARK_GRAY, Formatting.ITALIC),
                                false
                        )
                );
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();


    } private static void createHole(ServerWorld world, BlockPos center) {
        // Random position within 30 blocks
        int x = center.getX() + world.random.nextInt(60) - 30;
        int y = center.getY() + world.random.nextInt(20) - 10;
        int z = center.getZ() + world.random.nextInt(60) - 30;
        
        BlockPos holeCenter = new BlockPos(x, y, z);
        
        // Create 3x3 hole
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos pos = holeCenter.add(dx, dy, dz);
                    
                    // Don't remove bedrock or important blocks
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
     * Replace ground with soul sand
     */
    private static void replaceSoulSand(ServerWorld world, BlockPos center) {
        for (int x = -SOUL_SAND_RADIUS; x <= SOUL_SAND_RADIUS; x++) {
            for (int z = -SOUL_SAND_RADIUS; z <= SOUL_SAND_RADIUS; z++) {
                // Only replace blocks at ground level
                BlockPos groundPos = world.getTopPosition(
                    net.minecraft.world.Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                    center.add(x, 0, z)
                );
                
                // Replace top block with soul sand
                if (world.getBlockState(groundPos.down()).isSolidBlock(world, groundPos.down())) {
                    world.setBlockState(groundPos.down(), Blocks.SOUL_SAND.getDefaultState());
                }
            }
        }
    }
}
