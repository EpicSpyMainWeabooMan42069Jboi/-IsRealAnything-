package com.epicspymain.isrealanything.entity;

import com.epicspymain.isrealanything.entity.custom.TheOtherMEEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.SpawnReason;
import net.minecraft.world.Heightmap;

/**
 * Helper class for spawning TheOtherME entities at specific locations.
 * Used by StalkingBehavior and events.
 */
public class TheOtherMEEntitySpawner {


    public static TheOtherMEEntity spawnAt(ServerWorld world, BlockPos pos) {
        TheOtherMEEntity entity = ModEntities.THEOTHERME_ENTITY.create(world, SpawnReason.COMMAND);

        if (entity != null) {
            entity.refreshPositionAndAngles(
                    pos.getX() + 0.5,
                    pos.getY(),
                    pos.getZ() + 0.5,
                    0,
                    0
            );

            world.spawnEntity(entity);
            return entity;
        }

        return null;
    }

    /**
     * Spawns TheOtherME entity behind player at specified distance.
     */
    public static TheOtherMEEntity spawnBehindPlayer(ServerWorld world, BlockPos playerPos, float playerYaw, int distance) {
        // Calculate position behind player
        double radians = Math.toRadians(playerYaw);
        int x = (int) (playerPos.getX() - distance * Math.sin(radians));
        int z = (int) (playerPos.getZ() + distance * Math.cos(radians));
        int y = world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z);

        BlockPos behindPos = new BlockPos(x, y, z);

        return spawnAt(world, behindPos);
    }

    /**
     * Spawns TheOtherME entity near player (random position).
     */
    public static TheOtherMEEntity spawnNearPlayer(ServerWorld world, BlockPos playerPos, int minDistance, int maxDistance) {
        for (int attempt = 0; attempt < 10; attempt++) {
            double angle = world.getRandom().nextDouble() * 2 * Math.PI;
            int distance = minDistance + world.getRandom().nextInt(maxDistance - minDistance);

            int x = (int) (playerPos.getX() + distance * Math.cos(angle));
            int z = (int) (playerPos.getZ() + distance * Math.sin(angle));
            int y = world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z);

            BlockPos spawnPos = new BlockPos(x, y, z);

            // Check if position is valid
            if (world.getBlockState(spawnPos).isAir() &&
                    world.getBlockState(spawnPos.up()).isAir()) {
                return spawnAt(world, spawnPos);
            }
        }

        return null;
    }

    /**
     * Spawns multiple TheOtherME entities in a group.
     */
    public static int spawnGroup(ServerWorld world, BlockPos center, int count, int radius) {
        int spawned = 0;

        for (int i = 0; i < count; i++) {
            TheOtherMEEntity entity = spawnNearPlayer(world, center, radius / 2, radius);
            if (entity != null) {
                spawned++;
            }
        }

        return spawned;
    }
}