package com.epicspymain.isrealanything.events.helpers;

import net.minecraft.entity.TntEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

/**
 * Helper: TNTSpawner - Circular TNT spawning
 * Spawns 69 TNT entities in a circle for dramatic effect
 */
public class TNTSpawner {
    
    /**
     * Spawn TNT in circle pattern (69 TNT at specified radius)
     */
    public static void spawnTntInCircle(ServerWorld world, BlockPos center, double radius) {
        int tntCount = 69;
        
        for (int i = 0; i < tntCount; i++) {
            double angle = (2 * Math.PI / tntCount) * i;
            
            double x = center.getX() + Math.cos(angle) * radius;
            double z = center.getZ() + Math.sin(angle) * radius;
            double y = center.getY();
            
            // Create TNT entity
            TntEntity tnt = new TntEntity(world, x, y, z, null);
            tnt.setFuse(80 + world.random.nextInt(40)); // 4-6 seconds
            
            world.spawnEntity(tnt);
        }
    }
    
    /**
     * Spawn TNT in multiple circles (epicenter pattern)
     */
    public static void spawnTntEpicenter(ServerWorld world, BlockPos center, int circles) {
        for (int i = 1; i <= circles; i++) {
            double radius = i * 10.0; // 10, 20, 30 block radius
            spawnTntInCircle(world, center, radius);
        }
    }
    
    /**
     * Spawn TNT rain from sky
     */
    public static void spawnTntRain(ServerWorld world, BlockPos center, int count, int radius) {
        for (int i = 0; i < count; i++) {
            double x = center.getX() + (world.random.nextDouble() - 0.5) * radius * 2;
            double z = center.getZ() + (world.random.nextDouble() - 0.5) * radius * 2;
            double y = center.getY() + 50; // 50 blocks up
            
            TntEntity tnt = new TntEntity(world, x, y, z, null);
            tnt.setFuse(100); // 5 seconds
            
            world.spawnEntity(tnt);
        }
    }
}
