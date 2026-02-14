package com.epicspymain.isrealanything.events;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * EVENT 41: Error404Textures - Texture corruption
 * Everything gets Error 404 texture
 * Models, blocks, items - everything
 * Reality breaks down completely
 * 
 * Note: Requires client-side resource pack override
 * This tracks the effect duration
 */
public class Error404TexturesEvent {
    
    private static final int DURATION = 1200; // 60 seconds
    private static final Set<UUID> affectedPlayers = new HashSet<>();
    
    /**
     * Trigger texture corruption
     */
    public static void trigger(ServerPlayerEntity player) {
        if (affectedPlayers.contains(player.getUuid())) {
            return;
        }
        
        affectedPlayers.add(player.getUuid());
        
        player.sendMessage(
            Text.literal("ERROR 404: TEXTURES NOT FOUND")
                .formatted(Formatting.RED, Formatting.BOLD),
            false
        );
        
        player.sendMessage(
            Text.literal("Reality.exe has stopped working")
                .formatted(Formatting.DARK_RED),
            false
        );
        
        // Auto-remove after duration
        player.getServer().execute(() -> {
            try {
                Thread.sleep(DURATION * 50L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            affectedPlayers.remove(player.getUuid());
            
            player.sendMessage(
                Text.literal("Textures restored... for now.")
                    .formatted(Formatting.GRAY, Formatting.ITALIC),
                false
            );
        });
    }
    
    /**
     * Check if player has error textures
     */
    public static boolean hasErrorTextures(UUID playerUuid) {
        return affectedPlayers.contains(playerUuid);
    }
}
