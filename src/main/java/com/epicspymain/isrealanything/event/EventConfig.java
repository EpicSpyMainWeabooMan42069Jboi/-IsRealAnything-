package com.epicspymain.isrealanything.event;

/**
 * Configuration for event system timing and behavior
 * Adjust these values to tune the horror experience
 */
public class EventConfig {
    
    // ===== GENERAL EVENT TIMING =====
    
    /** Delay before any events start (in ticks, 20 ticks = 1 second) */
    public static int FIRST_EVENT_DELAY = 24000; // 20 minutes (1 Minecraft day)
    
    /** Interval between creepy message events (in ticks) */
    public static int MESSAGE_INTERVAL = 12000; // 10 minutes
    
    /** Interval between ambient sound events (in ticks) */
    public static int SOUND_EVENT_INTERVAL = 3600; // 3 minutes
    
    /** Interval between peek/stalking events (in ticks) */
    public static int PEEK_EVENT_INTERVAL = 6000; // 5 minutes
    
    
    // ===== STALKING INTENSITY PROGRESSION =====
    
    /** Game day when stalking starts (LOW intensity) */
    public static int STALKING_START_DAY = 3;
    
    /** Game day when stalking reaches MEDIUM intensity */
    public static int STALKING_MEDIUM_DAY = 7;
    
    /** Game day when stalking reaches HIGH intensity */
    public static int STALKING_HIGH_DAY = 14;
    
    /** Game day when stalking reaches VERY_HIGH intensity */
    public static int STALKING_VERY_HIGH_DAY = 21;
    
    /** Game day when stalking reaches MAXIMUM intensity */
    public static int STALKING_MAXIMUM_DAY = 28;
    
    
    // ===== THE OVERLOOK EVENT =====
    
    /** Minimum game day before The Overlook can be triggered */
    public static int OVERLOOK_MINIMUM_DAY = 36;
    
    /** Enable automatic Overlook trigger (false = manual trigger only) */
    public static boolean OVERLOOK_AUTO_TRIGGER = false;
    
    /** If auto-trigger enabled, day to trigger The Overlook */
    public static int OVERLOOK_AUTO_TRIGGER_DAY = 40;
    
    
    // ===== PROGRESSION MILESTONES =====
    
    /** Day for first major scare event */
    public static int MILESTONE_DAY_7 = 7;
    
    /** Day for intensity increase event */
    public static int MILESTONE_DAY_14 = 14;
    
    /** Day for major escalation event */
    public static int MILESTONE_DAY_21 = 21;
    
    /** Day for pre-Overlook warning event */
    public static int MILESTONE_DAY_30 = 30;
    
    
    // ===== EVENT PROBABILITIES (per tick) =====
    
    /** Base chance for random events at LOW intensity */
    public static float EVENT_CHANCE_LOW = 0.001f; // 0.1%
    
    /** Base chance for random events at MEDIUM intensity */
    public static float EVENT_CHANCE_MEDIUM = 0.002f; // 0.2%
    
    /** Base chance for random events at HIGH intensity */
    public static float EVENT_CHANCE_HIGH = 0.003f; // 0.3%
    
    /** Base chance for random events at VERY_HIGH intensity */
    public static float EVENT_CHANCE_VERY_HIGH = 0.005f; // 0.5%
    
    /** Base chance for random events at MAXIMUM intensity */
    public static float EVENT_CHANCE_MAXIMUM = 0.01f; // 1.0%
    
    
    // ===== ENTITY SPAWN DISTANCES =====
    
    /** Minimum distance for distant stalking spawns */
    public static float SPAWN_DISTANCE_FAR_MIN = 48.0f;
    
    /** Maximum distance for distant stalking spawns */
    public static float SPAWN_DISTANCE_FAR_MAX = 64.0f;
    
    /** Minimum distance for medium stalking spawns */
    public static float SPAWN_DISTANCE_MEDIUM_MIN = 24.0f;
    
    /** Maximum distance for medium stalking spawns */
    public static float SPAWN_DISTANCE_MEDIUM_MAX = 40.0f;
    
    /** Minimum distance for close stalking spawns */
    public static float SPAWN_DISTANCE_CLOSE_MIN = 8.0f;
    
    /** Maximum distance for close stalking spawns */
    public static float SPAWN_DISTANCE_CLOSE_MAX = 16.0f;
    
    
    // ===== DIFFICULTY MODIFIERS =====
    
    /** Multiplier for event frequency (higher = more frequent events) */
    public static float EVENT_FREQUENCY_MULTIPLIER = 1.0f;
    
    /** Enable peaceful mode (disables all hostile events) */
    public static boolean PEACEFUL_MODE = false;
    
    /** Enable debug messages in chat */
    public static boolean DEBUG_MODE = false;
    
    
    // ===== HELPER METHODS =====
    
    /**
     * Get adjusted event interval with frequency multiplier
     */
    public static int getAdjustedInterval(int baseInterval) {
        return Math.max(20, (int)(baseInterval / EVENT_FREQUENCY_MULTIPLIER));
    }
    
    /**
     * Get adjusted event chance with frequency multiplier
     */
    public static float getAdjustedChance(float baseChance) {
        return Math.min(1.0f, baseChance * EVENT_FREQUENCY_MULTIPLIER);
    }
    
    /**
     * Check if events should run (respects peaceful mode)
     */
    public static boolean shouldRunEvents() {
        return !PEACEFUL_MODE;
    }
    
    /**
     * Convert ticks to seconds
     */
    public static float ticksToSeconds(int ticks) {
        return ticks / 20.0f;
    }
    
    /**
     * Convert ticks to minutes
     */
    public static float ticksToMinutes(int ticks) {
        return ticks / 1200.0f;
    }
    
    /**
     * Convert Minecraft days to ticks
     */
    public static int daysToTicks(int days) {
        return days * 24000;
    }
    
    /**
     * Print configuration summary to console
     */
    public static String getConfigSummary() {
        return String.format("""
            IsRealAnything Event Configuration:
            - First Event Delay: %.1f minutes
            - Stalking Starts: Day %d
            - Stalking Maximum: Day %d
            - Overlook Minimum Day: %d
            - Overlook Auto-Trigger: %s
            - Event Frequency Multiplier: %.1fx
            - Peaceful Mode: %s
            - Debug Mode: %s
            """,
            ticksToMinutes(FIRST_EVENT_DELAY),
            STALKING_START_DAY,
            STALKING_MAXIMUM_DAY,
            OVERLOOK_MINIMUM_DAY,
            OVERLOOK_AUTO_TRIGGER ? "Enabled (Day " + OVERLOOK_AUTO_TRIGGER_DAY + ")" : "Disabled",
            EVENT_FREQUENCY_MULTIPLIER,
            PEACEFUL_MODE ? "Enabled" : "Disabled",
            DEBUG_MODE ? "Enabled" : "Disabled"
        );
    }
}
