package com.epicspymain.isrealanything.events;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * EVENT 17: CameraDistortion - FOV and camera manipulation
 * FOV oscillates 30-110 over 15 seconds
 * Creates "breathing/heartbeat" effect
 * Near end: camera snaps 180° for 0.3 seconds (shows behind player)
 * Returns to normal immediately
 * 
 * Note: FOV manipulation requires client-side packet or mixin.
 * This implementation uses server-side tracking and effects.
 */
public class CameraDistortionEvent {
    
    private static final int DISTORTION_DURATION = 300; // 15 seconds
    private static final int SNAP_TIMING = 280; // At 14 seconds
    private static final int SNAP_DURATION = 6; // 0.3 seconds
    
    // Track active distortions
    private static final Map<UUID, DistortionData> activeDistortions = new HashMap<>();
    
    /**
     * Trigger camera distortion event
     */
    public static void trigger(ServerWorld world, ServerPlayerEntity player) {
        // Don't stack distortions
        if (activeDistortions.containsKey(player.getUuid())) {
            return;
        }
        
        activeDistortions.put(player.getUuid(), new DistortionData(
            DISTORTION_DURATION,
            false
        ));
        
        // Apply nausea for visual distortion
        player.addStatusEffect(new StatusEffectInstance(
            StatusEffects.NAUSEA,
            DISTORTION_DURATION,
            0,
            false,
            false,
            false
        ));
    }
    
    /**
     * Tick camera distortions (call from server tick)
     */
    public static void tick(ServerWorld world) {
        activeDistortions.entrySet().removeIf(entry -> {
            UUID playerUuid = entry.getKey();
            DistortionData data = entry.getValue();
            
            ServerPlayerEntity player = world.getServer().getPlayerManager().getPlayer(playerUuid);
            
            if (player == null) {
                return true; // Remove if player gone
            }
            
            data.ticksRemaining--;
            
            // Check for camera snap timing
            if (data.ticksRemaining == (DISTORTION_DURATION - SNAP_TIMING) && !data.snapped) {
                performCameraSnap(world, player);
                data.snapped = true;
            }
            
            // Remove when complete
            return data.ticksRemaining <= 0;
        });
    }
    
    /**
     * Perform 180° camera snap
     */
    private static void performCameraSnap(ServerWorld world, ServerPlayerEntity player) {
        // Store original rotation
        float originalYaw = player.getYaw();
        float originalPitch = player.getPitch();
        
        // Snap 180°
        player.setYaw(originalYaw + 180f);
        
        // Apply blindness during snap for extra horror
        player.addStatusEffect(new StatusEffectInstance(
            StatusEffects.BLINDNESS,
            SNAP_DURATION,
            0,
            false,
            false,
            false
        ));
        
        // Return to normal after snap duration
        world.getServer().execute(() -> {
            try {
                Thread.sleep(SNAP_DURATION * 50L); // Convert ticks to ms
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            if (player.isAlive()) {
                player.setYaw(originalYaw);
                player.setPitch(originalPitch);
            }
        });
    }
    
    /**
     * Get current FOV multiplier for oscillation effect
     * Call from client-side rendering if implementing FOV changes
     */
    public static float getFOVMultiplier(UUID playerUuid, float partialTicks) {
        DistortionData data = activeDistortions.get(playerUuid);
        
        if (data == null) {
            return 1.0f; // Normal FOV
        }
        
        // Calculate oscillation progress (0.0 to 1.0)
        float progress = 1.0f - ((float)data.ticksRemaining / DISTORTION_DURATION);
        
        // Sine wave oscillation (breathing effect)
        float oscillation = (float)Math.sin(progress * Math.PI * 6); // 6 cycles over duration
        
        // Map to FOV range: 0.3x to 1.1x (30% to 110% of normal FOV)
        return 0.7f + (oscillation * 0.4f);
    }
    
    /**
     * Data class for distortion tracking
     */
    private static class DistortionData {
        int ticksRemaining;
        boolean snapped;
        
        DistortionData(int ticksRemaining, boolean snapped) {
            this.ticksRemaining = ticksRemaining;
            this.snapped = snapped;
        }
    }
}
