package com.epicspymain.isrealanything.events;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

/**
 * EVENT 27: SixtyNinthMood - Lava rain
 * Spawns falling lava blocks from sky
 * Creates fear and annoyance
 * Reference to "69" meme/mood
 */
public class SixtyNinthMoodEvent {
    
    private static final int LAVA_COUNT = 69;
    private static final int SPAWN_RADIUS = 30;
    private static final int SPAWN_HEIGHT = 60; // Above player
    
    /**
     * Trigger lava rain event
     */
    public static void trigger(ServerWorld world, ServerPlayerEntity player) {
        BlockPos playerPos = player.getBlockPos();
        
        // Warning message
        player.sendMessage(
            Text.literal("The sky opens...")
                .formatted(Formatting.DARK_RED, Formatting.BOLD),
            false
        );
        
        // Spawn falling lava blocks
        for (int i = 0; i < LAVA_COUNT; i++) {
            final int lavaIndex = i;
            
            world.getServer().execute(() -> {
                try {
                    Thread.sleep(100L * lavaIndex); // Stagger spawns
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                // Random position around player
                double offsetX = (world.random.nextDouble() - 0.5) * SPAWN_RADIUS * 2;
                double offsetZ = (world.random.nextDouble() - 0.5) * SPAWN_RADIUS * 2;
                
                BlockPos spawnPos = playerPos.add(
                    (int)offsetX,
                    SPAWN_HEIGHT,
                    (int)offsetZ
                );
                
                // Create falling lava block
                FallingBlockEntity fallingLava = FallingBlockEntity.spawnFromBlock(
                    world,
                    spawnPos,
                    Blocks.LAVA.getDefaultState()
                );
                
                if (fallingLava != null) {
                    // Make it fall slower for dramatic effect
                    fallingLava.setVelocity(0, -0.2, 0);
                }
            });
        }
        
        // Final message
        world.getServer().execute(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            player.sendMessage(
                Text.literal("It's raining fire.")
                    .formatted(Formatting.RED),
                false
            );
        });
    }
}
