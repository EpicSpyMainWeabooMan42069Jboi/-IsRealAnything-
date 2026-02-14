package com.epicspymain.isrealanything.events.helpers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Helper: SleepTracker - Tracks player sleep patterns
 * Used for sleep-related events and messages
 */
public class SleepTracker {
    
    private static final Map<UUID, SleepData> playerSleepData = new HashMap<>();
    
    /**
     * Record sleep attempt
     */
    public static void recordSleepAttempt(UUID playerUuid, long worldTime, boolean successful) {
        SleepData data = playerSleepData.computeIfAbsent(playerUuid, uuid -> new SleepData());
        
        data.totalSleepAttempts++;
        if (successful) {
            data.successfulSleeps++;
            data.lastSleepTime = worldTime;
            data.longestAwakeStreak = 0;
        } else {
            data.failedSleeps++;
        }
    }
    
    /**
     * Update awake time
     */
    public static void updateAwakeTime(UUID playerUuid, long worldTime) {
        SleepData data = playerSleepData.get(playerUuid);
        if (data != null && data.lastSleepTime > 0) {
            long ticksAwake = worldTime - data.lastSleepTime;
            data.longestAwakeStreak = Math.max(data.longestAwakeStreak, ticksAwake);
        }
    }
    
    /**
     * Get sleep data for player
     */
    public static SleepData getSleepData(UUID playerUuid) {
        return playerSleepData.computeIfAbsent(playerUuid, uuid -> new SleepData());
    }
    
    /**
     * Check if player is avoiding sleep
     */
    public static boolean isAvoidingSleep(UUID playerUuid, long worldTime) {
        SleepData data = playerSleepData.get(playerUuid);
        if (data == null || data.lastSleepTime == 0) {
            return false;
        }
        
        long ticksAwake = worldTime - data.lastSleepTime;
        return ticksAwake > 72000; // More than 3 days awake
    }
    
    /**
     * Sleep data class
     */
    public static class SleepData {
        public int totalSleepAttempts = 0;
        public int successfulSleeps = 0;
        public int failedSleeps = 0;
        public long lastSleepTime = 0;
        public long longestAwakeStreak = 0;
        
        public float getSleepSuccessRate() {
            if (totalSleepAttempts == 0) return 0;
            return (float) successfulSleeps / totalSleepAttempts;
        }
        
        public long getTicksSinceLastSleep(long currentTime) {
            if (lastSleepTime == 0) return 0;
            return currentTime - lastSleepTime;
        }
    }
}
