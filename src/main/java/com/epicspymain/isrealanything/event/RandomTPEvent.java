package com.epicspymain.isrealanything.event;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

/**
 * EVENT 12: RandomTP - Teleports player to mysterious coordinates
 * Teleports player to (1444, -50, 33222248)
 */
public class RandomTPEvent {
    
    private static final double TP_X = 1444.0;
    private static final double TP_Y = -50.0;
    private static final double TP_Z = 33222248.0;
    
    /**
     * Trigger random teleport
     */
    public static void trigger(ServerPlayerEntity player) {
        // Store original position (for reference/debugging)
        Vec3d originalPos = player.getPos();
        
        // Teleport player
        player.teleport(TP_X, TP_Y, TP_Z);
        
        // Send cryptic message after teleport
        player.sendMessage(
            Text.literal("WHERE AM I?").formatted(Formatting.DARK_RED, Formatting.BOLD),
            true // Action bar
        );
        
        // Send coordinates in chat after delay
        player.getServer().execute(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            player.sendMessage(
                Text.literal("Coordinates: " + (int)TP_X + ", " + (int)TP_Y + ", " + (int)TP_Z)
                    .formatted(Formatting.GRAY),
                false
            );
        });
    }
}
