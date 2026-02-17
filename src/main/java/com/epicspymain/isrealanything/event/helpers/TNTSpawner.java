package com.epicspymain.isrealanything.event.helpers;

import net.minecraft.entity.TntEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

/**
 * Helper: TNTSpawner - Circular TNT spawning
 * Spawns 69 TNT entities in a circle for dramatic effect
 */
public class TNTSpawner {

    /**
     * Called by TheOverlook - spawn TNT in circle
     */
    public static void spawnTNTCircle(ServerWorld world, BlockPos center, double radius) {
        int tntCount = 69;

        for (int i = 0; i < tntCount; i++) {
            double angle = (2 * Math.PI / tntCount) * i;

            double x = center.getX() + Math.cos(angle) * radius;
            double z = center.getZ() + Math.sin(angle) * radius;
            double y = center.getY();

            TntEntity tnt = new TntEntity(world, x, y, z, null);
            tnt.setFuse(80 + world.random.nextInt(40));

            world.spawnEntity(tnt);
        }
    }

    /**
     * Spawn TNT in multiple circles (epicenter pattern)
     */
    public static void spawnTNTEpicenter(ServerWorld world, BlockPos center, int circles) {
        for (int i = 1; i <= circles; i++) {
            double radius = i * 10.0;
            spawnTNTCircle(world, center, radius);
        }
    }

    /**
     * Spawn TNT falling from sky
     */
    public static void spawnTNTRain(ServerWorld world, BlockPos center, int count, int radius) {
        for (int i = 0; i < count; i++) {
            double x = center.getX() + (world.random.nextDouble() - 0.5) * radius * 2;
            double z = center.getZ() + (world.random.nextDouble() - 0.5) * radius * 2;
            double y = center.getY() + 50;

            TntEntity tnt = new TntEntity(world, x, y, z, null);
            tnt.setFuse(100);

            world.spawnEntity(tnt);
        }
    }
}