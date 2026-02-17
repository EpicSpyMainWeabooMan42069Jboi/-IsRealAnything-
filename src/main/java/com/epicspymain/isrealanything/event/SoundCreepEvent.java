package com.epicspymain.isrealanything.event;

import com.epicspymain.isrealanything.sound.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.List;

/**
 * EVENT 4: SoundCreep - Ambient horror sounds when alone
 * Plays distant footsteps, doors opening, block breaking sounds
 * Only triggers when player is ALONE (no entities nearby)
 */
public class SoundCreepEvent {
    
    private static final int ISOLATION_RADIUS = 50; // Must be alone within 50 blocks
    
    /**
     * Trigger ambient horror sounds if player is alone
     */
    public static void trigger(ServerWorld world, ServerPlayerEntity player) {
        // Check if player is truly alone
        if (!isPlayerAlone(world, player)) {
            return;
        }
        
        // Choose random sound type
        float soundType = world.random.nextFloat();
        
        if (soundType < 0.33f) {
            playDistantFootsteps(world, player);
        } else if (soundType < 0.66f) {
            playDoorSounds(world, player);
        } else {
            playBlockBreakingSounds(world, player);
        }
    }
    
    /**
     * Check if player is alone (no entities within radius)
     */
    private static boolean isPlayerAlone(ServerWorld world, ServerPlayerEntity player) {
        Box searchBox = Box.of(
            player.getPos(),
            ISOLATION_RADIUS * 2,
            ISOLATION_RADIUS * 2,
            ISOLATION_RADIUS * 2
        );
        
        List<Entity> nearbyEntities = world.getOtherEntities(player, searchBox);
        
        // Filter out item entities and non-living entities
        long livingEntityCount = nearbyEntities.stream()
            .filter(entity -> entity instanceof net.minecraft.entity.LivingEntity)
            .count();
        
        return livingEntityCount == 0;
    }
    
    /**
     * Play distant footstep sounds
     */
    private static void playDistantFootsteps(ServerWorld world, ServerPlayerEntity player) {
        // Random position around player (20-40 blocks away)
        double angle = world.random.nextDouble() * Math.PI * 2;
        double distance = 20 + world.random.nextDouble() * 20;
        
        int x = (int)(player.getX() + Math.cos(angle) * distance);
        int y = player.getBlockY();
        int z = (int)(player.getZ() + Math.sin(angle) * distance);
        
        BlockPos soundPos = new BlockPos(x, y, z);
        
        // Play 3-5 footstep sounds with delay
        int stepCount = 3 + world.random.nextInt(3);
        
        for (int i = 0; i < stepCount; i++) {
            final int step = i;
            world.getServer().execute(() -> {
                try {
                    Thread.sleep(300L * step); // 300ms between steps
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                world.playSound(
                    null,
                    soundPos,
                    ModSounds.FOOTSTEPS_HORROR,
                    SoundCategory.AMBIENT,
                    0.4f,
                    0.9f + world.random.nextFloat() * 0.2f
                );
            });
        }
    }
    
    /**
     * Play door opening/closing sounds
     */
    private static void playDoorSounds(ServerWorld world, ServerPlayerEntity player) {
        // Random position around player
        double angle = world.random.nextDouble() * Math.PI * 2;
        double distance = 15 + world.random.nextDouble() * 25;
        
        int x = (int)(player.getX() + Math.cos(angle) * distance);
        int y = player.getBlockY();
        int z = (int)(player.getZ() + Math.sin(angle) * distance);
        
        BlockPos soundPos = new BlockPos(x, y, z);
        
        // Play door creak sound
        world.playSound(
            null,
            soundPos,
            ModSounds.DOOR_CREAK,
            SoundCategory.AMBIENT,
            0.5f,
            1.0f
        );
        
        // Sometimes play door close sound after
        if (world.random.nextFloat() < 0.6f) {
            world.getServer().execute(() -> {
                try {
                    Thread.sleep(1500); // 1.5 seconds later
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                world.playSound(
                    null,
                    soundPos,
                    SoundEvents.BLOCK_WOODEN_DOOR_CLOSE,
                    SoundCategory.AMBIENT,
                    0.4f,
                    0.9f
                );
            });
        }
    }
    
    /**
     * Play block breaking sounds
     */
    private static void playBlockBreakingSounds(ServerWorld world, ServerPlayerEntity player) {
        // Random position around player
        double angle = world.random.nextDouble() * Math.PI * 2;
        double distance = 10 + world.random.nextDouble() * 30;
        
        int x = (int)(player.getX() + Math.cos(angle) * distance);
        int y = player.getBlockY();
        int z = (int)(player.getZ() + Math.sin(angle) * distance);
        
        BlockPos soundPos = new BlockPos(x, y, z);
        
        // Random block breaking sound
        SoundEvent[] breakSounds = {
            SoundEvents.BLOCK_STONE_BREAK,
            SoundEvents.BLOCK_WOOD_BREAK,
            SoundEvents.BLOCK_GRAVEL_BREAK,
            SoundEvents.BLOCK_GRASS_BREAK
        };
        
        SoundEvent breakSound = breakSounds[world.random.nextInt(breakSounds.length)];
        
        world.playSound(
            null,
            soundPos,
            breakSound,
            SoundCategory.BLOCKS,
            0.5f,
            0.8f + world.random.nextFloat() * 0.4f
        );
    }
}
