package com.epicspymain.isrealanything.ai;

import com.epicspymain.isrealanything.IsRealAnything;
import com.epicspymain.isrealanything.entity.client.TheMEEntitySpawner;
import com.epicspymain.isrealanything.entity.client.TheOtherMEEntitySpawner;
import com.epicspymain.isrealanything.sound.ModSounds;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;


public class StalkingController {
    
    private static int currentIntensity = 0;
    private static boolean overlookTriggered = false;
    private static int ticksSinceLastPeek = 0;
    private static int ticksSinceLastVanish = 0;
    
    // Intensity level constants
    public static final int INTENSITY_NONE = 0;
    public static final int INTENSITY_LOW = 1;      // Rare, distant
    public static final int INTENSITY_MEDIUM = 2;    // Occasional, medium distance
    public static final int INTENSITY_HIGH = 3;      // Frequent, close proximity
    public static final int INTENSITY_EXTREME = 4;   // Aggressive, teleporting
    public static final int INTENSITY_MAXIMUM = 5;   // Constant presence
    
    /**
     * Sets the stalking intensity level (0-5).
     * 
     * @param level Intensity level (0 = none, 5 = maximum)
     */
    public static void setIntensity(int level) {
        if (overlookTriggered) {
            IsRealAnything.LOGGER.warn("Cannot set intensity - OVERLOOK_TRIGGERED is active");
            return;
        }
        
        int oldIntensity = currentIntensity;
        currentIntensity = Math.max(0, Math.min(5, level));
        
        if (currentIntensity != oldIntensity) {
            IsRealAnything.LOGGER.info("Stalking intensity changed: {} -> {}", oldIntensity, currentIntensity);
        }
        
        // Reset stalking behavior when intensity changes
        if (currentIntensity == 0) {
            StalkingBehavior.reset();
        }
    }
    
    /**
     * Gets current stalking intensity.
     */
    public static int getIntensity() {
        return overlookTriggered ? 0 : currentIntensity;
    }
    
    /**
     * Increases intensity by one level.
     */
    public static void increaseIntensity() {
        setIntensity(currentIntensity + 1);
    }
    
    /**
     * Decreases intensity by one level.
     */
    public static void decreaseIntensity() {
        setIntensity(currentIntensity - 1);
    }
    
    /**
     * Triggers a "peek" event - entity appears briefly watching player.
     * 
     * @param world The server world
     * @param player The target player
     * @param distance Distance from player (blocks)
     */
    public static void triggerPeek(ServerWorld world, ServerPlayerEntity player, int distance) {
        if (overlookTriggered) {
            return;
        }
        
        if (ticksSinceLastPeek < 200) {
            IsRealAnything.LOGGER.debug("Peek on cooldown");
            return;
        }
        
        // Find a position to peek from
        BlockPos playerPos = player.getBlockPos();
        BlockPos peekPos = findPeekPosition(world, playerPos, distance);
        
        if (peekPos != null) {
            // Spawn entity at peek position
            TheMEEntitySpawner.spawnAt(world, peekPos);
            
            // Play subtle sound
            world.playSound(null, peekPos, ModSounds.SCREAM,
                SoundCategory.AMBIENT, 0.4f, 0.9f);
            
            ticksSinceLastPeek = 0;
            IsRealAnything.LOGGER.debug("Peek triggered at distance {}", distance);
        }
    }
    
    /**
     * Triggers entity appearing behind player (jumpscare).
     * 
     * @param world The server world
     * @param player The target player
     */
    public static void triggerBehindPlayer(ServerWorld world, ServerPlayerEntity player) {
        if (overlookTriggered) {
            return;
        }
        
        BlockPos playerPos = player.getBlockPos();
        float playerYaw = player.getYaw();
        
        // Spawn behind player at varying distances based on intensity
        int distance = switch (currentIntensity) {
            case 1, 2 -> 8;
            case 3 -> 5;
            case 4, 5 -> 3;
            default -> 10;
        };
        

        if (currentIntensity >= 4) {
            TheOtherMEEntitySpawner.spawnBehindPlayer(world, playerPos, playerYaw, distance);

        
        IsRealAnything.LOGGER.debug("Behind player trigger (distance: {})", distance);
    }
    
    /**
     * Triggers immediate vanish of all stalking entities.
     * Used when player gets too close or looks directly at entity.
     */
    public static void triggerVanish() {
        if (overlookTriggered) {
            return;
        }
        
        ticksSinceLastVanish = 0;
        
        // All vanishing is handled by StalkingBehavior when entities are looked at
        IsRealAnything.LOGGER.debug("Vanish trigger");
    }
    
    /**
     * Triggers a group spawn event (horror sequence).
     * 
     * @param world The server world
     * @param player The target player
     * @param count Number of entities to spawn
     */
    public static void triggerGroupSpawn(ServerWorld world, ServerPlayerEntity player, int count) {
        if (overlookTriggered) {
            return;
        }
        
        if (currentIntensity < 3) {
            IsRealAnything.LOGGER.debug("Intensity too low for group spawn");
            return;
        }
        
        BlockPos playerPos = player.getBlockPos();
        int radius = 20;
        
        // Spawn group of TheOtherME entities
        int spawned = TheOtherMEEntitySpawner.spawnGroup(world, playerPos, count, radius);
        

            IsRealAnything.LOGGER.info("Group spawn triggered: {} entities", spawned);
        }
    }
    
    /**
     * Triggers stalking based on time of day.
     * More aggressive at night.
     * 
     * @param world The server world
     * @param player The target player
     */
    public static void triggerTimeBased(ServerWorld world, ServerPlayerEntity player) {
        if (overlookTriggered) {
            return;
        }
        
        if (currentIntensity == 0) {
            return;
        }
        
        boolean isNight = world.isNight();
        
        if (isNight) {
            // Night: more aggressive
            if (world.getRandom().nextFloat() < 0.1f * currentIntensity) {
                triggerBehindPlayer(world, player);
            }
        } else {
            // Day: subtle peeking
            if (world.getRandom().nextFloat() < 0.05f * currentIntensity) {
                triggerPeek(world, player, 32);
            }
        }
    }
    
    /**
     * Triggers stalking when player is in certain locations.
     * 
     * @param world The server world
     * @param player The target player
     */
    public static void triggerLocationBased(ServerWorld world, ServerPlayerEntity player) {
        if (overlookTriggered) {
            return;
        }
        
        if (currentIntensity == 0) {
            return;
        }
        
        BlockPos pos = player.getBlockPos();
        boolean isUnderground = pos.getY() < 50;
        boolean isDark = world.getLightLevel(pos) < 7;
        
        if (isUnderground || isDark) {
            // Underground or dark: more likely to stalk
            if (world.getRandom().nextFloat() < 0.15f * currentIntensity) {
                triggerPeek(world, player, 16);
            }
        }
    }
    
    /**
     * Updates stalking controller (called every tick).
     */
    public static void tick() {
        if (overlookTriggered) {
            return;
        }
        
        ticksSinceLastPeek++;
        ticksSinceLastVanish++;
    }
    
    /**
     * Sets OVERLOOK_TRIGGERED state.
     * When true, all stalking stops immediately.
     */
    public static void setOverlookTriggered(boolean triggered) {
        overlookTriggered = triggered;
        
        if (triggered) {
            currentIntensity = 0;
            StalkingBehavior.stopAllStalking();
            IsRealAnything.LOGGER.info("StalkingController: OVERLOOK_TRIGGERED set to true - all stalking stopped");
        }
    }
    
    /**
     * Gets OVERLOOK_TRIGGERED state.
     */
    public static boolean isOverlookTriggered() {
        return overlookTriggered;
    }
    
    /**
     * Resets stalking controller to initial state.
     */
    public static void reset() {
        currentIntensity = 0;
        overlookTriggered = false;
        ticksSinceLastPeek = 0;
        ticksSinceLastVanish = 0;
        StalkingBehavior.reset();
        IsRealAnything.LOGGER.info("StalkingController reset");
    }
    
    /**
     * Finds a suitable peek position near player.
     */
    private static BlockPos findPeekPosition(ServerWorld world, BlockPos center, int distance) {
        for (int attempt = 0; attempt < 15; attempt++) {
            double angle = world.getRandom().nextDouble() * 2 * Math.PI;
            int x = (int) (center.getX() + distance * Math.cos(angle));
            int z = (int) (center.getZ() + distance * Math.sin(angle));
            int y = world.getTopY(x, z);
            
            BlockPos pos = new BlockPos(x, y, z);
            
            // Check if position is valid
            if (world.getBlockState(pos).isAir() && 
                world.getBlockState(pos.up()).isAir() &&
                world.getLightLevel(pos) < 10) {
                return pos;
            }
        }
        
        return null;
    }
    
    /**
     * Gets recommended intensity for game day.
     * Intensity increases over time.
     */
    public static int getRecommendedIntensity(long gameDay) {
        if (gameDay <= 2) {
            return INTENSITY_NONE;
        } else if (gameDay <= 5) {
            return INTENSITY_LOW;
        } else if (gameDay <= 10) {
            return INTENSITY_MEDIUM;
        } else if (gameDay <= 20) {
            return INTENSITY_HIGH;
        } else if (gameDay <= 35) {
            return INTENSITY_EXTREME;
        } else {
            return INTENSITY_MAXIMUM;
        }
    }
}
