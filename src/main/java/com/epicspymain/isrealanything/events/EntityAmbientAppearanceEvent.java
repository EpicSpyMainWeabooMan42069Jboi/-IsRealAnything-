package com.epicspymain.isrealanything.events;

import com.epicspymain.isrealanything.entity.ModEntities;
import com.epicspymain.isrealanything.entity.custom.TheMEEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.*;

/**
 * EVENT 8: EntityAmbientAppearance - Entity watching from distance
 * Entity appears 20-40 blocks away, standing still, watching player
 * Vanishes if:
 * - Player looks directly for >1 second
 * - Player gets within 10 blocks
 * - Player looks away
 * Lasts 3-5 seconds max
 * No sound, particles, or chat
 * Spawns in: tree lines, behind rocks, hills, outside base
 */
public class EntityAmbientAppearanceEvent {
    
    private static final int MIN_DISTANCE = 20;
    private static final int MAX_DISTANCE = 40;
    private static final int MIN_DURATION = 60; // 3 seconds
    private static final int MAX_DURATION = 100; // 5 seconds
    private static final int LOOK_TIMEOUT = 20; // 1 second of looking
    private static final int CLOSE_DISTANCE = 10;
    
    // Track active ambient entities
    private static final Map<UUID, AmbientEntityData> activeEntities = new HashMap<>();
    
    /**
     * Trigger ambient entity appearance
     */
    public static void trigger(ServerWorld world, ServerPlayerEntity player) {
        // Find suitable spawn location
        BlockPos spawnPos = findAmbientSpawnLocation(world, player);
        
        if (spawnPos == null) {
            return; // No suitable location
        }
        
        // Spawn TheME entity
        TheMEEntity entity = ModEntities.THE_ME.create(world);
        if (entity == null) {
            return;
        }
        
        entity.refreshPositionAndAngles(
            spawnPos.getX() + 0.5,
            spawnPos.getY(),
            spawnPos.getZ() + 0.5,
            0, 0
        );
        
        // Make entity face player
        Vec3d playerPos = player.getPos();
        entity.lookAt(net.minecraft.entity.EntityAnchorArgumentType.EntityAnchor.EYES, playerPos);
        
        // Disable AI so it just stands there
        entity.setNoAi(true);
        
        world.spawnEntity(entity);
        
        // Track this ambient entity
        int duration = MIN_DURATION + world.random.nextInt(MAX_DURATION - MIN_DURATION);
        activeEntities.put(entity.getUuid(), new AmbientEntityData(
            player.getUuid(),
            duration,
            0
        ));
    }
    
    /**
     * Tick all ambient entities (call from server tick)
     */
    public static void tick(ServerWorld world) {
        Iterator<Map.Entry<UUID, AmbientEntityData>> iterator = activeEntities.entrySet().iterator();
        
        while (iterator.hasNext()) {
            Map.Entry<UUID, AmbientEntityData> entry = iterator.next();
            UUID entityUuid = entry.getKey();
            AmbientEntityData data = entry.getValue();
            
            TheMEEntity entity = (TheMEEntity) world.getEntity(entityUuid);
            ServerPlayerEntity player = world.getServer().getPlayerManager().getPlayer(data.targetPlayerUuid);
            
            if (entity == null || player == null || !entity.isAlive()) {
                iterator.remove();
                continue;
            }
            
            // Check vanish conditions
            boolean shouldVanish = false;
            
            // Duration expired
            if (data.ticksRemaining <= 0) {
                shouldVanish = true;
            }
            
            // Player too close
            if (entity.distanceTo(player) < CLOSE_DISTANCE) {
                shouldVanish = true;
            }
            
            // Player looking at entity
            if (isPlayerLookingAtEntity(player, entity)) {
                data.lookingTicks++;
                if (data.lookingTicks > LOOK_TIMEOUT) {
                    shouldVanish = true;
                }
            } else {
                // Player looked away - vanish
                if (data.lookingTicks > 0) {
                    shouldVanish = true;
                }
            }
            
            if (shouldVanish) {
                entity.discard();
                iterator.remove();
            } else {
                data.ticksRemaining--;
            }
        }
    }
    
    /**
     * Check if player is looking at entity
     */
    private static boolean isPlayerLookingAtEntity(ServerPlayerEntity player, TheMEEntity entity) {
        Vec3d playerLook = player.getRotationVec(1.0f);
        Vec3d toEntity = entity.getPos().subtract(player.getPos()).normalize();
        
        double dotProduct = playerLook.dotProduct(toEntity);
        
        // If dot product > 0.95, player is looking fairly directly at entity
        return dotProduct > 0.95;
    }
    
    /**
     * Find suitable location for ambient entity spawn
     */
    private static BlockPos findAmbientSpawnLocation(ServerWorld world, ServerPlayerEntity player) {
        BlockPos playerPos = player.getBlockPos();
        
        for (int attempt = 0; attempt < 30; attempt++) {
            // Random angle and distance
            double angle = world.random.nextDouble() * Math.PI * 2;
            int distance = MIN_DISTANCE + world.random.nextInt(MAX_DISTANCE - MIN_DISTANCE);
            
            int x = playerPos.getX() + (int)(Math.cos(angle) * distance);
            int z = playerPos.getZ() + (int)(Math.sin(angle) * distance);
            
            // Find ground level
            BlockPos testPos = new BlockPos(x, playerPos.getY(), z);
            testPos = world.getTopPosition(net.minecraft.world.Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, testPos);
            
            // Check if location is suitable (solid ground, not too exposed)
            if (isSuitableAmbientLocation(world, testPos, playerPos)) {
                return testPos;
            }
        }
        
        return null;
    }
    
    /**
     * Check if location is suitable for ambient spawning
     */
    private static boolean isSuitableAmbientLocation(ServerWorld world, BlockPos pos, BlockPos playerPos) {
        // Must have solid ground
        if (!world.getBlockState(pos.down()).isSolidBlock(world, pos.down())) {
            return false;
        }
        
        // Must have air to stand in
        if (!world.getBlockState(pos).isAir() || !world.getBlockState(pos.up()).isAir()) {
            return false;
        }
        
        // Prefer locations with some cover nearby (trees, blocks)
        int coverBlocks = 0;
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                BlockPos checkPos = pos.add(x, 0, z);
                if (!world.getBlockState(checkPos).isAir()) {
                    coverBlocks++;
                }
            }
        }
        
        // Should have some cover but not be completely enclosed
        return coverBlocks > 3 && coverBlocks < 15;
    }
    
    /**
     * Data class for ambient entity tracking
     */
    private static class AmbientEntityData {
        final UUID targetPlayerUuid;
        int ticksRemaining;
        int lookingTicks;
        
        AmbientEntityData(UUID targetPlayerUuid, int ticksRemaining, int lookingTicks) {
            this.targetPlayerUuid = targetPlayerUuid;
            this.ticksRemaining = ticksRemaining;
            this.lookingTicks = lookingTicks;
        }
    }
}
