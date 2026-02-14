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

import java.util.ArrayList;
import java.util.List;

/**
 * THE OVERLOOK - The climactic horror event
 * 
 * A multi-phase event where the player is surrounded by entities that only move
 * when not being looked at (Weeping Angels style). Culminates in a final confrontation.
 * 
 * PHASES:
 * 1. WARNING - Ominous messages, distant sounds (30 seconds)
 * 2. ARRIVAL - Entities spawn at distance, frozen when looked at (60 seconds)
 * 3. ENCIRCLEMENT - More entities spawn, closing in (60 seconds)
 * 4. CONVERGENCE - Entities surround player, moving when not observed (45 seconds)
 * 5. CONFRONTATION - Final entity appears, others vanish, boss fight
 * 6. RESOLUTION - Event ends, rewards or consequences
 */
public class TheOverlookEvent {
    private static EventPhase currentPhase = EventPhase.NONE;
    private static int phaseTicks = 0;
    private static ServerPlayerEntity targetPlayer = null;
    private static final List<Integer> spawnedEntityIds = new ArrayList<>();
    private static BlockPos eventCenter = null;
    
    public enum EventPhase {
        NONE(0),
        WARNING(600),        // 30 seconds
        ARRIVAL(1200),       // 60 seconds
        ENCIRCLEMENT(1200),  // 60 seconds
        CONVERGENCE(900),    // 45 seconds
        CONFRONTATION(0),    // Until entity defeated
        RESOLUTION(200);     // 10 seconds
        
        private final int duration;
        
        EventPhase(int duration) {
            this.duration = duration;
        }
        
        public int getDuration() {
            return duration;
        }
    }
    
    /**
     * Start The Overlook event
     */
    public static void start(ServerWorld world, ServerPlayerEntity player) {
        if (currentPhase != EventPhase.NONE) {
            return; // Event already running
        }
        
        currentPhase = EventPhase.WARNING;
        phaseTicks = 0;
        targetPlayer = player;
        eventCenter = player.getBlockPos();
        spawnedEntityIds.clear();
        
        player.sendMessage(Text.literal("THE OVERLOOK BEGINS").formatted(Formatting.DARK_RED, Formatting.BOLD), false);
    }
    
    /**
     * Tick the event - call from EventManager or server tick
     */
    public static void tick(ServerWorld world) {
        if (currentPhase == EventPhase.NONE || targetPlayer == null) {
            return;
        }
        
        phaseTicks++;
        
        switch (currentPhase) {
            case WARNING -> tickWarningPhase(world);
            case ARRIVAL -> tickArrivalPhase(world);
            case ENCIRCLEMENT -> tickEncirclementPhase(world);
            case CONVERGENCE -> tickConvergencePhase(world);
            case CONFRONTATION -> tickConfrontationPhase(world);
            case RESOLUTION -> tickResolutionPhase(world);
        }
        
        // Check for phase transition
        if (currentPhase.getDuration() > 0 && phaseTicks >= currentPhase.getDuration()) {
            advancePhase(world);
        }
    }
    
    /**
     * PHASE 1: WARNING - Ominous buildup
     */
    private static void tickWarningPhase(ServerWorld world) {
        if (phaseTicks == 1) {
            targetPlayer.sendMessage(Text.literal("You feel a presence...").formatted(Formatting.GRAY), false);
            world.playSound(null, targetPlayer.getBlockPos(), ModSounds.STATIC_NOISE, 
                SoundCategory.AMBIENT, 0.3f, 0.7f);
        }
        
        if (phaseTicks == 200) {
            targetPlayer.sendMessage(Text.literal("Something is coming...").formatted(Formatting.DARK_GRAY), false);
            world.playSound(null, targetPlayer.getBlockPos(), ModSounds.WHISPER_1, 
                SoundCategory.AMBIENT, 0.5f, 0.8f);
        }
        
        if (phaseTicks == 400) {
            targetPlayer.sendMessage(Text.literal("They are here.").formatted(Formatting.DARK_RED), false);
            world.playSound(null, targetPlayer.getBlockPos(), ModSounds.HEARTBEAT, 
                SoundCategory.AMBIENT, 0.7f, 1.0f);
        }
        
        // Periodic heartbeat
        if (phaseTicks % 40 == 0) {
            world.playSound(null, targetPlayer.getBlockPos(), ModSounds.HEARTBEAT, 
                SoundCategory.AMBIENT, 0.4f, 1.0f);
        }
    }
    
    /**
     * PHASE 2: ARRIVAL - Entities spawn at distance
     */
    private static void tickArrivalPhase(ServerWorld world) {
        if (phaseTicks == 1) {
            targetPlayer.sendMessage(Text.literal("DON'T LOOK AWAY").formatted(Formatting.RED, Formatting.BOLD), true);
            spawnOverlookEntities(world, 3, 48.0f, 64.0f);
        }
        
        // Spawn additional entities periodically
        if (phaseTicks % 300 == 0) {
            spawnOverlookEntities(world, 1, 40.0f, 56.0f);
            world.playSound(null, targetPlayer.getBlockPos(), ModSounds.STATIC_NOISE, 
                SoundCategory.AMBIENT, 0.5f, 0.8f);
        }
        
        // Warning messages
        if (phaseTicks == 400) {
            targetPlayer.sendMessage(Text.literal("They only move when you're not watching...").formatted(Formatting.DARK_GRAY), false);
        }
    }
    
    /**
     * PHASE 3: ENCIRCLEMENT - More entities, closer
     */
    private static void tickEncirclementPhase(ServerWorld world) {
        if (phaseTicks == 1) {
            targetPlayer.sendMessage(Text.literal("THEY'RE GETTING CLOSER").formatted(Formatting.RED, Formatting.BOLD), true);
            spawnOverlookEntities(world, 4, 32.0f, 48.0f);
        }
        
        if (phaseTicks % 200 == 0) {
            spawnOverlookEntities(world, 2, 24.0f, 40.0f);
            world.playSound(null, targetPlayer.getBlockPos(), ModSounds.BREATHING, 
                SoundCategory.HOSTILE, 0.6f, 0.9f);
        }
        
        // Nausea effect for tension
        if (phaseTicks % 100 == 0) {
            targetPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 60, 0));
        }
    }
    
    /**
     * PHASE 4: CONVERGENCE - Entities very close, intense
     */
    private static void tickConvergencePhase(ServerWorld world) {
        if (phaseTicks == 1) {
            targetPlayer.sendMessage(Text.literal("YOU CAN'T ESCAPE").formatted(Formatting.DARK_RED, Formatting.BOLD), true);
            spawnOverlookEntities(world, 6, 16.0f, 32.0f);
        }
        
        if (phaseTicks % 100 == 0) {
            spawnOverlookEntities(world, 1, 12.0f, 20.0f);
            world.playSound(null, targetPlayer.getBlockPos(), ModSounds.ERRRRRR, 
                SoundCategory.HOSTILE, 0.7f, 0.8f);
        }
        
        // Intense effects
        if (phaseTicks % 60 == 0) {
            targetPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 40, 0));
            world.playSound(null, targetPlayer.getBlockPos(), ModSounds.STATIC_NOISE, 
                SoundCategory.AMBIENT, 0.8f, 0.7f);
        }
    }
    
    /**
     * PHASE 5: CONFRONTATION - Final entity, others vanish
     */
    private static void tickConfrontationPhase(ServerWorld world) {
        if (phaseTicks == 1) {
            // Remove all other entities
            despawnAllOverlookEntities(world);
            
            // Brief darkness
            targetPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 60, 1));
            world.playSound(null, targetPlayer.getBlockPos(), ModSounds.SCREAM, 
                SoundCategory.HOSTILE, 1.2f, 0.8f);
            
            // Spawn final boss entity after darkness
            world.getServer().execute(() -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                spawnFinalEntity(world);
                targetPlayer.sendMessage(Text.literal("FACE ME").formatted(Formatting.DARK_RED, Formatting.BOLD), true);
            });
        }
        
        // Check if final entity is defeated
        if (phaseTicks > 100 && !hasLivingOverlookEntities(world)) {
            advancePhase(world); // Move to resolution
        }
    }
    
    /**
     * PHASE 6: RESOLUTION - Event ends
     */
    private static void tickResolutionPhase(ServerWorld world) {
        if (phaseTicks == 1) {
            targetPlayer.sendMessage(Text.literal("It's over... for now.").formatted(Formatting.GRAY), false);
            world.playSound(null, targetPlayer.getBlockPos(), ModSounds.FOREVER, 
                SoundCategory.AMBIENT, 0.6f, 1.0f);
            
            // Reward player
            targetPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 1));
            targetPlayer.sendMessage(Text.literal("You survived The Overlook.").formatted(Formatting.GOLD), false);
        }
        
        if (phaseTicks >= 200) {
            endEvent(world, true);
        }
    }
    
    /**
     * Spawn entities in a circle around player
     */
    private static void spawnOverlookEntities(ServerWorld world, int count, float minDist, float maxDist) {
        for (int i = 0; i < count; i++) {
            double angle = (2 * Math.PI / count) * i + world.random.nextDouble() * 0.5;
            float distance = minDist + world.random.nextFloat() * (maxDist - minDist);
            
            double x = eventCenter.getX() + Math.cos(angle) * distance;
            double z = eventCenter.getZ() + Math.sin(angle) * distance;
            double y = world.getTopY(net.minecraft.world.Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (int)x, (int)z);
            
            // Alternate between TheME and TheOtherME
            if (world.random.nextBoolean()) {
                TheMEEntity entity = ModEntities.THE_ME.create(world);
                if (entity != null) {
                    entity.refreshPositionAndAngles(x, y, z, 0, 0);
                    world.spawnEntity(entity);
                    spawnedEntityIds.add(entity.getId());
                }
            } else {
                TheOtherMEEntity entity = ModEntities.THE_OTHER_ME.create(world);
                if (entity != null) {
                    entity.refreshPositionAndAngles(x, y, z, 0, 0);
                    world.spawnEntity(entity);
                    spawnedEntityIds.add(entity.getId());
                }
            }
        }
    }
    
    /**
     * Spawn the final boss entity
     */
    private static void spawnFinalEntity(ServerWorld world) {
        Vec3d spawnPos = targetPlayer.getPos().add(
            targetPlayer.getRotationVec(1.0f).multiply(8.0)
        );
        
        TheOtherMEEntity boss = ModEntities.THE_OTHER_ME.create(world);
        if (boss != null) {
            boss.refreshPositionAndAngles(spawnPos.x, spawnPos.y, spawnPos.z, 
                targetPlayer.getYaw() + 180, 0);
            
            // Boss is stronger
            boss.setHealth(boss.getMaxHealth() * 1.5f);
            
            world.spawnEntity(boss);
            spawnedEntityIds.add(boss.getId());
            
            world.playSound(null, targetPlayer.getBlockPos(), ModSounds.TED_LEWIS_FUCK_YOU, 
                SoundCategory.HOSTILE, 1.0f, 1.0f);
        }
    }
    
    /**
     * Remove all overlook entities
     */
    private static void despawnAllOverlookEntities(ServerWorld world) {
        for (Integer entityId : spawnedEntityIds) {
            var entity = world.getEntityById(entityId);
            if (entity != null) {
                entity.discard();
                world.playSound(null, entity.getBlockPos(), ModSounds.EVENT_GLITCH, 
                    SoundCategory.HOSTILE, 0.5f, 0.8f);
            }
        }
        spawnedEntityIds.clear();
    }
    
    /**
     * Check if any overlook entities are still alive
     */
    private static boolean hasLivingOverlookEntities(ServerWorld world) {
        for (Integer entityId : new ArrayList<>(spawnedEntityIds)) {
            var entity = world.getEntityById(entityId);
            if (entity == null || !entity.isAlive()) {
                spawnedEntityIds.remove(entityId);
            }
        }
        return !spawnedEntityIds.isEmpty();
    }
    
    /**
     * Advance to next phase
     */
    private static void advancePhase(ServerWorld world) {
        phaseTicks = 0;
        
        switch (currentPhase) {
            case WARNING -> currentPhase = EventPhase.ARRIVAL;
            case ARRIVAL -> currentPhase = EventPhase.ENCIRCLEMENT;
            case ENCIRCLEMENT -> currentPhase = EventPhase.CONVERGENCE;
            case CONVERGENCE -> currentPhase = EventPhase.CONFRONTATION;
            case CONFRONTATION -> currentPhase = EventPhase.RESOLUTION;
            case RESOLUTION -> endEvent(world, true);
        }
    }
    
    /**
     * End the event
     */
    private static void endEvent(ServerWorld world, boolean success) {
        despawnAllOverlookEntities(world);
        EventManager.setOverlookCompleted(success);
        currentPhase = EventPhase.NONE;
        targetPlayer = null;
        eventCenter = null;
        phaseTicks = 0;
    }
    
    /**
     * Get current phase
     */
    public static EventPhase getCurrentPhase() {
        return currentPhase;
    }
    
    /**
     * Check if event is active
     */
    public static boolean isActive() {
        return currentPhase != EventPhase.NONE;
    }
}
