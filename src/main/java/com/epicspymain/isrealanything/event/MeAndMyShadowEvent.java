package com.epicspymain.isrealanything.event;

import com.epicspymain.isrealanything.entity.ModEntities;
import com.epicspymain.isrealanything.entity.custom.TheOtherMEEntity;
import com.epicspymain.isrealanything.sound.ModSounds;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

import java.util.*;

/**
 * EVENT 23: MeAndMyShadow - Player shadow clone
 * Spawns hostile fake copy of player that chases them
 * Deals 0.5 hearts damage
 * Creates duplicate if killed (up to 3 total)
 * Lasts 5 minutes total
 */
public class MeAndMyShadowEvent {
    
    private static final int MAX_DURATION = 6000; // 5 minutes
    private static final int MAX_DUPLICATES = 3;
    private static final float DAMAGE = 1.0f; // 0.5 hearts
    
    // Track active shadow events
    private static final Map<UUID, ShadowEventData> activeShadows = new HashMap<>();
    
    /**
     * Trigger shadow clone event
     */
    public static void trigger(ServerWorld world, ServerPlayerEntity player) {
        // Don't stack shadow events
        if (activeShadows.containsKey(player.getUuid())) {
            return;
        }
        
        // Initial message
        player.sendMessage(
            Text.literal("You see a figure that looks... familiar.")
                .formatted(Formatting.DARK_GRAY, Formatting.ITALIC),
            false
        );
        
        // Spawn first shadow
        UUID shadowId = spawnShadow(world, player);
        
        if (shadowId != null) {
            activeShadows.put(player.getUuid(), new ShadowEventData(
                player.getUuid(),
                MAX_DURATION,
                1,
                new ArrayList<>(Collections.singletonList(shadowId))
            ));
            
            // Play eerie sound
            world.playSound(
                null,
                player.getBlockPos(),
                ModSounds.EVENT_GLITCH,
                SoundCategory.HOSTILE,
                0.8f,
                0.7f
            );
        }
    }
    
    /**
     * Spawn a shadow clone
     */
    private static UUID spawnShadow(ServerWorld world, ServerPlayerEntity player) {
        // Spawn behind player
        Vec3d behindPlayer = player.getPos().subtract(
            player.getRotationVec(1.0f).multiply(8.0)
        );
        
        TheOtherMEEntity shadow = ModEntities.THE_OTHER_ME.create(world);
        if (shadow == null) {
            return null;
        }
        
        shadow.refreshPositionAndAngles(
            behindPlayer.x,
            behindPlayer.y,
            behindPlayer.z,
            player.getYaw() + 180,
            0
        );
        
        // Set custom name to look like player
        shadow.setCustomName(Text.literal(player.getName().getString() + "?")
            .formatted(Formatting.DARK_GRAY, Formatting.OBFUSCATED));
        shadow.setCustomNameVisible(true);
        
        // Reduce damage to 0.5 hearts
        shadow.setHealth(shadow.getMaxHealth() * 0.5f);
        
        // Target player
        shadow.setTarget(player);
        
        world.spawnEntity(shadow);
        
        return shadow.getUuid();
    }
    
    /**
     * Tick active shadow events
     */
    public static void tick(ServerWorld world) {
        Iterator<Map.Entry<UUID, ShadowEventData>> iterator = activeShadows.entrySet().iterator();
        
        while (iterator.hasNext()) {
            Map.Entry<UUID, ShadowEventData> entry = iterator.next();
            UUID playerUuid = entry.getKey();
            ShadowEventData data = entry.getValue();
            
            ServerPlayerEntity player = world.getServer().getPlayerManager().getPlayer(playerUuid);
            
            if (player == null) {
                iterator.remove();
                continue;
            }
            
            data.ticksRemaining--;
            
            // Check for dead shadows
            Iterator<UUID> shadowIterator = data.shadowIds.iterator();
            while (shadowIterator.hasNext()) {
                UUID shadowId = shadowIterator.next();
                TheOtherMEEntity shadow = (TheOtherMEEntity) world.getEntity(shadowId);
                
                if (shadow == null || !shadow.isAlive()) {
                    shadowIterator.remove();
                    
                    // Spawn duplicate if under limit and time remaining
                    if (data.duplicateCount < MAX_DUPLICATES && data.ticksRemaining > 600) {
                        UUID newShadowId = spawnShadow(world, player);
                        if (newShadowId != null) {
                            data.shadowIds.add(newShadowId);
                            data.duplicateCount++;
                            
                            player.sendMessage(
                                Text.literal("It splits...")
                                    .formatted(Formatting.DARK_RED),
                                true
                            );
                        }
                    }
                }
            }
            
            // End event if time expired or no shadows left
            if (data.ticksRemaining <= 0 || data.shadowIds.isEmpty()) {
                // Clean up remaining shadows
                for (UUID shadowId : data.shadowIds) {
                    TheOtherMEEntity shadow = (TheOtherMEEntity) world.getEntity(shadowId);
                    if (shadow != null) {
                        shadow.discard();
                    }
                }
                
                player.sendMessage(
                    Text.literal("The shadows fade away...")
                        .formatted(Formatting.GRAY, Formatting.ITALIC),
                    false
                );
                
                iterator.remove();
            }
        }
    }
    
    /**
     * Data class for shadow event tracking
     */
    private static class ShadowEventData {
        final UUID playerUuid;
        int ticksRemaining;
        int duplicateCount;
        final List<UUID> shadowIds;
        
        ShadowEventData(UUID playerUuid, int ticksRemaining, int duplicateCount, List<UUID> shadowIds) {
            this.playerUuid = playerUuid;
            this.ticksRemaining = ticksRemaining;
            this.duplicateCount = duplicateCount;
            this.shadowIds = shadowIds;
        }
    }
}
