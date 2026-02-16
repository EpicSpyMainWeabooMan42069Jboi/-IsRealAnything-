package com.epicspymain.isrealanything.events;

import com.epicspymain.isrealanything.sound.ModSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

/**
 * EVENT 20: HaveYouEverBeenLonely - Removes ALL living creatures
 * Removes EVERY living creature in the world except players
 * Ultimate isolation event
 */
public class HaveYouEverBeenLonelyEvent {
    
    /**
     * Trigger the loneliness event - remove all entities
     */
    public static void trigger(ServerWorld world, ServerPlayerEntity player) {
        // Send warning message
        player.sendMessage(
            Text.literal("Have you ever been lonely?")
                .formatted(Formatting.DARK_PURPLE, Formatting.ITALIC),
            false
        );
        
        // Wait 3 seconds
        world.getServer().execute(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Remove all living entities
            int removedCount = removeAllLivingEntities(world);
            
            // Play static noise
            world.playSound(
                null,
                player.getBlockPos(),
                ModSounds.STATIC_NOISE,
                SoundCategory.AMBIENT,
                1.0f,
                0.5f
            );
            
            // Send confirmation message
            world.getServer().execute(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                player.sendMessage(
                    Text.literal("Now you are.")
                        .formatted(Formatting.DARK_RED, Formatting.BOLD),
                    false
                );
                
                player.sendMessage(
                    Text.literal("Entities removed: " + removedCount)
                        .formatted(Formatting.DARK_GRAY),
                    false
                );
                
                // Final sound
                world.playSound(
                    null,
                    player.getBlockPos(),
                    ModSounds.FOREVER,
                    SoundCategory.AMBIENT,
                    0.7f,
                    0.8f
                );
            });
        });
    }
    
    /**
     * Remove all living entities in the world except players
     */
    private static int removeAllLivingEntities(ServerWorld world) {
        int count = 0;
        
        // Get all loaded entities
        List<LivingEntity> entities = world.getEntitiesByClass(
            LivingEntity.class,
            world.getBorder().asBox(),
            entity -> !(entity instanceof ServerPlayerEntity)
        );
        
        // Remove each entity
        for (LivingEntity entity : entities) {
            entity.discard();
            count++;
        }
        
        return count;
    }
    
    /**
     * Check if world is "lonely" (no living entities except players)
     */
    public static boolean isWorldLonely(ServerWorld world) {
        List<LivingEntity> entities = world.getEntitiesByClass(
            LivingEntity.class,
            world.getBorder().asBox(),
            entity -> !(entity instanceof ServerPlayerEntity)
        );
        
        return entities.isEmpty();
    }
}
