package com.epicspymain.isrealanything.event;

import com.epicspymain.isrealanything.IsRealAnything;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.*;

/**
 * EventManager - Complete event system with 47 events
 * Phase-based progression starting Day 2
 */
public class EventManager {

    // Event enum with all 47 events
    public enum Events {
        // Phase 1: Day 2-4 (Subtle)
        IJOIN(1, false), // Day 2 spawn - ONE TIME
        CHAT_ECHO(1, true),
        SOUND_CREEP(1, true),
        OVERLAY_TEXT(1, true),
        ENTITY_AMBIENT(1, true),
        
        // Phase 2: Day 5-9 (Escalating)
        EPIC_SPAWNS(2, true),
        GLITCH_CORRUPTION(2, true),
        MY_MOB_PALS(2, true),
        WHITE_SCREEN_JUMPSCARE(2, true),
        CAMERA_DISTORTION(2, true),
        MY_VOICE_SIGNS(2, true),
        FILE_NAMES_IN_CHAT(2, true),
        HOUSE(2, false), // Freedom Home structure - ONE TIME
        IRONTRAP(2, false), // Iron trap structure - ONE TIME
        SIXTYNINETH_MOOD(2, true),
        
        // Phase 3: Day 10-19 (Intense)
        FAKE_DISCONNECT_POPUP(3, true),
        PLAYER_CONTROL_INVERSION(3, true),
        INVENTORY_SHUFFLE(3, true),
        FAKE_BLUE_SCREEN(3, true),
        RANDOM_TP(3, true),
        WEATHER_IN_MY_GRASP(3, true),
        BURNING_MOMENTS(3, true),
        FORCED_WAKEUP(3, true),
        CALM_BEFORE_STORM(3, true),
        BEDROCKPILLAR(3, false), // Bedrock pillar structure - ONE TIME
        MINE(3, false), // Strip mine structure - ONE TIME
        MEADOW(3, false), // Meadow structure - ONE TIME
        LIMBO_EXILE(3, true), // NEW: Limbo dimension event
        
        // Phase 4: Day 20+ (Psychological)
        RANDOM_BLOCK_REPLACE(4, true),
        UNDERGROUND_MINING(4, true),
        ERROR_404_TEXTURES(4, true),
        TIMEOUT_TEXTURE_GLITCH(4, true),
        MIRROR_WORLD(4, true),
        ME_AND_MY_SHADOW(4, true),
        MEMORY(4, false), // Memory structure - ONE TIME
        HAVE_YOU_EVER_BEEN_LONELY(4, true),
        I_STILL_LOVE_YOU(4, true),
        MY_BEAUTIFUL_FACE(4, true),
        ARE_YOU_LOOKING(4, true),
        THE_OVERLOOK(4, false), // Special entity event - ONE TIME
        
        // Phase 5: Day 30+ (Endgame)
        REAL_DESKTOP_MIMIC(5, true),
        BROWSER(5, true),
        CMD_FAKE_TYPING(5, true),
        CMD_FLOOD(5, true),
        DO_NOT_DELETE_THIS(5, true),
        YOU_COULD_HAVE_LEFT(5, false), // Final message - ONE TIME
        LAST_CHANCE(5, false), // Point of no return - ONE TIME
        OH_THATS_A_SHAME(5, false); // Game over event - ONE TIME
        
        public final int minPhase;
        public final boolean repeatable;
        
        Events(int minPhase, boolean repeatable) {
            this.minPhase = minPhase;
            this.repeatable = repeatable;
        }
    }

    // Tracking
    private static final Set<Events> triggeredEvents = new HashSet<>();
    private static int currentDay = 0;
    private static int tickCounter = 0;
    private static final Random random = new Random();

    // Settings
    private static final int TICKS_PER_CHECK = 600; // Check every 30 seconds
    private static final int START_DAY = 2; // Events start on day 2

    /**
     * Called every server tick
     */
    public static void onTick(MinecraftServer server) {
        if (server == null || server.getPlayerManager().getPlayerList().isEmpty()) {
            return;
        }

        tickCounter++;

        // Check every TICKS_PER_CHECK ticks
        if (tickCounter % TICKS_PER_CHECK != 0) {
            return;
        }

        ServerWorld overworld = server.getOverworld();
        long worldTime = overworld.getTimeOfDay();
        int calculatedDay = (int) (worldTime / 24000);

        // Update current day
        if (calculatedDay > currentDay) {
            currentDay = calculatedDay;
            int phase = getCurrentPhase(worldTime);
            IsRealAnything.LOGGER.info("Day changed to: {} (Phase {})", currentDay, phase);
        }

        // Don't trigger events before day 2
        if (currentDay < START_DAY) {
            return;
        }

        // Day 2: Trigger IJoinEvent once, then start normal event system
        if (currentDay == 2 && !triggeredEvents.contains(Events.IJOIN)) {
            ServerPlayerEntity player = server.getPlayerManager().getPlayerList().get(0);
            if (player != null) {
                triggerEvent(Events.IJOIN, server);
                IsRealAnything.LOGGER.info("Day 2: IJoinEvent triggered");
            }
            return;
        }

        // Get eligible events for current phase
        List<Events> eligible = getEligibleEvents(worldTime);
        
        // Debug logging
        int phase = getCurrentPhase(worldTime);
        IsRealAnything.LOGGER.info("Day {}, Phase {}, Eligible events: {}", currentDay, phase, eligible.size());

        if (eligible.isEmpty()) {
            return;
        }

        // Random chance to trigger an event (15% per check = ~every 3-4 minutes)
        if (random.nextDouble() < 0.15) {
            Events event = eligible.get(random.nextInt(eligible.size()));
            triggerEvent(event, server);
        }
    }

    /**
     * Get current phase based on world time
     */
    private static int getCurrentPhase(long worldTime) {
        int day = (int) (worldTime / 24000);
        
        if (day < 5) return 1;   // Phase 1: Day 2-4
        if (day < 10) return 2;  // Phase 2: Day 5-9
        if (day < 20) return 3;  // Phase 3: Day 10-19
        if (day < 30) return 4;  // Phase 4: Day 20-29
        return 5;                 // Phase 5: Day 30+
    }

    /**
     * Get eligible events for current world time
     */
    private static List<Events> getEligibleEvents(long worldTime) {
        List<Events> eligible = new ArrayList<>();
        int currentPhase = getCurrentPhase(worldTime);
        
        for (Events event : Events.values()) {
            // Skip if phase not reached
            if (event.minPhase > currentPhase) {
                continue;
            }
            
            // Skip if already triggered and not repeatable
            if (!event.repeatable && triggeredEvents.contains(event)) {
                continue;
            }
            
            // Skip IJoinEvent after first trigger
            if (event == Events.IJOIN) {
                continue;
            }
            
            eligible.add(event);
        }
        
        return eligible;
    }

    /**
     * Trigger a specific event
     */
    private static void triggerEvent(Events event, MinecraftServer server) {
        ServerPlayerEntity player = server.getPlayerManager().getPlayerList().get(0);
        if (player == null) return;
        
        ServerWorld world = server.getOverworld();
        
        IsRealAnything.LOGGER.info("Triggering event: {}", event.name());
        
        try {
            switch (event) {
                // Phase 1
                case IJOIN:
                    IJoinEvent.trigger(player);
                    break;
                case CHAT_ECHO:
                    ChatEchoEvent.trigger(player);
                    break;
                case SOUND_CREEP:
                    SoundCreepEvent.trigger(player);
                    break;
                case OVERLAY_TEXT:
                    OverlayTextEvent.trigger(player);
                    break;
                case ENTITY_AMBIENT:
                    EntityAmbientAppearanceEvent.trigger(player);
                    break;
                
                // Phase 2
                case EPIC_SPAWNS:
                    EpicSpawnsEvent.trigger(player);
                    break;
                case GLITCH_CORRUPTION:
                    GlitchCorruptionEvent.trigger(player);
                    break;
                case MY_MOB_PALS:
                    MyMobPalsEvent.trigger(player);
                    break;
                case WHITE_SCREEN_JUMPSCARE:
                    WhiteScreenJumpscareEvent.trigger(player);
                    break;
                case CAMERA_DISTORTION:
                    CameraDistortionEvent.trigger(player);
                    break;
                case MY_VOICE_SIGNS:
                    MyVoiceSignsEvent.trigger(player);
                    break;
                case FILE_NAMES_IN_CHAT:
                    FileNamesInChatEvent.trigger(player);
                    break;
                case HOUSE:
                    StructureSpawnEvent.spawnFreedomHome(world, player);
                    break;
                case IRONTRAP:
                    StructureSpawnEvent.spawnIronTrap(world, player);
                    break;
                case SIXTYNINETH_MOOD:
                    SixtyNinthMoodEvent.trigger(player);
                    break;
                
                // Phase 3
                case FAKE_DISCONNECT_POPUP:
                    FakeDisconnectPopupEvent.trigger(player);
                    break;
                case PLAYER_CONTROL_INVERSION:
                    PlayerControlInversionEvent.trigger(player);
                    break;
                case INVENTORY_SHUFFLE:
                    InventoryShuffleEvent.trigger(player);
                    break;
                case FAKE_BLUE_SCREEN:
                    FakeBlueScreenEvent.trigger(player);
                    break;
                case RANDOM_TP:
                    RandomTPEvent.trigger(player);
                    break;
                case WEATHER_IN_MY_GRASP:
                    WeatherInMyGraspEvent.trigger(player);
                    break;
                case BURNING_MOMENTS:
                    BurningMomentsEvent.trigger(player);
                    break;
                case FORCED_WAKEUP:
                    ForcedWakeupEvent.trigger(player);
                    break;
                case CALM_BEFORE_STORM:
                    CalmBeforeStormEvent.trigger(player);
                    break;
                case BEDROCKPILLAR:
                    StructureSpawnEvent.spawnBedrockPillar(world, player);
                    break;
                case MINE:
                    StructureSpawnEvent.spawnStripMine(world, player);
                    break;
                case MEADOW:
                    StructureSpawnEvent.spawnMeadow(world, player);
                    break;
                case LIMBO_EXILE:
                    LimboExileEvent.trigger(player);
                    break;
                
                // Phase 4
                case RANDOM_BLOCK_REPLACE:
                    RandomBlockReplaceEvent.trigger(player);
                    break;
                case UNDERGROUND_MINING:
                    UndergroundMiningEvent.trigger(player);
                    break;
                case ERROR_404_TEXTURES:
                    Error404TexturesEvent.trigger(player);
                    break;
                case TIMEOUT_TEXTURE_GLITCH:
                    TimeoutTextureGlitchEvent.trigger(player);
                    break;
                case MIRROR_WORLD:
                    MirrorWorldEvent.trigger(player);
                    break;
                case ME_AND_MY_SHADOW:
                    MeAndMyShadowEvent.trigger(player);
                    break;
                case MEMORY:
                    StructureSpawnEvent.spawnMemory(world, player);
                    break;
                case HAVE_YOU_EVER_BEEN_LONELY:
                    HaveYouEverBeenLonelyEvent.trigger(player);
                    break;
                case I_STILL_LOVE_YOU:
                    IStillLoveYouEvent.trigger(player);
                    break;
                case MY_BEAUTIFUL_FACE:
                    MyBeautifulFaceEvent.trigger(player);
                    break;
                case ARE_YOU_LOOKING:
                    AreYouLookingEvent.trigger(player);
                    break;
                case THE_OVERLOOK:
                    TheOverlook.trigger(player);
                    break;
                
                // Phase 5
                case REAL_DESKTOP_MIMIC:
                    RealDesktopMimicEvent.trigger(player);
                    break;
                case BROWSER:
                    BrowserEvent.trigger(player);
                    break;
                case CMD_FAKE_TYPING:
                    CmdFakeTypingEvent.trigger(player);
                    break;
                case CMD_FLOOD:
                    CmdFloodEvent.trigger(player);
                    break;
                case DO_NOT_DELETE_THIS:
                    DoNotDeleteThisEvent.trigger(player);
                    break;
                case YOU_COULD_HAVE_LEFT:
                    YouCouldHaveLeftEvent.trigger(player);
                    break;
                case LAST_CHANCE:
                    LastChanceEvent.trigger(player);
                    break;
                case OH_THATS_A_SHAME:
                    OhThatsAShameEvent.trigger(player);
                    break;
            }
            
            // Mark as triggered
            triggeredEvents.add(event);
            
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error triggering event {}: {}", event.name(), e.getMessage());
        }
    }

    /**
     * Get current day
     */
    public static int getCurrentDay() {
        return currentDay;
    }

    /**
     * Reset the event system
     */
    public static void reset() {
        triggeredEvents.clear();
        currentDay = 0;
        tickCounter = 0;
        IsRealAnything.LOGGER.info("EventManager reset");
    }
}