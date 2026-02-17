package com.epicspymain.isrealanything.event.helpers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SleepTracker {
    
    private static final Map<UUID, SleepData> playerSleepData = new HashMap<>();
    

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
    

    public static void updateAwakeTime(UUID playerUuid, long worldTime) {
        SleepData data = playerSleepData.get(playerUuid);
        if (data != null && data.lastSleepTime > 0) {
            long ticksAwake = worldTime - data.lastSleepTime;
            data.longestAwakeStreak = Math.max(data.longestAwakeStreak, ticksAwake);
        }
    }
    

    public static SleepData getSleepData(UUID playerUuid) {
        return playerSleepData.computeIfAbsent(playerUuid, uuid -> new SleepData());
    }
    

    public static boolean isAvoidingSleep(UUID playerUuid, long worldTime) {
        SleepData data = playerSleepData.get(playerUuid);
        if (data == null || data.lastSleepTime == 0) {
            return false;
        }
        
        long ticksAwake = worldTime - data.lastSleepTime;
        return ticksAwake > 72000; // More than 3 days awake
    }


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
