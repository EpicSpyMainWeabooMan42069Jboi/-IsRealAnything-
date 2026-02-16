package com.epicspymain.isrealanything.entity;

import com.epicspymain.isrealanything.entity.custom.TheMEEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

/**
 * Helper class for spawning TheME entities at specific locations.
 * Used by StalkingBehavior and events.
 */
public class TheMEEntitySpawner {

    /**
     * Spawns TheME entity at a specific position.
     */
    public static TheMEEntity spawnAt(ServerWorld world, BlockPos pos) {
        TheMEEntity entity = ModEntities.THE_ME.create(world);

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
     * Spawns TheME entity behind player at specified distance.
     */
    public static TheMEEntity spawnBehindPlayer(ServerWorld world, BlockPos playerPos, float playerYaw, int distance) {
        // Calculate position behind player
        double radians = Math.toRadians(playerYaw);
        int x = (int) (playerPos.getX() - distance * Math.sin(radians));
        int z = (int) (playerPos.getZ() + distance * Math.cos(radians));
        int y = world.getTopY(x, z);

        BlockPos behindPos = new BlockPos(x, y, z);

        return spawnAt(world, behindPos);
    }

    /**
     * Spawns TheME entity near player (random position).
     */
    public static TheMEEntity spawnNearPlayer(ServerWorld world, BlockPos playerPos, int minDistance, int maxDistance) {
        for (int attempt = 0; attempt < 10; attempt++) {
            double angle = world.getRandom().nextDouble() * 2 * Math.PI;
            int distance = minDistance + world.getRandom().nextInt(maxDistance - minDistance);

            int x = (int) (playerPos.getX() + distance * Math.cos(angle));
            int z = (int) (playerPos.getZ() + distance * Math.sin(angle));
            int y = world.getTopY(x, z);

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
     * Spawns multiple TheME entities in a group.
     */
    public static int spawnGroup(ServerWorld world, BlockPos center, int count, int radius) {
        int spawned = 0;

        for (int i = 0; i < count; i++) {
            TheMEEntity entity = spawnNearPlayer(world, center, radius / 2, radius);
            if (entity != null) {
                spawned++;
            }
        }

        return spawned;
    }
}