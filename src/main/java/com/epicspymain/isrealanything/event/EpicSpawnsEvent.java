package com.epicspymain.isrealanything.event;

import com.epicspymain.isrealanything.entity.ModEntities;
import com.epicspymain.isrealanything.entity.custom.TheOtherMEEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.*;

/**
 * EVENT 9: EpicSpawns - Spy entity behavior
 * Entity stands still behind objects, crouching or floating
 * Spies on player
 * Vanishes when player approaches (within 8 blocks)
 */
public class EpicSpawnsEvent {
    
    private static final int SPAWN_DISTANCE_MIN = 15;
    private static final int SPAWN_DISTANCE_MAX = 35;
    private static final int VANISH_DISTANCE = 8;
    private static final int SPY_DURATION = 200; // 10 seconds max
    
    // Track active spy entities
    private static final Map<UUID, SpyEntityData> spyEntities = new HashMap<>();
    
    /**
     * Trigger spy entity spawn
     */
    public static void trigger(ServerWorld world, ServerPlayerEntity player) {
        // Find location behind object
        BlockPos spawnPos = findSpyLocation(world, player);
        
        if (spawnPos == null) {
            return;
        }
        
        // Spawn TheOtherME (more sinister variant)
        TheOtherMEEntity entity = ModEntities.THE_OTHER_ME.create(world);
        if (entity == null) {
            return;
        }
        
        // Random behavior: crouching or floating
        boolean shouldFloat = world.random.nextBoolean();
        
        double spawnY = spawnPos.getY();
        if (shouldFloat) {
            spawnY += 1.5; // Float 1.5 blocks up
        }
        
        entity.refreshPositionAndAngles(
            spawnPos.getX() + 0.5,
            spawnY,
            spawnPos.getZ() + 0.5,
            0, 0
        );
        
        // Make entity look at player
        Vec3d playerPos = player.getPos();
        entity.lookAt(net.minecraft.entity.EntityAnchorArgumentType.EntityAnchor.EYES, playerPos);
        
        // Crouch if not floating
        if (!shouldFloat) {
            entity.setSneaking(true);
        }
        
        // Disable AI
        entity.setNoAi(true);
        
        world.spawnEntity(entity);
        
        // Track spy entity
        spyEntities.put(entity.getUuid(), new SpyEntityData(
            player.getUuid(),
            SPY_DURATION,
            shouldFloat
        ));
    }
    
    /**
     * Tick all spy entities
     */
    public static void tick(ServerWorld world) {
        Iterator<Map.Entry<UUID, SpyEntityData>> iterator = spyEntities.entrySet().iterator();
        
        while (iterator.hasNext()) {
            Map.Entry<UUID, SpyEntityData> entry = iterator.next();
            UUID entityUuid = entry.getKey();
            SpyEntityData data = entry.getValue();
            
            TheOtherMEEntity entity = (TheOtherMEEntity) world.getEntity(entityUuid);
            ServerPlayerEntity player = world.getServer().getPlayerManager().getPlayer(data.targetPlayerUuid);
            
            if (entity == null || player == null || !entity.isAlive()) {
                iterator.remove();
                continue;
            }
            
            // Check if player is too close
            double distance = entity.distanceTo(player);
            
            if (distance < VANISH_DISTANCE || data.ticksRemaining <= 0) {
                // Vanish (no effects)
                entity.discard();
                iterator.remove();
            } else {
                // Keep looking at player
                Vec3d playerPos = player.getPos();
                entity.lookAt(net.minecraft.entity.EntityAnchorArgumentType.EntityAnchor.EYES, playerPos);
                
                data.ticksRemaining--;
            }
        }
    }
    
    /**
     * Find suitable spy location (behind objects)
     */
    private static BlockPos findSpyLocation(ServerWorld world, ServerPlayerEntity player) {
        BlockPos playerPos = player.getBlockPos();
        
        for (int attempt = 0; attempt < 40; attempt++) {
            // Random angle and distance
            double angle = world.random.nextDouble() * Math.PI * 2;
            int distance = SPAWN_DISTANCE_MIN + world.random.nextInt(SPAWN_DISTANCE_MAX - SPAWN_DISTANCE_MIN);
            
            int x = playerPos.getX() + (int)(Math.cos(angle) * distance);
            int z = playerPos.getZ() + (int)(Math.sin(angle) * distance);
            
            // Find ground level
            BlockPos testPos = new BlockPos(x, playerPos.getY(), z);
            testPos = world.getTopPosition(net.minecraft.world.Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, testPos);
            
            // Check if location has cover nearby
            if (hasNearbyObstacles(world, testPos)) {
                return testPos;
            }
        }
        
        return null;
    }
    
    /**
     * Check if location has obstacles for hiding
     */
    private static boolean hasNearbyObstacles(ServerWorld world, BlockPos pos) {
        // Check for blocks/trees nearby that entity can hide behind
        int obstacleCount = 0;
        
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                if (x == 0 && z == 0) continue; // Skip center
                
                BlockPos checkPos = pos.add(x, 0, z);
                if (!world.getBlockState(checkPos).isAir()) {
                    obstacleCount++;
                }
                
                // Check for trees (logs above)
                BlockPos abovePos = checkPos.up();
                if (world.getBlockState(abovePos).getBlock().toString().contains("log")) {
                    obstacleCount += 2; // Trees are good cover
                }
            }
        }
        
        // Should have some obstacles but not be completely blocked
        return obstacleCount > 5 && obstacleCount < 30;
    }
    
    /**
     * Data class for spy entity tracking
     */
    private static class SpyEntityData {
        final UUID targetPlayerUuid;
        int ticksRemaining;
        final boolean isFloating;
        
        SpyEntityData(UUID targetPlayerUuid, int ticksRemaining, boolean isFloating) {
            this.targetPlayerUuid = targetPlayerUuid;
            this.ticksRemaining = ticksRemaining;
            this.isFloating = isFloating;
        }
    }
}
