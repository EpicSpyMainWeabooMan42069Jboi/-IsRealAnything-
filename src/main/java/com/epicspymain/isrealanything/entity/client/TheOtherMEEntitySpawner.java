package com.epicspymain.isrealanything.entity.client;

import com.epicspymain.isrealanything.IsRealAnything;
import com.epicspymain.isrealanything.entity.ModEntities;
import com.epicspymain.isrealanything.entity.custom.TheOtherMEEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.world.Heightmap;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Handles spawning logic for TheOtherME Entity.
 * Can spawn entities at specific locations or under specific conditions.
 */
public class TheOtherMEEntitySpawner {
    
    /**
     * Spawns TheOtherME entity at a specific position.
     * 
     * @param world The world to spawn in
     * @param pos Position to spawn at
     * @return The spawned entity, or null if failed
     */
    public static TheOtherMEEntity spawnAt(World world, BlockPos pos) {
        if (world.isClient) {
            return null;
        }
        
        try {
            TheOtherMEEntity entity = ModEntities.THEOTHERME_ENTITY.create(world, SpawnReason.COMMAND);
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
                
                IsRealAnything.LOGGER.info("TheOtherME entity spawned at {}", pos);
                return entity;
            }
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Failed to spawn TheOtherME entity", e);
        }
        
        return null;
    }
    

    public static TheOtherMEEntity spawnNearPlayer(World world, BlockPos playerPos, double distance) {
        if (world.isClient) {
            return null;
        }
        
        // Calculate random position around player
        double angle = world.random.nextDouble() * 2 * Math.PI;
        int x = (int) (playerPos.getX() + distance * Math.cos(angle));
        int z = (int) (playerPos.getZ() + distance * Math.sin(angle));
        int y = world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z);
        
        BlockPos spawnPos = new BlockPos(x, y, z);
        return spawnAt(world, spawnPos);
    }
    
    /**
     * Spawns TheOtherME entity in shadows/darkness.
     * 
     * @param world The world
     * @param playerPos Player position reference
     * @param searchRadius Radius to search for dark spots
     * @return The spawned entity, or null if no valid location found
     */
    public static TheOtherMEEntity spawnInDarkness(World world, BlockPos playerPos, int searchRadius) {
        if (world.isClient) {
            return null;
        }
        
        // Try multiple positions
        for (int attempt = 0; attempt < 20; attempt++) {
            int x = playerPos.getX() + world.random.nextInt(searchRadius * 2) - searchRadius;
            int z = playerPos.getZ() + world.random.nextInt(searchRadius * 2) - searchRadius;
            int y = world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z);
            
            BlockPos pos = new BlockPos(x, y, z);
            
            // Check if position is dark enough
            if (world.getLightLevel(pos) < 4 && canSpawnAt(world, pos)) {
                return spawnAt(world, pos);
            }
        }
        
        return null;
    }
    
    /**
     * Spawns TheOtherME entity directly behind player (jump scare).
     * 
     * @param world The world
     * @param playerPos Player position
     * @param playerYaw Player's yaw rotation
     * @param distance Distance behind player
     * @return The spawned entity, or null if failed
     */
    public static TheOtherMEEntity spawnBehindPlayer(World world, BlockPos playerPos, float playerYaw, double distance) {
        if (world.isClient) {
            return null;
        }
        
        // Calculate position behind player
        double radians = Math.toRadians(playerYaw);
        int x = (int) (playerPos.getX() - distance * Math.sin(radians));
        int z = (int) (playerPos.getZ() + distance * Math.cos(radians));
        int y = world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z);
        
        BlockPos spawnPos = new BlockPos(x, y, z);
        TheOtherMEEntity entity = spawnAt(world, spawnPos);
        
        if (entity != null) {
            // Make entity face player immediately
            entity.lookAt(null, playerPos.toCenterPos());
        }
        
        return entity;
    }
    
    /**
     * Checks if TheOtherME entity can spawn at location.
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
        
        // Check if there's enough space (entity is 2 blocks tall)
        BlockPos above = pos.up();
        if (!world.getBlockState(pos).isAir() || !world.getBlockState(above).isAir()) {
            return false;
        }
        
        // Check light level (prefer darkness)
        return world.getLightLevel(pos) < 8;
    }
    
    /**
     * Spawns TheOtherME entity only if conditions are met.
     * 
     * @param world The world
     * @param pos Position to spawn at
     * @return The spawned entity, or null if conditions not met
     */
    public static TheOtherMEEntity spawnIfValid(World world, BlockPos pos) {
        if (canSpawnAt(world, pos)) {
            return spawnAt(world, pos);
        }
        return null;
    }
    
    /**
     * Spawns a group of TheOtherME entities for intense horror sequences.
     * 
     * @param world The world
     * @param centerPos Center position
     * @param count Number of entities to spawn
     * @param radius Spread radius
     * @return Number of successfully spawned entities
     */
    public static int spawnGroup(World world, BlockPos centerPos, int count, int radius) {
        if (world.isClient) {
            return 0;
        }
        
        int spawned = 0;
        
        for (int i = 0; i < count; i++) {
            int x = centerPos.getX() + world.random.nextInt(radius * 2) - radius;
            int z = centerPos.getZ() + world.random.nextInt(radius * 2) - radius;
            int y = world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z);
            
            BlockPos pos = new BlockPos(x, y, z);
            
            if (spawnIfValid(world, pos) != null) {
                spawned++;
            }
        }
        
        IsRealAnything.LOGGER.info("Spawned {} TheOtherME entities in group", spawned);
        return spawned;
    }
}
