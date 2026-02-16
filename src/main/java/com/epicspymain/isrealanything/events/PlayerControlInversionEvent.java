package com.epicspymain.isrealanything.events;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * EVENT 35: PlayerControlInversion - Control reversal
 * Mouse controls inverted
 * Movement keys backwards
 * Everything in game reversed
 * 
 * Note: Requires client-side mixin for actual control inversion
 * This tracks the effect duration server-side
 */
public class PlayerControlInversionEvent {
    
    private static final int DURATION = 600; // 30 seconds
    private static final Map<UUID, Integer> invertedPlayers = new HashMap<>();
    
    /**
     * Trigger control inversion
     */
    public static void trigger(ServerPlayerEntity player) {
        if (invertedPlayers.containsKey(player.getUuid())) {
            return;
        }
        
        invertedPlayers.put(player.getUuid(), DURATION);
        
        player.sendMessage(
            Text.literal("Everything feels... backwards.")
                .formatted(Formatting.DARK_PURPLE, Formatting.ITALIC),
            true
        );
    }
    
    /**
     * Tick inversions
     */
    public static void tick() {
        invertedPlayers.entrySet().removeIf(entry -> {
            entry.setValue(entry.getValue() - 1);
            return entry.getValue() <= 0;
        });
    }
    
    /**
     * Check if player is inverted
     */
    public static boolean isInverted(UUID playerUuid) {
        return invertedPlayers.containsKey(playerUuid);
    }
}
