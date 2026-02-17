package com.epicspymain.isrealanything.event;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;

import java.util.*;

/**
 * EVENT 3: MyMobPals - Possessed mob behavior
 * All mobs in 70 block radius get possessed:
 * - Stop and look at player for 5 seconds
 * - Speed increases 15x
 * - Deal 1.5 hearts damage
 * - After timer expires, act normal
 */
public class MyMobPalsEvent {
    
    private static final int POSSESSION_RADIUS = 70;
    private static final int POSSESSION_DURATION = 100; // 5 seconds (20 ticks/sec)
    private static final float SPEED_MULTIPLIER = 15.0f;
    private static final float DAMAGE_MULTIPLIER = 1.5f; // +1.5 hearts
    
    // Track possessed mobs and their original stats
    private static final Map<UUID, PossessedMobData> possessedMobs = new HashMap<>();
    
    /**
     * Trigger mob possession event
     */
    public static void trigger(ServerWorld world, ServerPlayerEntity player) {
        Box searchBox = Box.of(
            player.getPos(),
            POSSESSION_RADIUS * 2,
            POSSESSION_RADIUS * 2,
            POSSESSION_RADIUS * 2
        );
        
        List<MobEntity> nearbyMobs = world.getEntitiesByClass(
            MobEntity.class,
            searchBox,
            mob -> mob.isAlive() && !isPossessed(mob)
        );
        
        for (MobEntity mob : nearbyMobs) {
            possessMob(mob, player);
        }
    }
    
    /**
     * Possess a single mob
     */
    private static void possessMob(MobEntity mob, ServerPlayerEntity player) {
        // Store original stats
        double originalSpeed = mob.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        double originalDamage = mob.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        
        PossessedMobData data = new PossessedMobData(
            originalSpeed,
            originalDamage,
            POSSESSION_DURATION,
            player.getUuid()
        );
        
        possessedMobs.put(mob.getUuid(), data);
        
        // Apply possession effects
        mob.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
            .setBaseValue(originalSpeed * SPEED_MULTIPLIER);
        mob.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)
            .setBaseValue(originalDamage + DAMAGE_MULTIPLIER);
        
        // Make mob look at player
        mob.getLookControl().lookAt(player, 180f, 180f);
        mob.setTarget(player);
    }
    
    /**
     * Tick all possessed mobs (call from server tick)
     */
    public static void tick(ServerWorld world) {
        Iterator<Map.Entry<UUID, PossessedMobData>> iterator = possessedMobs.entrySet().iterator();
        
        while (iterator.hasNext()) {
            Map.Entry<UUID, PossessedMobData> entry = iterator.next();
            UUID mobUuid = entry.getKey();
            PossessedMobData data = entry.getValue();
            
            // Decrement timer
            data.ticksRemaining--;
            
            // Check if possession ended
            if (data.ticksRemaining <= 0) {
                // Find mob and restore stats
                LivingEntity mob = (LivingEntity) world.getEntity(mobUuid);
                if (mob != null && mob.isAlive()) {
                    restoreMob((MobEntity) mob, data);
                }
                iterator.remove();
            }
        }
    }
    
    /**
     * Restore mob to original state
     */
    private static void restoreMob(MobEntity mob, PossessedMobData data) {
        mob.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
            .setBaseValue(data.originalSpeed);
        mob.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)
            .setBaseValue(data.originalDamage);
        
        // Clear target
        mob.setTarget(null);
    }
    
    /**
     * Check if mob is currently possessed
     */
    public static boolean isPossessed(MobEntity mob) {
        return possessedMobs.containsKey(mob.getUuid());
    }
    
    /**
     * Get possessed mob data
     */
    public static PossessedMobData getPossessedData(UUID mobUuid) {
        return possessedMobs.get(mobUuid);
    }
    
    /**
     * Data class for possessed mob state
     */
    public static class PossessedMobData {
        public final double originalSpeed;
        public final double originalDamage;
        public int ticksRemaining;
        public final UUID targetPlayerUuid;
        
        public PossessedMobData(double originalSpeed, double originalDamage, int ticksRemaining, UUID targetPlayerUuid) {
            this.originalSpeed = originalSpeed;
            this.originalDamage = originalDamage;
            this.ticksRemaining = ticksRemaining;
            this.targetPlayerUuid = targetPlayerUuid;
        }
    }
}
