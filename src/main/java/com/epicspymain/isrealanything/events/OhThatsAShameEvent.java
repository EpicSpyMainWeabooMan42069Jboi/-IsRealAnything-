package com.epicspymain.isrealanything.events;

import com.epicspymain.isrealanything.sound.ModSounds;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * EVENT 22: OhThatsAShame - Clears entire inventory
 * Ultimate punishment/grief event
 * Completely empties player's inventory without warning
 */
public class OhThatsAShameEvent {
    
    /**
     * Trigger inventory clear event
     */
    public static void trigger(ServerWorld world, ServerPlayerEntity player) {
        // Clear entire inventory
        player.getInventory().clear();
        
        // Play sinister sound
        world.playSound(
            null,
            player.getBlockPos(),
            ModSounds.LAUGH_DISTANT,
            SoundCategory.HOSTILE,
            1.0f,
            0.6f
        );
        
        // Send mocking message
        player.sendMessage(
            Text.literal("Oh, that's a shame.")
                .formatted(Formatting.DARK_RED, Formatting.ITALIC),
            false
        );
        
        // Follow-up message
        world.getServer().execute(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            player.sendMessage(
                Text.literal("Did you really think you owned those items?")
                    .formatted(Formatting.GRAY, Formatting.ITALIC),
                false
            );
            
            // Static noise
            world.playSound(
                null,
                player.getBlockPos(),
                ModSounds.STATIC_NOISE,
                SoundCategory.AMBIENT,
                0.7f,
                0.7f
            );
        });
    }
}
