package com.epicspymain.isrealanything.events;

import com.epicspymain.isrealanything.sound.ModSounds;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * EVENT 13: WhiteScreenJumpscare - Full screen white overlay
 * Shows white screen for 1 second using blindness effect
 * Accompanied by jumpscare sound
 * 
 * Note: Full texture overlay would require client-side rendering.
 * This implementation uses blindness + title screen as alternative.
 */
public class WhiteScreenJumpscareEvent {
    
    /**
     * Trigger white screen jumpscare
     */
    public static void trigger(ServerWorld world, ServerPlayerEntity player) {
        // Blindness effect (1 second)
        player.addStatusEffect(new StatusEffectInstance(
            StatusEffects.BLINDNESS,
            20, // 1 second
            10, // Max amplifier for whitest effect
            false,
            false,
            false
        ));
        
        // Full screen "white" title (simulate white screen)
        player.sendMessage(
            Text.literal("                                                                ")
                .formatted(Formatting.WHITE),
            true
        );
        
        // Show multiple blank lines to cover screen
        for (int i = 0; i < 20; i++) {
            player.sendMessage(
                Text.literal("                                                                ")
                    .formatted(Formatting.WHITE),
                false
            );
        }
        
        // Play jumpscare sound
        world.playSound(
            null,
            player.getBlockPos(),
            ModSounds.JUMPSCARE,
            SoundCategory.MASTER,
            2.0f,
            1.0f
        );
        
        // Also play scream
        world.getServer().execute(() -> {
            try {
                Thread.sleep(200); // Slight delay
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            world.playSound(
                null,
                player.getBlockPos(),
                ModSounds.SCREAM,
                SoundCategory.MASTER,
                1.5f,
                1.2f
            );
        });
        
        // Nausea for added effect
        player.addStatusEffect(new StatusEffectInstance(
            StatusEffects.NAUSEA,
            100, // 5 seconds
            0
        ));
    }
}
