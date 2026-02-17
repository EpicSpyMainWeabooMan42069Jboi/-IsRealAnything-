package com.epicspymain.isrealanything.entity.client;

import com.epicspymain.isrealanything.IsRealAnything;
import com.epicspymain.isrealanything.entity.ModEntities;
import com.epicspymain.isrealanything.entity.custom.TheMEEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Handles spawning logic for TheME Entity.
 * Can spawn entities at specific locations or under specific conditions.
 */
public class TheMEEntitySpawner {
    

    public static TheMEEntity spawnAt(World world, BlockPos pos) {
        if (world.isClient) {
            return null;
        }
        
        try {
            TheMEEntity entity = ModEntities.THE_ME.create(world);
            if (entity != null) {
                entity.refreshPositionAndAngles(
                    pos.getX() + 0.5,
                    pos.getY(),
                    pos.getZ() + 0.5,
                    0.0f,
                    0.0f
                );
                entity.initialize((ServerWorld) world, world.getLocalDifficulty(pos), 
                    SpawnReason.COMMAND, null);
                world.spawnEntity(entity);
                
                IsRealAnything.LOGGER.info("TheME entity spawned at {}", pos);
                return entity;
            }
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Failed to spawn TheME entity", e);
        }
        
        return null;
    }
    
    /**
     * Spawns TheME entity near a player.
     * 
     * @param world The world
     * @param playerPos Player position
     * @param distance Distance from player (blocks)
     * @return The spawned entity, or null if failed
     */
    public static TheMEEntity spawnNearPlayer(World world, BlockPos playerPos, double distance) {
        if (world.isClient) {
            return null;
        }
        
        // Calculate random position around player
        double angle = world.random.nextDouble() * 2 * Math.PI;
        int x = (int) (playerPos.getX() + distance * Math.cos(angle));
        int z = (int) (playerPos.getZ() + distance * Math.sin(angle));
        int y = world.getTopY(x, z);
        
        BlockPos spawnPos = new BlockPos(x, y, z);
        return spawnAt(world, spawnPos);
    }
    
    /**
     * Spawns TheME entity behind a player (horror effect).
     * 
     * @param world The world
     * @param playerPos Player position
     * @param playerYaw Player's yaw rotation
     * @param distance Distance behind player
     * @return The spawned entity, or null if failed
     */
    public static TheMEEntity spawnBehindPlayer(World world, BlockPos playerPos, float playerYaw, double distance) {
        if (world.isClient) {
            return null;
        }
        
        // Calculate position behind player
        double radians = Math.toRadians(playerYaw);
        int x = (int) (playerPos.getX() - distance * Math.sin(radians));
        int z = (int) (playerPos.getZ() + distance * Math.cos(radians));
        int y = world.getTopY(x, z);
        
        BlockPos spawnPos = new BlockPos(x, y, z);
        TheMEEntity entity = spawnAt(world, spawnPos);
        
        if (entity != null) {
            // Make entity face player
            entity.lookAt(null, playerPos.toCenterPos());
        }
        
        return entity;
    }
    
    /**
     * Checks if TheME entity can spawn at location.
     * 
     * @param world The world
     * @param pos Position to check
     * @return true if spawn is valid
     */
    public static boolean canSpawnAt(World world, BlockPos pos) {
        // Check if position is valid
        if (!world.isInBuildLimit(pos)) {
            return false;
        }
        
        // Check if there's enough space
        BlockPos above = pos.up();
        if (!world.getBlockState(pos).isAir() || !world.getBlockState(above).isAir()) {
            return false;
        }
        
        // Check light level (spawn in darkness for horror effect)
        return world.getLightLevel(pos) < 7;
    }
    
    /**
     * Spawns TheME entity only if conditions are met (dark area, etc.).
     * 
     * @param world The world
     * @param pos Position to spawn at
     * @return The spawned entity, or null if conditions not met
     */
    public static TheMEEntity spawnIfValid(World world, BlockPos pos) {
        if (canSpawnAt(world, pos)) {
            return spawnAt(world, pos);
        }
        return null;
    }
}
