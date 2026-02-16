package com.epicspymain.isrealanything.world;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * DataTracker - Persistent server state tracking
 * Stores player-specific data and event history
 * Persists across server restarts
 */
public class DataTracker extends PersistentState {
    
    private static final String DATA_NAME = "isrealanything_data";
    
    // Player sleep stages (for ForcedWakeupEvent progression)
    private final Map<UUID, Integer> playerSleepStages = new HashMap<>();
    
    // Player warning read status (for DoNotDeleteThis event)
    private final Map<UUID, Boolean> playerReadWarning = new HashMap<>();
    
    // Player PII consent (for data collection events)
    private final Map<UUID, Boolean> playerPIIConsent = new HashMap<>();
    
    // Event history (event name -> last trigger time)
    private final Map<String, Long> eventHistory = new HashMap<>();
    
    // Player-specific event tracking
    private final Map<UUID, Map<String, Integer>> playerEventCounts = new HashMap<>();
    
    // World progression state
    private int currentPhase = 1;
    private long worldCreationTime = 0;
    private boolean finalEventTriggered = false;
    
    /**
     * Get or create server state for the world
     */
    public static DataTracker getServerState(MinecraftServer server) {
        PersistentStateManager persistentStateManager = server.getWorld(World.OVERWORLD)
            .getPersistentStateManager();
        
        DataTracker state = persistentStateManager.getOrCreate(
            DataTracker::createFromNbt,
            DataTracker::new,
            DATA_NAME
        );
        
        state.markDirty();
        return state;
    }
    
    /**
     * Create from NBT data
     */
    public static DataTracker createFromNbt(NbtCompound nbt) {
        DataTracker state = new DataTracker();
        
        // Load player sleep stages
        if (nbt.contains("SleepStages")) {
            NbtCompound sleepData = nbt.getCompound("SleepStages");
            for (String key : sleepData.getKeys()) {
                UUID uuid = UUID.fromString(key);
                state.playerSleepStages.put(uuid, sleepData.getInt(key));
            }
        }
        
        // Load player warnings
        if (nbt.contains("ReadWarnings")) {
            NbtCompound warningData = nbt.getCompound("ReadWarnings");
            for (String key : warningData.getKeys()) {
                UUID uuid = UUID.fromString(key);
                state.playerReadWarning.put(uuid, warningData.getBoolean(key));
            }
        }
        
        // Load PII consent
        if (nbt.contains("PIIConsent")) {
            NbtCompound piiData = nbt.getCompound("PIIConsent");
            for (String key : piiData.getKeys()) {
                UUID uuid = UUID.fromString(key);
                state.playerPIIConsent.put(uuid, piiData.getBoolean(key));
            }
        }
        
        // Load event history
        if (nbt.contains("EventHistory")) {
            NbtCompound historyData = nbt.getCompound("EventHistory");
            for (String eventName : historyData.getKeys()) {
                state.eventHistory.put(eventName, historyData.getLong(eventName));
            }
        }
        
        // Load world state
        state.currentPhase = nbt.getInt("CurrentPhase");
        state.worldCreationTime = nbt.getLong("WorldCreationTime");
        state.finalEventTriggered = nbt.getBoolean("FinalEventTriggered");
        
        return state;
    }
    
    /**
     * Write to NBT data
     */
    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        // Save player sleep stages
        NbtCompound sleepData = new NbtCompound();
        for (Map.Entry<UUID, Integer> entry : playerSleepStages.entrySet()) {
            sleepData.putInt(entry.getKey().toString(), entry.getValue());
        }
        nbt.put("SleepStages", sleepData);
        
        // Save player warnings
        NbtCompound warningData = new NbtCompound();
        for (Map.Entry<UUID, Boolean> entry : playerReadWarning.entrySet()) {
            warningData.putBoolean(entry.getKey().toString(), entry.getValue());
        }
        nbt.put("ReadWarnings", warningData);
        
        // Save PII consent
        NbtCompound piiData = new NbtCompound();
        for (Map.Entry<UUID, Boolean> entry : playerPIIConsent.entrySet()) {
            piiData.putBoolean(entry.getKey().toString(), entry.getValue());
        }
        nbt.put("PIIConsent", piiData);
        
        // Save event history
        NbtCompound historyData = new NbtCompound();
        for (Map.Entry<String, Long> entry : eventHistory.entrySet()) {
            historyData.putLong(entry.getKey(), entry.getValue());
        }
        nbt.put("EventHistory", historyData);
        
        // Save world state
        nbt.putInt("CurrentPhase", currentPhase);
        nbt.putLong("WorldCreationTime", worldCreationTime);
        nbt.putBoolean("FinalEventTriggered", finalEventTriggered);
        
        return nbt;
    }
    
    // === PLAYER SLEEP STAGE ===
    
    public int getPlayerSleepStage(UUID playerUuid) {
        return playerSleepStages.getOrDefault(playerUuid, 0);
    }
    
    public void setPlayerSleepStage(UUID playerUuid, int stage) {
        playerSleepStages.put(playerUuid, stage);
        markDirty();
    }
    
    public void incrementPlayerSleepStage(UUID playerUuid) {
        int current = getPlayerSleepStage(playerUuid);
        setPlayerSleepStage(playerUuid, current + 1);
    }
    
    // === PLAYER WARNING READ ===
    
    public boolean getPlayerReadWarning(UUID playerUuid) {
        return playerReadWarning.getOrDefault(playerUuid, false);
    }
    
    public void setPlayerReadWarning(UUID playerUuid, boolean read) {
        playerReadWarning.put(playerUuid, read);
        markDirty();
    }
    
    // === PLAYER PII CONSENT ===
    
    public boolean getPlayerPIIConsent(UUID playerUuid) {
        return playerPIIConsent.getOrDefault(playerUuid, false);
    }
    
    public void setPlayerPIIConsent(UUID playerUuid, boolean consent) {
        playerPIIConsent.put(playerUuid, consent);
        markDirty();
    }
    
    // === EVENT HISTORY ===
    
    public long getEventLastTrigger(String eventName) {
        return eventHistory.getOrDefault(eventName, 0L);
    }
    
    public void setEventLastTrigger(String eventName, long time) {
        eventHistory.put(eventName, time);
        markDirty();
    }
    
    public boolean hasEventTriggered(String eventName) {
        return eventHistory.containsKey(eventName);
    }
    
    // === PLAYER EVENT COUNTS ===
    
    public int getPlayerEventCount(UUID playerUuid, String eventName) {
        if (!playerEventCounts.containsKey(playerUuid)) {
            return 0;
        }
        return playerEventCounts.get(playerUuid).getOrDefault(eventName, 0);
    }
    
    public void incrementPlayerEventCount(UUID playerUuid, String eventName) {
        if (!playerEventCounts.containsKey(playerUuid)) {
            playerEventCounts.put(playerUuid, new HashMap<>());
        }
        Map<String, Integer> counts = playerEventCounts.get(playerUuid);
        counts.put(eventName, counts.getOrDefault(eventName, 0) + 1);
        markDirty();
    }
    
    // === WORLD STATE ===
    
    public int getCurrentPhase() {
        return currentPhase;
    }
    
    public void setCurrentPhase(int phase) {
        this.currentPhase = phase;
        markDirty();
    }
    
    public long getWorldCreationTime() {
        return worldCreationTime;
    }
    
    public void setWorldCreationTime(long time) {
        this.worldCreationTime = time;
        markDirty();
    }
    
    public boolean isFinalEventTriggered() {
        return finalEventTriggered;
    }
    
    public void setFinalEventTriggered(boolean triggered) {
        this.finalEventTriggered = triggered;
        markDirty();
    }
    
    // === UTILITY METHODS ===
    
    /**
     * Get current day number based on world time
     */
    public static int getCurrentDay(World world) {
        return (int) (world.getTimeOfDay() / 24000L) + 1;
    }
    
    /**
     * Reset all data (for testing)
     */
    public void resetAll() {
        playerSleepStages.clear();
        playerReadWarning.clear();
        playerPIIConsent.clear();
        eventHistory.clear();
        playerEventCounts.clear();
        currentPhase = 1;
        worldCreationTime = 0;
        finalEventTriggered = false;
        markDirty();
    }
}
