package com.epicspymain.isrealanything.event;

import com.epicspymain.isrealanything.entity.ModEntities;
import com.epicspymain.isrealanything.entity.custom.TheMEEntity;
import com.epicspymain.isrealanything.sound.ModSounds;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/**
 * EVENT 11: UndergroundMining - Underground stalking event
 * When player is mining underground (Y < 50):
 * - Ambient horror sounds
 * - Entity spawns in darkness behind player
 * - Breathing sounds get louder as it approaches
 * - Lights flicker (if torches nearby)
 */
public class UndergroundMiningEvent {
    
    private static final int UNDERGROUND_Y_LEVEL = 50;
    private static final int SPAWN_DISTANCE = 20;
    
    /**
     * Execute underground mining event
     */
    public static void execute(ServerWorld world, ServerPlayerEntity player) {
        // Check if player is underground
        if (player.getBlockPos().getY() >= UNDERGROUND_Y_LEVEL) {
            return;
        }
        
        // Phase 1: Ambient sounds
        playAmbientMiningSounds(world, player);
        
        // Wait 5 seconds
        world.getServer().execute(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Phase 2: Spawn entity in darkness
            spawnUndergroundStalker(world, player);
        });
    }
    
    /**
     * Play ambient mining horror sounds
     */
    private static void playAmbientMiningSounds(ServerWorld world, ServerPlayerEntity player) {
        BlockPos pos = player.getBlockPos();
        
        // Distant pickaxe sounds
        world.playSound(
            null,
            pos.add(
                world.random.nextInt(20) - 10,
                world.random.nextInt(10) - 5,
                world.random.nextInt(20) - 10
            ),
            net.minecraft.sound.SoundEvents.BLOCK_STONE_BREAK,
            SoundCategory.BLOCKS,
            0.3f,
            0.7f
        );
        
        // Wait 2 seconds
        world.getServer().execute(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Breathing sound
            world.playSound(
                null,
                pos,
                ModSounds.BREATHING,
                SoundCategory.HOSTILE,
                0.4f,
                0.8f
            );
        });
    }
    
    /**
     * Spawn underground stalker entity
     */
    private static void spawnUndergroundStalker(ServerWorld world, ServerPlayerEntity player) {
        // Find dark location behind player
        Vec3d behindPlayer = player.getPos().subtract(
            player.getRotationVec(1.0f).multiply(SPAWN_DISTANCE)
        );
        
        BlockPos spawnPos = new BlockPos(
            (int)behindPlayer.x,
            (int)behindPlayer.y,
            (int)behindPlayer.z
        );
        
        // Ensure location is dark
        if (world.getLightLevel(spawnPos) > 3) {
            // Find nearest dark spot
            spawnPos = findNearbyDarkSpot(world, spawnPos);
            if (spawnPos == null) {
                return; // No dark spot found
            }
        }
        
        // Spawn TheME
        TheMEEntity entity = ModEntities.THEME_ENTITY.create(world);
        if (entity != null) {
            entity.refreshPositionAndAngles(
                spawnPos.getX() + 0.5,
                spawnPos.getY(),
                spawnPos.getZ() + 0.5,
                0, 0
            );
            
            entity.setTarget(player);
            world.spawnEntity(entity);
            
            // Play breathing sound from entity location
            world.playSound(
                null,
                spawnPos,
                ModSounds.BREATHING,
                SoundCategory.HOSTILE,
                0.6f,
                0.9f
            );
            
            // Send subtle message
            player.sendMessage(
                Text.literal("You feel a presence in the darkness...")
                    .formatted(Formatting.DARK_GRAY),
                true // Action bar
            );
            
            // Escalating breathing sounds
            for (int i = 1; i <= 3; i++) {
                final int intensity = i;
                world.getServer().execute(() -> {
                    try {
                        Thread.sleep(2000L * intensity);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    
                    if (entity.isAlive()) {
                        world.playSound(
                            null,
                            entity.getBlockPos(),
                            ModSounds.BREATHING,
                            SoundCategory.HOSTILE,
                            0.4f + (intensity * 0.2f), // Gets louder
                            0.9f - (intensity * 0.1f) // Gets lower pitch
                        );
                    }
                });
            }
        }
    }
    
    /**
     * Find nearby dark spot for spawning
     */
    private static BlockPos findNearbyDarkSpot(ServerWorld world, BlockPos center) {
        for (int radius = 5; radius <= 20; radius += 5) {
            for (int attempt = 0; attempt < 20; attempt++) {
                double angle = world.random.nextDouble() * Math.PI * 2;
                
                int x = center.getX() + (int)(Math.cos(angle) * radius);
                int y = center.getY() + world.random.nextInt(6) - 3;
                int z = center.getZ() + (int)(Math.sin(angle) * radius);
                
                BlockPos testPos = new BlockPos(x, y, z);
                
                // Check if dark and has space
                if (world.getLightLevel(testPos) <= 3 &&
                    world.getBlockState(testPos).isAir() &&
                    world.getBlockState(testPos.up()).isAir()) {
                    return testPos;
                }
            }
        }
        
        return null;
    }
}
