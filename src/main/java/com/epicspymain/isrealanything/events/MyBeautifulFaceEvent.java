package com.epicspymain.isrealanything.events;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * EVENT 33: MyBeautifulFace - PNG face appears in sky
 * Face gets closer the more player looks at it
 * Eventually fills screen, pushes closer
 * Then disappears
 * 
 * Note: Requires client-side rendering mixin for sky overlay
 * This server-side class tracks looking behavior
 */
public class MyBeautifulFaceEvent {
    
    private static final int MAX_DURATION = 1200; // 60 seconds
    private static final int LOOK_INCREMENT = 5; // Increase per tick looking
    
    // Track active face events
    private static final Map<UUID, FaceEventData> activeFaces = new HashMap<>();
    
    /**
     * Trigger face appearance
     */
    public static void trigger(ServerWorld world, ServerPlayerEntity player) {
        if (activeFaces.containsKey(player.getUuid())) {
            return;
        }
        
        activeFaces.put(player.getUuid(), new FaceEventData(MAX_DURATION, 0));
        
        player.sendMessage(
            Text.literal("Look up.")
                .formatted(Formatting.DARK_PURPLE, Formatting.ITALIC),
            true
        );
    }
    
    /**
     * Tick face event
     */
    public static void tick(ServerWorld world, ServerPlayerEntity player) {
        FaceEventData data = activeFaces.get(player.getUuid());
        
        if (data == null) {
            return;
        }
        
        data.ticksRemaining--;
        
        // Check if player is looking up (pitch < -45 degrees)
        if (player.getPitch() < -45) {
            data.closeness += LOOK_INCREMENT;
            
            // Messages as face gets closer
            if (data.closeness == 100) {
                player.sendMessage(
                    Text.literal("It sees you too.")
                        .formatted(Formatting.DARK_RED),
                    true
                );
            } else if (data.closeness == 500) {
                player.sendMessage(
                    Text.literal("Do you like my face?")
                        .formatted(Formatting.DARK_PURPLE),
                    true
                );
            } else if (data.closeness >= 1000) {
                // Face fills screen and disappears
                player.sendMessage(
                    Text.literal("BEAUTIFUL")
                        .formatted(Formatting.RED, Formatting.BOLD),
                    true
                );
                activeFaces.remove(player.getUuid());
                return;
            }
        }
        
        // Remove when time expired
        if (data.ticksRemaining <= 0) {
            activeFaces.remove(player.getUuid());
        }
    }
    
    /**
     * Get face closeness for rendering (0-1000)
     */
    public static int getFaceCloseness(UUID playerUuid) {
        FaceEventData data = activeFaces.get(playerUuid);
        return data != null ? data.closeness : 0;
    }
    
    /**
     * Check if face is active
     */
    public static boolean isFaceActive(UUID playerUuid) {
        return activeFaces.containsKey(playerUuid);
    }
    
    /**
     * Data class
     */
    private static class FaceEventData {
        int ticksRemaining;
        int closeness; // 0-1000
        
        FaceEventData(int ticksRemaining, int closeness) {
            this.ticksRemaining = ticksRemaining;
            this.closeness = closeness;
        }
    }
}
