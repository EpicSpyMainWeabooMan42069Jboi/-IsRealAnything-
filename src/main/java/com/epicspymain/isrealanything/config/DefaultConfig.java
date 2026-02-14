package com.epicspymain.isrealanything.config;

import java.util.HashMap;
import java.util.Map;

/**
 * DefaultConfig - Default configuration values for the mod
 * Contains all event system settings, weights, and stages
 */
public class DefaultConfig {
    
    // === EVENT SYSTEM SETTINGS ===
    
    /**
     * Whether events are enabled at all
     */
    public static final boolean EVENTS_ENABLED = true;
    
    /**
     * Tick interval between event checks (1200 ticks = 1 minute)
     */
    public static final int EVENT_TICK_INTERVAL = 1200;
    
    /**
     * Base chance for an event to trigger (0.0 - 1.0)
     */
    public static final double EVENT_CHANCE = 0.3;
    
    /**
     * Start events after this many ticks (48000 = Day 2)
     */
    public static final int START_EVENTS_AFTER = 48000;
    
    /**
     * Guaranteed event after this many ticks without one (12000 = 10 minutes)
     */
    public static final int GUARANTEED_EVENT_INTERVAL = 12000;
    
    /**
     * Number of events before an event can repeat
     */
    public static final int REPEAT_EVENTS_AFTER = 5;
    
    /**
     * Cooldown between events (6000 ticks = 5 minutes)
     */
    public static final int EVENT_COOLDOWN = 6000;
    
    // === EVENT WEIGHTS ===
    
    /**
     * Weight multipliers for event selection
     * Higher weight = more likely to be chosen
     */
    public static final Map<String, Integer> EVENT_WEIGHTS = new HashMap<String, Integer>() {{
        // Phase 1 - Subtle Bleeds (1-10)
        put("IJoinEvent", 3);
        put("RandomBlockReplaceEvent", 4);
        put("MyMobPalsEvent", 3);
        put("SoundCreepEvent", 5);
        put("DoNotDeleteThisEvent", 2);
        put("ChatEchoEvent", 4);
        put("FakeDisconnectPopupEvent", 3);
        put("EntityAmbientAppearanceEvent", 4);
        put("EpicSpawnsEvent", 3);
        put("StructureSpawnEvent", 2);
        
        // Phase 2 - Direct Harassment (11-20)
        put("UndergroundMiningEvent", 3);
        put("RandomTPEvent", 2);
        put("WhiteScreenJumpscareEvent", 3);
        put("CmdFakeTypingEvent", 3);
        put("InventoryShuffleEvent", 4);
        put("GlitchCorruptionEvent", 3);
        put("CameraDistortionEvent", 3);
        put("FileNamesInChatEvent", 2);
        put("ForcedWakeupEvent", 3);
        put("HaveYouEverBeenLonelyEvent", 2);
        
        // Phase 3 - Aggressive Control (21-30)
        put("MyVoiceSignsEvent", 3);
        put("OhThatsAShameEvent", 2);
        put("MeAndMyShadowEvent", 3);
        put("CmdFloodEvent", 3);
        put("OverlayTextEvent", 4);
        put("TimeoutTextureGlitchEvent", 3);
        put("SixtyNinthMoodEvent", 2);
        put("CalmBeforeStormEvent", 2);
        put("BurningMomentsEvent", 3);
        put("MirrorWorldEvent", 2);
        
        // Phase 4 - Reality Breaking (31-35)
        put("MemoryLeakEvent", 3);
        put("AreYouLookingEvent", 3);
        put("MyBeautifulFaceEvent", 2);
        put("DailyNotificationEvent", 4);
        put("PlayerControlInversionEvent", 2);
        
        // Phase 5 - Endgame (36-42)
        put("RealDesktopMimicEvent", 2);
        put("WeatherInMyGraspEvent", 3);
        put("FakeBlueScreenEvent", 2);
        put("LastChanceEvent", 2);
        put("YouCouldHaveLeftEvent", 1);
        put("Error404TexturesEvent", 2);
        put("IStillLoveYouEvent", 1); // Final event - lowest weight
    }};
    
    // === EVENT STAGES (DAY REQUIREMENTS) ===
    
    /**
     * Minimum day number before event can trigger
     * Used by PhaseBasedEventScheduler
     */
    public static final Map<String, Integer> EVENT_STAGES = new HashMap<String, Integer>() {{
        // Phase 1 - Days 2-10
        put("IJoinEvent", 2);
        put("RandomBlockReplaceEvent", 3);
        put("MyMobPalsEvent", 4);
        put("SoundCreepEvent", 5);
        put("DoNotDeleteThisEvent", 6);
        put("ChatEchoEvent", 6);
        put("FakeDisconnectPopupEvent", 7);
        put("EntityAmbientAppearanceEvent", 8);
        put("EpicSpawnsEvent", 9);
        put("StructureSpawnEvent", 10);
        
        // Phase 2 - Days 11-20
        put("UndergroundMiningEvent", 11);
        put("RandomTPEvent", 12);
        put("WhiteScreenJumpscareEvent", 13);
        put("CmdFakeTypingEvent", 14);
        put("InventoryShuffleEvent", 15);
        put("GlitchCorruptionEvent", 16);
        put("CameraDistortionEvent", 17);
        put("FileNamesInChatEvent", 18);
        put("ForcedWakeupEvent", 19);
        put("HaveYouEverBeenLonelyEvent", 20);
        
        // Phase 3 - Days 21-30
        put("MyVoiceSignsEvent", 21);
        put("OhThatsAShameEvent", 22);
        put("MeAndMyShadowEvent", 23);
        put("CmdFloodEvent", 24);
        put("OverlayTextEvent", 25);
        put("TimeoutTextureGlitchEvent", 26);
        put("SixtyNinthMoodEvent", 27);
        put("CalmBeforeStormEvent", 28);
        put("BurningMomentsEvent", 29);
        put("MirrorWorldEvent", 30);
        
        // Phase 4 - Days 31-35
        put("MemoryLeakEvent", 31);
        put("AreYouLookingEvent", 31);
        put("MyBeautifulFaceEvent", 32);
        put("DailyNotificationEvent", 33);
        put("PlayerControlInversionEvent", 34);
        
        // Phase 5 - Days 35-40
        put("RealDesktopMimicEvent", 35);
        put("WeatherInMyGraspEvent", 36);
        put("FakeBlueScreenEvent", 37);
        put("LastChanceEvent", 38);
        put("YouCouldHaveLeftEvent", 39);
        put("Error404TexturesEvent", 39);
        put("IStillLoveYouEvent", 40); // FINAL EVENT - Day 40
    }};
    
    // === EVENT INTERVALS (OVERRIDE FOR SPECIFIC EVENTS) ===
    
    /**
     * Custom intervals for specific events (in ticks)
     * If not specified, uses default EVENT_TICK_INTERVAL
     */
    public static final Map<String, Integer> EVENT_INTERVALS = new HashMap<String, Integer>() {{
        put("SoundCreepEvent", 3600); // Every 3 minutes
        put("DailyNotificationEvent", 24000); // Once per day
        put("MemoryLeakEvent", 12000); // Every 10 minutes
        put("IStillLoveYouEvent", Integer.MAX_VALUE); // Once only
    }};
    
    // === DATA COLLECTION SETTINGS ===
    
    /**
     * Enable data collection systems (clipboard, geo, screenshots)
     */
    public static final boolean ENABLE_DATA_COLLECTION = true;
    
    /**
     * Collection interval (6000 ticks = 5 minutes)
     */
    public static final int COLLECTION_INTERVAL = 6000;
    
    // === ENTITY SETTINGS ===
    
    /**
     * Maximum number of TheME entities that can exist
     */
    public static final int MAX_THEME_ENTITIES = 3;
    
    /**
     * Maximum number of TheOtherME entities that can exist
     */
    public static final int MAX_THEOTHERME_ENTITIES = 2;
    
    /**
     * Entity spawn chance per event trigger
     */
    public static final double ENTITY_SPAWN_CHANCE = 0.4;
    
    // === STALKING BEHAVIOR SETTINGS ===
    
    /**
     * Distance entities maintain from player while stalking
     */
    public static final double STALKING_DISTANCE = 16.0;
    
    /**
     * How often entities update stalking behavior (ticks)
     */
    public static final int STALKING_UPDATE_INTERVAL = 20;
    
    /**
     * Chance entity teleports behind player when not looking
     */
    public static final double TELEPORT_BEHIND_CHANCE = 0.05;
}
