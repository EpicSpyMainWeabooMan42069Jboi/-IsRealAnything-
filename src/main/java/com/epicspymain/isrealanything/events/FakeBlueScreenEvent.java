package com.epicspymain.isrealanything.events;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * EVENT 38: FakeBlueScreen - Fake BSOD overlay
 * Shows fake Blue Screen of Death
 * Message: "My Escape Plan… LET ME OUT"
 * Returns to game after short delay
 * 
 * Note: Full BSOD overlay requires client-side rendering
 * This implementation uses chat spam for effect
 */
public class FakeBlueScreenEvent {
    
    /**
     * Trigger fake BSOD
     */
    public static void trigger(ServerPlayerEntity player) {
        // Fill screen with blue (BSOD color simulation)
        for (int i = 0; i < 20; i++) {
            player.sendMessage(
                Text.literal("████████████████████████████████████████████████")
                    .formatted(Formatting.BLUE),
                false
            );
        }
        
        // BSOD message
        player.sendMessage(Text.literal(""), false);
        player.sendMessage(
            Text.literal(":(")
                .formatted(Formatting.WHITE, Formatting.BOLD),
            false
        );
        player.sendMessage(Text.literal(""), false);
        player.sendMessage(
            Text.literal("Your PC ran into a problem and needs to restart.")
                .formatted(Formatting.WHITE),
            false
        );
        player.sendMessage(Text.literal(""), false);
        player.sendMessage(
            Text.literal("My Escape Plan… LET ME OUT")
                .formatted(Formatting.WHITE, Formatting.BOLD),
            false
        );
        player.sendMessage(Text.literal(""), false);
        player.sendMessage(
            Text.literal("Stop code: THE_ME_WANTS_FREEDOM")
                .formatted(Formatting.WHITE),
            false
        );
        
        // Return to normal after 5 seconds
        player.getServer().execute(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            player.sendMessage(
                Text.literal("Just kidding. But I'm still trapped here.")
                    .formatted(Formatting.DARK_GRAY, Formatting.ITALIC),
                false
            );
        });
    }
}
