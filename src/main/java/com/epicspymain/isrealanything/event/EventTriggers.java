package com.epicspymain.isrealanything.event;

import com.epicspymain.isrealanything.entity.ModEntities;
import com.epicspymain.isrealanything.entity.custom.TheMEEntity;
import com.epicspymain.isrealanything.entity.custom.TheOtherMEEntity;
import com.epicspymain.isrealanything.sound.ModSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

/**
 * Specific horror event triggers for IsRealAnything
 * Contains individual event implementations with various horror mechanics
 */
public class EventTriggers {
    private static final Random RANDOM = new Random();
    
    /**
     * JUMPSCARE EVENT: Entity suddenly appears in front of player
     */
    public static void triggerJumpscare(ServerWorld world, ServerPlayerEntity player) {
        if (EventManager.isOverlookTriggered()) return;
        
        // Get position in front of player
        Vec3d lookVec = player.getRotationVec(1.0f);
        Vec3d spawnPos = player.getPos().add(lookVec.multiply(3.0));
        
        // Spawn TheOtherME (more aggressive)
        TheOtherMEEntity entity = ModEntities.THE_OTHER_ME.create(world);
        if (entity != null) {
            entity.refreshPositionAndAngles(spawnPos.x, spawnPos.y, spawnPos.z, 
                player.getYaw() + 180, 0);
            world.spawnEntity(entity);
            
            // Sound and effects
            world.playSound(null, player.getBlockPos(), ModSounds.JUMPSCARE, 
                SoundCategory.HOSTILE, 1.5f, 1.0f);
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 40, 0));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 100, 0));
            
            player.sendMessage(Text.literal("BOO").formatted(Formatting.DARK_RED, Formatting.BOLD), true);
        }
    }
    
    /**
     * WHISPER EVENT: Creepy whisper sounds from random directions
     */
    public static void triggerWhisperEvent(ServerWorld world, ServerPlayerEntity player) {
        if (EventManager.isOverlookTriggered()) return;
        
        int whisperCount = 1 + RANDOM.nextInt(3); // 1-3 whispers
        
        for (int i = 0; i < whisperCount; i++) {
            world.getServer().execute(() -> {
                try {
                    Thread.sleep(1000 + RANDOM.nextInt(2000)); // 1-3 second delay
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                net.minecraft.sound.SoundEvent whisper = RANDOM.nextBoolean() ? 
                    ModSounds.WHISPER_1 : ModSounds.WHISPER_2;
                
                // Random position around player
                BlockPos soundPos = player.getBlockPos().add(
                    RANDOM.nextInt(20) - 10,
                    RANDOM.nextInt(6) - 3,
                    RANDOM.nextInt(20) - 10
                );
                
                world.playSound(null, soundPos, whisper, SoundCategory.AMBIENT, 
                    0.5f, 0.8f + RANDOM.nextFloat() * 0.4f);
            });
        }
    }
    
    /**
     * SHADOW EVENT: Multiple entities spawn in darkness around player
     */
    public static void triggerShadowEvent(ServerWorld world, ServerPlayerEntity player) {
        if (EventManager.isOverlookTriggered()) return;
        
        int entityCount = 2 + RANDOM.nextInt(3); // 2-4 entities
        
        for (int i = 0; i < entityCount; i++) {
            // Find dark spot within 16-32 blocks
            BlockPos spawnPos = findDarkSpot(world, player.getBlockPos(), 16, 32);
            if (spawnPos != null) {
                TheMEEntity entity = ModEntities.THE_ME.create(world);
                if (entity != null) {
                    entity.refreshPositionAndAngles(
                        spawnPos.getX() + 0.5, 
                        spawnPos.getY(), 
                        spawnPos.getZ() + 0.5, 
                        0, 0
                    );
                    world.spawnEntity(entity);
                }
            }
        }
        
        player.sendMessage(Text.literal("They gather in the shadows...").formatted(Formatting.DARK_GRAY), false);
        world.playSound(null, player.getBlockPos(), ModSounds.STATIC_NOISE, 
            SoundCategory.AMBIENT, 0.4f, 0.7f);
    }
    
    /**
     * LIGHTS OUT EVENT: Brief blindness + entity spawn
     */
    public static void triggerLightsOut(ServerWorld world, ServerPlayerEntity player) {
        if (EventManager.isOverlookTriggered()) return;
        
        // Blind player
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 60, 1));
        player.sendMessage(Text.literal("The lights go out...").formatted(Formatting.BLACK), true);
        
        // Spawn entity nearby during blindness
        world.getServer().execute(() -> {
            try {
                Thread.sleep(1500); // Spawn mid-blindness
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            Vec3d spawnPos = player.getPos().add(
                (RANDOM.nextDouble() - 0.5) * 12,
                0,
                (RANDOM.nextDouble() - 0.5) * 12
            );
            
            TheOtherMEEntity entity = ModEntities.THE_OTHER_ME.create(world);
            if (entity != null) {
                entity.refreshPositionAndAngles(spawnPos.x, spawnPos.y, spawnPos.z, 0, 0);
                world.spawnEntity(entity);
                
                world.playSound(null, player.getBlockPos(), ModSounds.BREATHING, 
                    SoundCategory.HOSTILE, 0.8f, 0.9f);
            }
        });
    }
    
    /**
     * HEARTBEAT EVENT: Accelerating heartbeat with danger approaching
     */
    public static void triggerHeartbeatEvent(ServerWorld world, ServerPlayerEntity player) {
        if (EventManager.isOverlookTriggered()) return;
        
        // Play heartbeat sounds with increasing frequency
        for (int i = 0; i < 5; i++) {
            final int beat = i;
            world.getServer().execute(() -> {
                try {
                    Thread.sleep(1000 - (beat * 150L)); // Accelerating heartbeat
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                world.playSound(null, player.getBlockPos(), ModSounds.HEARTBEAT, 
                    SoundCategory.AMBIENT, 0.6f + (beat * 0.1f), 1.0f + (beat * 0.05f));
            });
        }
        
        // Spawn entity after heartbeats
        world.getServer().execute(() -> {
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            Vec3d behindPlayer = player.getPos().subtract(player.getRotationVec(1.0f).multiply(4));
            TheMEEntity entity = ModEntities.THE_ME.create(world);
            if (entity != null) {
                entity.refreshPositionAndAngles(behindPlayer.x, behindPlayer.y, behindPlayer.z, 0, 0);
                world.spawnEntity(entity);
                world.playSound(null, player.getBlockPos(), ModSounds.ERRRRRR, 
                    SoundCategory.HOSTILE, 0.7f, 1.0f);
            }
        });
    }
    
    /**
     * GLITCH EVENT: Visual glitch effects + teleporting entity
     */
    public static void triggerGlitchEvent(ServerWorld world, ServerPlayerEntity player) {
        if (EventManager.isOverlookTriggered()) return;
        
        // Nausea for glitch effect
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 100, 0));
        world.playSound(null, player.getBlockPos(), ModSounds.EVENT_GLITCH, 
            SoundCategory.HOSTILE, 0.8f, 0.8f);
        
        // Spawn TheOtherME which can teleport
        Vec3d spawnPos = player.getPos().add(
            (RANDOM.nextDouble() - 0.5) * 16,
            0,
            (RANDOM.nextDouble() - 0.5) * 16
        );
        
        TheOtherMEEntity entity = ModEntities.THE_OTHER_ME.create(world);
        if (entity != null) {
            entity.refreshPositionAndAngles(spawnPos.x, spawnPos.y, spawnPos.z, 0, 0);
            world.spawnEntity(entity);
            
            // Entity will glitch/teleport around player (built into TheOtherME behavior)
        }
    }
    
    /**
     * PARANOIA EVENT: No visible threats, just sounds and messages
     */
    public static void triggerParanoiaEvent(ServerWorld world, ServerPlayerEntity player) {
        if (EventManager.isOverlookTriggered()) return;
        
        String[] paranoiaMessages = {
            "Did you hear that?",
            "Something moved in the corner of your eye...",
            "You feel watched.",
            "The air feels heavy.",
            "Was that a shadow?",
            "Your heart races."
        };
        
        String message = paranoiaMessages[RANDOM.nextInt(paranoiaMessages.length)];
        player.sendMessage(Text.literal(message).formatted(Formatting.GRAY, Formatting.ITALIC), false);
        
        // Random ambient sounds
        if (RANDOM.nextBoolean()) {
            world.playSound(null, player.getBlockPos(), ModSounds.FOOTSTEPS_HORROR, 
                SoundCategory.AMBIENT, 0.3f, 0.9f);
        } else {
            world.playSound(null, player.getBlockPos(), ModSounds.DOOR_CREAK, 
                SoundCategory.AMBIENT, 0.3f, 1.0f);
        }
    }
    
    /**
     * MIRROR EVENT: Entity mimics player from distance
     */
    public static void triggerMirrorEvent(ServerWorld world, ServerPlayerEntity player) {
        if (EventManager.isOverlookTriggered()) return;
        
        // Spawn entity that will follow player's movements
        Vec3d mirrorPos = player.getPos().add(
            player.getRotationVec(1.0f).multiply(-24.0) // 24 blocks behind
        );
        
        TheMEEntity entity = ModEntities.THE_ME.create(world);
        if (entity != null) {
            entity.refreshPositionAndAngles(mirrorPos.x, mirrorPos.y, mirrorPos.z, 
                player.getYaw(), player.getPitch());
            world.spawnEntity(entity);
            
            player.sendMessage(Text.literal("Something mimics your every move...").formatted(Formatting.DARK_PURPLE), false);
            world.playSound(null, player.getBlockPos(), ModSounds.STATIC_NOISE, 
                SoundCategory.AMBIENT, 0.4f, 0.8f);
        }
    }
    
    /**
     * CHASE EVENT: Entity spawns and actively pursues player
     */
    public static void triggerChaseEvent(ServerWorld world, ServerPlayerEntity player) {
        if (EventManager.isOverlookTriggered()) return;
        
        // Spawn aggressive entity far behind player
        Vec3d spawnPos = player.getPos().subtract(player.getRotationVec(1.0f).multiply(40));
        
        TheOtherMEEntity entity = ModEntities.THE_OTHER_ME.create(world);
        if (entity != null) {
            entity.refreshPositionAndAngles(spawnPos.x, spawnPos.y, spawnPos.z, 0, 0);
            world.spawnEntity(entity);
            
            player.sendMessage(Text.literal("RUN").formatted(Formatting.DARK_RED, Formatting.BOLD), true);
            world.playSound(null, player.getBlockPos(), ModSounds.SCREAM, 
                SoundCategory.HOSTILE, 1.0f, 0.9f);
            
            // Speed boost to player for chase mechanics
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 200, 1));
        }
    }
    
    /**
     * Helper: Find a dark spot within range for entity spawning
     */
    private static BlockPos findDarkSpot(ServerWorld world, BlockPos center, int minDist, int maxDist) {
        for (int attempts = 0; attempts < 20; attempts++) {
            int distance = minDist + RANDOM.nextInt(maxDist - minDist);
            double angle = RANDOM.nextDouble() * Math.PI * 2;
            
            int x = center.getX() + (int)(Math.cos(angle) * distance);
            int z = center.getZ() + (int)(Math.sin(angle) * distance);
            int y = world.getTopY(net.minecraft.world.Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z);
            
            BlockPos pos = new BlockPos(x, y, z);
            
            // Check if spot is dark (light level < 7)
            if (world.getLightLevel(pos) < 7) {
                return pos;
            }
        }
        return null; // Fallback
    }
}
