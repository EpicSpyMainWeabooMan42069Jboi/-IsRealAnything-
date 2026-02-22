package com.epicspymain.isrealanything.event;

import com.epicspymain.isrealanything.entity.ModEntities;
import com.epicspymain.isrealanything.entity.custom.TheMEEntity;
import com.epicspymain.isrealanything.entity.custom.TheOtherMEEntity;
import com.epicspymain.isrealanything.sound.ModSounds;
import com.epicspymain.isrealanything.world.DimensionRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * EVENT: LIMBO_EXILE (Phase 3+)
 * Teleports player to limbo dimension for 4 minutes
 * Spawns entity 75 blocks away, watching
 * Returns player to original position after timer
 */
public class LimboExileEvent {

    private static final Map<ServerPlayerEntity, ExileData> exiledPlayers = new HashMap<>();
    private static final int EXILE_DURATION = 4800; // 4 minutes (240 seconds)
    private static final Random random = new Random();
    
    /**
     * Stores player's original location for return
     */
    private static class ExileData {
        Vec3d originalPos;
        RegistryKey<net.minecraft.world.World> originalDim;
        float yaw;
        float pitch;
        int returnTimer;
        
        ExileData(Vec3d pos, RegistryKey<net.minecraft.world.World> dim, float yaw, float pitch) {
            this.originalPos = pos;
            this.originalDim = dim;
            this.yaw = yaw;
            this.pitch = pitch;
            this.returnTimer = EXILE_DURATION;
        }
    }

    /**
     * Trigger limbo exile event
     */
    public static void trigger(ServerPlayerEntity player) {
        // Check if already exiled
        if (exiledPlayers.containsKey(player)) {
            return;
        }
        
        MinecraftServer server = player.getServer();
        if (server == null) return;
        
        // Get limbo dimension
        ServerWorld limboDimension = server.getWorld(DimensionRegistry.LIMBO_DIMENSION_KEY);
        
        if (limboDimension == null) {
            // Fallback: teleport to void in overworld
            player.sendMessage(Text.literal("§8Where am I?"), false);
            player.teleport(player.getServerWorld(), 
                player.getX(), -64, player.getZ(), 
                player.getYaw(), player.getPitch());
            return;
        }
        
        // Store original position
        ExileData exileData = new ExileData(
            player.getPos(),
            player.getWorld().getRegistryKey(),
            player.getYaw(),
            player.getPitch()
        );
        exiledPlayers.put(player, exileData);
        
        // Teleport to limbo (center at 0, 64, 0)
        double x = random.nextInt(40) - 20; // -20 to 20
        double z = random.nextInt(40) - 20;
        
        player.teleport(limboDimension, x, 64, z, player.getYaw(), player.getPitch());
        
        // Send eerie message
        player.sendMessage(Text.literal("§8Where am I?"), false);
        
        // Play sound
        limboDimension.playSound(null, x, 64, z, 
            ModSounds.ERRRRRR, SoundCategory.AMBIENT, 0.3f, 0.7f);
        
        // Spawn watching entity 75 blocks away
        spawnWatchingEntity(limboDimension, player, x, z);
    }
    
    /**
     * Spawn entity that watches player from distance
     */
    private static void spawnWatchingEntity(ServerWorld world, ServerPlayerEntity player, double playerX, double playerZ) {
        // Random angle
        double angle = random.nextDouble() * Math.PI * 2;
        double distance = 75.0;
        
        double x = playerX + Math.cos(angle) * distance;
        double z = playerZ + Math.sin(angle) * distance;
        double y = 64;
        
        // Random entity type
        Entity entity;
        if (random.nextBoolean()) {
            entity = ModEntities.THE_ME.create(world);
        } else {
            entity = ModEntities.THE_OTHER_ME.create(world);
        }
        
        if (entity != null) {
            entity.refreshPositionAndAngles(x, y, z, 0, 0);
            
            // Look at player
            double dx = playerX - x;
            double dz = playerZ - z;
            float yaw = (float)(Math.atan2(dz, dx) * 180.0 / Math.PI) - 90.0f;
            entity.setYaw(yaw);
            entity.setHeadYaw(yaw);
            
            // Make stationary
            entity.setVelocity(0, 0, 0);
            entity.setNoGravity(true);
            
            // Remove AI goals to make it just stand and stare
            if (entity instanceof TheMEEntity theME) {
                theME.goalSelector.clear(goal -> true);
                theME.targetSelector.clear(goal -> true);
            } else if (entity instanceof TheOtherMEEntity theOther) {
                theOther.goalSelector.clear(goal -> true);
                theOther.targetSelector.clear(goal -> true);
            }
            
            world.spawnEntity(entity);
        }
    }
    
    /**
     * Tick exiled players - called from server tick
     */
    public static void tickExiledPlayers(MinecraftServer server) {
        if (exiledPlayers.isEmpty()) return;
        
        // Copy to avoid concurrent modification
        Map<ServerPlayerEntity, ExileData> toReturn = new HashMap<>();
        
        for (Map.Entry<ServerPlayerEntity, ExileData> entry : exiledPlayers.entrySet()) {
            ServerPlayerEntity player = entry.getKey();
            ExileData data = entry.getValue();
            
            if (player == null || player.isRemoved()) {
                toReturn.put(player, data);
                continue;
            }
            
            data.returnTimer--;
            
            if (data.returnTimer <= 0) {
                toReturn.put(player, data);
            }
        }
        
        // Return players
        for (Map.Entry<ServerPlayerEntity, ExileData> entry : toReturn.entrySet()) {
            returnPlayer(server, entry.getKey(), entry.getValue());
            exiledPlayers.remove(entry.getKey());
        }
    }
    
    /**
     * Return player to original location
     */
    private static void returnPlayer(MinecraftServer server, ServerPlayerEntity player, ExileData data) {
        if (player == null || player.isRemoved()) return;
        
        ServerWorld originalWorld = server.getWorld(data.originalDim);
        if (originalWorld == null) {
            originalWorld = server.getOverworld();
        }
        
        // Teleport back
        player.teleport(originalWorld, 
            data.originalPos.x, 
            data.originalPos.y, 
            data.originalPos.z, 
            data.yaw, 
            data.pitch);
        
        // Send message
        player.sendMessage(Text.literal("§7You're back. For now."), false);
    }
    
    /**
     * Check if player is exiled
     */
    public static boolean isExiled(ServerPlayerEntity player) {
        return exiledPlayers.containsKey(player);
    }
    
    /**
     * Force return a player (emergency)
     */
    public static void forceReturn(MinecraftServer server, ServerPlayerEntity player) {
        ExileData data = exiledPlayers.remove(player);
        if (data != null) {
            returnPlayer(server, player, data);
        }
    }
}
