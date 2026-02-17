package com.epicspymain.isrealanything.event;

import com.epicspymain.isrealanything.sound.ModSounds;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

/**
 * EVENT 19: ForcedWakeup - Destroys player's bed
 * Finds and destroys player's bed
 * Plays horror sound
 * Sends cryptic message
 */
public class ForcedWakeupEvent {
    
    /**
     * Trigger forced wakeup by destroying bed
     */
    public static void trigger(ServerWorld world, ServerPlayerEntity player) {
        // Find player's bed
        BlockPos bedPos = findPlayerBed(world, player);
        
        if (bedPos == null) {
            // No bed found, search wider area
            bedPos = searchForBed(world, player.getBlockPos(), 50);
        }
        
        if (bedPos != null) {
            // Destroy the bed
            world.breakBlock(bedPos, false); // No drops
            
            // Play horror sound
            world.playSound(
                null,
                bedPos,
                ModSounds.LAUGH_DISTANT,
                SoundCategory.BLOCKS,
                1.0f,
                0.7f
            );
            
            // Send message
            player.sendMessage(
                Text.literal("You won't be sleeping tonight.")
                    .formatted(Formatting.DARK_RED, Formatting.ITALIC),
                false
            );
            
            // Additional creepy sound
            world.getServer().execute(() -> {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                world.playSound(
                    null,
                    player.getBlockPos(),
                    ModSounds.WHISPER_2,
                    SoundCategory.AMBIENT,
                    0.6f,
                    0.8f
                );
            });
        } else {
            // No bed found, just send message
            player.sendMessage(
                Text.literal("Sleep? You think you deserve sleep?")
                    .formatted(Formatting.DARK_GRAY, Formatting.ITALIC),
                false
            );
        }
    }
    
    /**
     * Find player's spawn bed
     */
    private static BlockPos findPlayerBed(ServerWorld world, ServerPlayerEntity player) {
        // Try to get player's respawn position (bed location)
        BlockPos spawnPos = player.getSpawnPointPosition();
        
        if (spawnPos != null) {
            // Check if there's actually a bed there
            Block block = world.getBlockState(spawnPos).getBlock();
            if (block instanceof BedBlock) {
                return spawnPos;
            }
        }
        
        return null;
    }
    
    /**
     * Search for any bed near player
     */
    private static BlockPos searchForBed(ServerWorld world, BlockPos center, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int y = -10; y <= 10; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = center.add(x, y, z);
                    Block block = world.getBlockState(pos).getBlock();
                    
                    if (block instanceof BedBlock) {
                        return pos;
                    }
                }
            }
        }
        
        return null;
    }
}
