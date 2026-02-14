package com.epicspymain.isrealanything.event;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;

import java.util.*;

/**
 * Tracks player progression and activities to inform event triggers
 * Monitors various gameplay metrics to create contextual horror experiences
 */
public class ProgressionTracker {
    private static final ProgressionTracker INSTANCE = new ProgressionTracker();
    
    private final Map<UUID, PlayerProgress> playerProgress = new HashMap<>();
    
    private ProgressionTracker() {}
    
    public static ProgressionTracker getInstance() {
        return INSTANCE;
    }
    
    /**
     * Get or create progression data for player
     */
    public PlayerProgress getProgress(ServerPlayerEntity player) {
        return playerProgress.computeIfAbsent(player.getUuid(), uuid -> new PlayerProgress());
    }
    
    /**
     * Update progression tracking for player
     */
    public void updateProgress(ServerPlayerEntity player) {
        PlayerProgress progress = getProgress(player);
        progress.updateFromPlayer(player);
    }
    
    /**
     * Check if player has reached a specific progression level
     */
    public boolean hasReachedProgression(ServerPlayerEntity player, ProgressionLevel level) {
        PlayerProgress progress = getProgress(player);
        return progress.getCurrentLevel().ordinal() >= level.ordinal();
    }
    
    /**
     * Reset player progression (for death/respawn)
     */
    public void resetProgress(UUID playerUuid) {
        playerProgress.remove(playerUuid);
    }
    
    /**
     * Progression levels based on player activities
     */
    public enum ProgressionLevel {
        EARLY_GAME,      // Just started, basic survival
        ESTABLISHED,     // Has base, basic resources
        ADVANCED,        // Diamonds, nether access
        LATE_GAME,       // End game gear, exploring
        END_GAME         // Beaten dragon, full gear
    }
    
    /**
     * Player progression data
     */
    public static class PlayerProgress {
        // Time tracking
        private long ticksPlayed = 0;
        private long lastUpdateTick = 0;
        
        // Location tracking
        private final List<BlockPos> visitedLocations = new ArrayList<>();
        private BlockPos lastKnownPos = BlockPos.ORIGIN;
        private int timesInNether = 0;
        private int timesInEnd = 0;
        
        // Activity tracking
        private int blocksMined = 0;
        private int blocksPlaced = 0;
        private int itemsCrafted = 0;
        private int mobsKilled = 0;
        private int deaths = 0;
        private int jumps = 0;
        private int damageDealt = 0;
        private int damageTaken = 0;
        
        // Resource tracking
        private boolean hasDiamonds = false;
        private boolean hasNetherAccess = false;
        private boolean hasEndAccess = false;
        private boolean hasElytra = false;
        private boolean hasBeacons = false;
        
        // Behavioral tracking
        private long timeSpentUnderground = 0;
        private long timeSpentAtNight = 0;
        private int timesSlept = 0;
        
        /**
         * Update progression from player stats
         */
        public void updateFromPlayer(ServerPlayerEntity player) {
            ticksPlayed++;
            
            // Update position tracking
            BlockPos currentPos = player.getBlockPos();
            if (!currentPos.equals(lastKnownPos)) {
                if (visitedLocations.size() == 0 || 
                    currentPos.getSquaredDistance(visitedLocations.get(visitedLocations.size() - 1)) > 256) {
                    visitedLocations.add(currentPos);
                }
                lastKnownPos = currentPos;
            }
            
            // Update dimension tracking
            String dimension = player.getWorld().getRegistryKey().getValue().toString();
            if (dimension.contains("nether")) {
                hasNetherAccess = true;
                if (ticksPlayed - lastUpdateTick > 200) { // New visit
                    timesInNether++;
                }
            } else if (dimension.contains("end")) {
                hasEndAccess = true;
                if (ticksPlayed - lastUpdateTick > 200) {
                    timesInEnd++;
                }
            }
            
            // Update underground time
            if (currentPos.getY() < 50) {
                timeSpentUnderground++;
            }
            
            // Update night time
            if (player.getWorld().isNight()) {
                timeSpentAtNight++;
            }
            
            // Check for progression items
            if (player.getInventory().contains(net.minecraft.item.Items.DIAMOND.getDefaultStack())) {
                hasDiamonds = true;
            }
            if (player.getInventory().contains(net.minecraft.item.Items.ELYTRA.getDefaultStack())) {
                hasElytra = true;
            }
            
            // Update stats from player
            blocksMined = player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.MINED)) / 10;
            itemsCrafted = player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.CRAFTED)) / 10;
            mobsKilled = player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.KILLED)) / 10;
            deaths = player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.DEATHS));
            jumps = player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.JUMP));
            
            lastUpdateTick = ticksPlayed;
        }
        
        /**
         * Calculate current progression level
         */
        public ProgressionLevel getCurrentLevel() {
            // End game: Has elytra, visited end, lots of progress
            if (hasElytra || hasEndAccess || itemsCrafted > 1000) {
                return ProgressionLevel.END_GAME;
            }
            
            // Late game: Has diamonds, nether access, established base
            if ((hasDiamonds && hasNetherAccess) || itemsCrafted > 500 || blocksMined > 5000) {
                return ProgressionLevel.LATE_GAME;
            }
            
            // Advanced: Either has diamonds or nether access
            if (hasDiamonds || hasNetherAccess || itemsCrafted > 200) {
                return ProgressionLevel.ADVANCED;
            }
            
            // Established: Has basic base, crafted items, mined blocks
            if (itemsCrafted > 50 || blocksMined > 500 || blocksPlaced > 200) {
                return ProgressionLevel.ESTABLISHED;
            }
            
            // Early game: Just started
            return ProgressionLevel.EARLY_GAME;
        }
        
        /**
         * Calculate player's vulnerability score (0.0 to 1.0)
         * Higher = more vulnerable = better time for scares
         */
        public float getVulnerabilityScore(ServerPlayerEntity player) {
            float score = 0.0f;
            
            // Low health = vulnerable
            if (player.getHealth() < player.getMaxHealth() * 0.3f) {
                score += 0.3f;
            } else if (player.getHealth() < player.getMaxHealth() * 0.6f) {
                score += 0.15f;
            }
            
            // Underground = vulnerable
            if (player.getBlockPos().getY() < 40) {
                score += 0.2f;
            }
            
            // Night time = vulnerable
            if (player.getWorld().isNight()) {
                score += 0.15f;
            }
            
            // Low light = vulnerable
            if (player.getWorld().getLightLevel(player.getBlockPos()) < 7) {
                score += 0.15f;
            }
            
            // Alone (no nearby players) = vulnerable
            if (player.getWorld().getPlayers().size() == 1) {
                score += 0.1f;
            }
            
            // Mining/not moving = vulnerable
            if (player.getVelocity().length() < 0.1) {
                score += 0.1f;
            }
            
            return Math.min(score, 1.0f);
        }
        
        /**
         * Check if player is in a "safe" location (well-lit base)
         */
        public boolean isInSafeLocation(ServerPlayerEntity player) {
            BlockPos pos = player.getBlockPos();
            
            // Check light level
            if (player.getWorld().getLightLevel(pos) < 10) {
                return false;
            }
            
            // Check if surrounded by player-placed blocks (base detection)
            int playerBlocksNearby = 0;
            for (int x = -3; x <= 3; x++) {
                for (int z = -3; z <= 3; z++) {
                    BlockPos checkPos = pos.add(x, 0, z);
                    if (!player.getWorld().getBlockState(checkPos).isAir()) {
                        playerBlocksNearby++;
                    }
                }
            }
            
            // If surrounded by blocks and well-lit, probably in base
            return playerBlocksNearby > 20;
        }
        
        // Getters
        public long getTicksPlayed() { return ticksPlayed; }
        public int getGameDay() { return (int)(ticksPlayed / 24000); }
        public int getBlocksMined() { return blocksMined; }
        public int getItemsCrafted() { return itemsCrafted; }
        public int getMobsKilled() { return mobsKilled; }
        public int getDeaths() { return deaths; }
        public boolean hasDiamonds() { return hasDiamonds; }
        public boolean hasNetherAccess() { return hasNetherAccess; }
        public boolean hasEndAccess() { return hasEndAccess; }
        public long getTimeSpentUnderground() { return timeSpentUnderground; }
        public long getTimeSpentAtNight() { return timeSpentAtNight; }
        public List<BlockPos> getVisitedLocations() { return visitedLocations; }
    }
}
