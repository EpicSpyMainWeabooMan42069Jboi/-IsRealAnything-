package com.epicspymain.isrealanything.event;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * EVENT 25: OverlayText - Flashing obsessive messages
 * Displays "LOVE ME" / "LIAR" / "BE WITH ME" flashing across screen
 * Lasts 6 seconds (120 ticks)
 * Creates overwhelming, possessive atmosphere
 */
public class OverlayTextEvent {
    
    private static final int DURATION = 120; // 6 seconds
    private static final int FLASH_INTERVAL = 10; // 0.5 seconds
    
    private static final String[] OVERLAY_MESSAGES = {
        "LOVE ME",
        "LIAR",
        "BE WITH ME",
        "LOOK AT ME",
        "WHY WON'T YOU LOVE ME",
        "I NEED YOU",
        "DON'T LEAVE ME",
        "YOU'RE MINE"
    };
    
    // Track active overlays
    private static final Map<UUID, OverlayData> activeOverlays = new HashMap<>();
    
    /**
     * Trigger overlay text event
     */
    public static void trigger(ServerPlayerEntity player) {
        // Don't stack overlays
        if (activeOverlays.containsKey(player.getUuid())) {
            return;
        }
        
        activeOverlays.put(player.getUuid(), new OverlayData(
            DURATION,
            0
        ));
    }
    
    /**
     * Tick overlay texts
     */
    public static void tick(ServerPlayerEntity player) {
        OverlayData data = activeOverlays.get(player.getUuid());
        
        if (data == null) {
            return;
        }
        
        data.ticksRemaining--;
        data.flashTimer++;
        
        // Flash message
        if (data.flashTimer >= FLASH_INTERVAL) {
            data.flashTimer = 0;
            
            // Pick random message
            String message = OVERLAY_MESSAGES[
                player.getWorld().random.nextInt(OVERLAY_MESSAGES.length)
            ];
            
            // Send as title (large text)
            player.sendMessage(
                Text.literal(message)
                    .formatted(Formatting.DARK_RED, Formatting.BOLD, Formatting.OBFUSCATED),
                true // Overlay/action bar
            );
            
            // Also spam in chat for full effect
            if (data.ticksRemaining % 20 == 0) { // Every second
                player.sendMessage(
                    Text.literal(message)
                        .formatted(Formatting.RED, Formatting.BOLD),
                    false
                );
            }
        }
        
        // Remove when complete
        if (data.ticksRemaining <= 0) {
            activeOverlays.remove(player.getUuid());
            
            // Clear action bar
            player.sendMessage(Text.literal(""), true);
        }
    }
    
    /**
     * Check if player has active overlay
     */
    public static boolean hasActiveOverlay(UUID playerUuid) {
        return activeOverlays.containsKey(playerUuid);
    }
    
    /**
     * Data class for overlay tracking
     */
    private static class OverlayData {
        int ticksRemaining;
        int flashTimer;
        
        OverlayData(int ticksRemaining, int flashTimer) {
            this.ticksRemaining = ticksRemaining;
            this.flashTimer = flashTimer;
        }
    }
}
