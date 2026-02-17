package com.epicspymain.isrealanything.event;

import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

/**
 * EVENT 29: BurningMoments - Trap spawning
 * Spawns obvious or hidden traps
 * Player will fall for them eventually
 * Pressure plates, lava pits, TNT, etc.
 */
public class BurningMomentsEvent {
    
    private static final int TRAP_COUNT = 5; // 3-5 traps
    
    /**
     * Trigger trap spawning
     */
    public static void trigger(ServerWorld world, ServerPlayerEntity player) {
        int traps = TRAP_COUNT + world.random.nextInt(3);
        
        for (int i = 0; i < traps; i++) {
            float trapType = world.random.nextFloat();
            
            if (trapType < 0.3f) {
                spawnLavaPit(world, player);
            } else if (trapType < 0.6f) {
                spawnTNTTrap(world, player);
            } else {
                spawnPressurePlateTrap(world, player);
            }
        }
        
        // Subtle warning
        player.sendMessage(
            Text.literal("Something feels... dangerous.")
                .formatted(Formatting.DARK_RED, Formatting.ITALIC),
            true
        );
    }
    
    /**
     * Spawn hidden lava pit
     */
    private static void spawnLavaPit(ServerWorld world, ServerPlayerEntity player) {
        BlockPos trapPos = findTrapLocation(world, player, 15, 30);
        
        if (trapPos == null) return;
        
        // Dig 3x3 pit, 3 blocks deep
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                for (int y = 0; y < 3; y++) {
                    BlockPos pos = trapPos.add(x, -y, z);
                    world.setBlockState(pos, Blocks.AIR.getDefaultState());
                }
            }
        }
        
        // Fill bottom with lava
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockPos pos = trapPos.add(x, -3, z);
                world.setBlockState(pos, Blocks.LAVA.getDefaultState());
            }
        }
        
        // Cover with carpet (hidden trap)
        if (world.random.nextBoolean()) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos pos = trapPos.add(x, 0, z);
                    world.setBlockState(pos, Blocks.RED_CARPET.getDefaultState());
                }
            }
        }
    }
    
    /**
     * Spawn TNT trap
     */
    private static void spawnTNTTrap(ServerWorld world, ServerPlayerEntity player) {
        BlockPos trapPos = findTrapLocation(world, player, 10, 25);
        
        if (trapPos == null) return;
        
        // Place TNT underground
        world.setBlockState(trapPos.down(), Blocks.TNT.getDefaultState());
        
        // Place pressure plate on top
        world.setBlockState(trapPos, Blocks.STONE_PRESSURE_PLATE.getDefaultState());
    }
    
    /**
     * Spawn obvious pressure plate trap (player knows but might forget)
     */
    private static void spawnPressurePlateTrap(ServerWorld world, ServerPlayerEntity player) {
        BlockPos trapPos = findTrapLocation(world, player, 8, 20);
        
        if (trapPos == null) return;
        
        // Place dispenser with arrow
        world.setBlockState(trapPos, Blocks.DISPENSER.getDefaultState());
        
        // Place pressure plate in front
        BlockPos platePos = trapPos.offset(world.random.nextHorizontal());
        world.setBlockState(platePos, Blocks.STONE_PRESSURE_PLATE.getDefaultState());
        
        // Add sign warning (making it "obvious")
        BlockPos signPos = platePos.up();
        world.setBlockState(signPos, Blocks.OAK_SIGN.getDefaultState());
    }
    
    /**
     * Find suitable trap location
     */
    private static BlockPos findTrapLocation(ServerWorld world, ServerPlayerEntity player, int minDist, int maxDist) {
        BlockPos playerPos = player.getBlockPos();
        
        for (int attempt = 0; attempt < 20; attempt++) {
            double angle = world.random.nextDouble() * Math.PI * 2;
            int distance = minDist + world.random.nextInt(maxDist - minDist);
            
            int x = playerPos.getX() + (int)(Math.cos(angle) * distance);
            int z = playerPos.getZ() + (int)(Math.sin(angle) * distance);
            
            BlockPos testPos = new BlockPos(x, playerPos.getY(), z);
            testPos = world.getTopPosition(net.minecraft.world.Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, testPos);
            
            // Check if suitable
            if (world.getBlockState(testPos).isAir() &&
                world.getBlockState(testPos.down()).isSolidBlock(world, testPos.down())) {
                return testPos;
            }
        }
        
        return null;
    }
}
