package com.epicspymain.isrealanything.event;

import com.epicspymain.isrealanything.ai.ContextualMessageManager;
import com.epicspymain.isrealanything.ai.StalkingController;
import com.epicspymain.isrealanything.sound.ModSounds;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;

/**
 * Central event coordination system for IsRealAnything mod
 * Manages horror events, triggers, and progression-based stalking
 */
public class EventManager {
    private static final EventManager INSTANCE = new EventManager();
    
    // Event state tracking
    private final Map<UUID, PlayerEventState> playerStates = new HashMap<>();
    private final StalkingController stalkingController = StalkingController.getInstance();
    private final ContextualMessageManager messageManager = ContextualMessageManager.getInstance();
    
    // Global event flags
    private static boolean normalSchedulingHalted = false;
    private static final Set<Integer> triggeredEvents = new HashSet<>();
    
    // Configuration loaded from EventConfig
    
    private EventManager() {}
    
    public static EventManager getInstance() {
        return INSTANCE;
    }
    
    /**
     * Tick all active player events - call from server tick
     */
    public void tick(ServerWorld world) {
        // OVERLOOK OVERRIDE: Stop all normal scheduling if triggered
        if (TheOverlook.OVERLOOK_TRIGGERED) {
            return;
        }
        
        for (ServerPlayerEntity player : world.getPlayers()) {
            PlayerEventState state = getOrCreateState(player);
            tickPlayerEvents(world, player, state);
        }
    }
    
    /**
     * Tick individual player's event state
     */
    private void tickPlayerEvents(ServerWorld world, ServerPlayerEntity player, PlayerEventState state) {
        state.ticksPlayed++;
        
        // Skip all events if Overlook is triggered or peaceful mode
        if (TheOverlook.OVERLOOK_TRIGGERED || !EventConfig.shouldRunEvents()) {
            return;
        }
        
        // Wait for initial delay before any events
        if (state.ticksPlayed < EventConfig.getAdjustedInterval(EventConfig.FIRST_EVENT_DELAY)) {
            return;
        }
        
        // Update stalking intensity based on progression
        int gameDay = (int) (state.ticksPlayed / 24000);
        StalkingController.StalkingIntensity recommendedIntensity = 
            stalkingController.getRecommendedIntensity(gameDay);
        
        if (state.currentIntensity.ordinal() < recommendedIntensity.ordinal()) {
            state.currentIntensity = recommendedIntensity;
            stalkingController.setIntensity(recommendedIntensity);
        }
        
        // Periodic events
        triggerPeriodicEvents(world, player, state);
        
        // Random events based on intensity
        triggerRandomEvents(world, player, state);
        
        // Check for progression milestones
        checkProgressionMilestones(world, player, state);
    }
    
    /**
     * Trigger time-based periodic events
     */
    private void triggerPeriodicEvents(ServerWorld world, ServerPlayerEntity player, PlayerEventState state) {
        long currentTick = state.ticksPlayed;
        
        // Creepy messages
        if (currentTick - state.lastMessageTick >= EventConfig.getAdjustedInterval(EventConfig.MESSAGE_INTERVAL)) {
            if (world.random.nextFloat() < 0.7f) { // 70% chance
                messageManager.sendCreepyMessage(player);
                state.lastMessageTick = currentTick;
            }
        }
        
        // Ambient horror sounds
        if (currentTick - state.lastSoundEventTick >= EventConfig.getAdjustedInterval(EventConfig.SOUND_EVENT_INTERVAL)) {
            if (world.random.nextFloat() < 0.5f) { // 50% chance
                triggerAmbientSound(world, player);
                state.lastSoundEventTick = currentTick;
            }
        }
        
        // Peek events (entity watching from distance)
        if (currentTick - state.lastPeekEventTick >= EventConfig.getAdjustedInterval(EventConfig.PEEK_EVENT_INTERVAL)) {
            if (world.random.nextFloat() < 0.6f) { // 60% chance
                float distance = 32.0f + world.random.nextFloat() * 32.0f; // 32-64 blocks
                stalkingController.triggerPeek(world, player, distance);
                state.lastPeekEventTick = currentTick;
            }
        }
    }
    
    /**
     * Trigger random intensity-based events
     */
    private void triggerRandomEvents(ServerWorld world, ServerPlayerEntity player, PlayerEventState state) {
        float randomRoll = world.random.nextFloat();
        
        switch (state.currentIntensity) {
            case LOW:
                if (randomRoll < EventConfig.getAdjustedChance(EventConfig.EVENT_CHANCE_LOW)) {
                    stalkingController.triggerPeek(world, player, EventConfig.SPAWN_DISTANCE_FAR_MIN);
                }
                break;
                
            case MEDIUM:
                if (randomRoll < EventConfig.getAdjustedChance(EventConfig.EVENT_CHANCE_MEDIUM)) {
                    stalkingController.triggerPeek(world, player, EventConfig.SPAWN_DISTANCE_MEDIUM_MIN);
                } else if (randomRoll < EventConfig.getAdjustedChance(EventConfig.EVENT_CHANCE_MEDIUM) * 1.25f) {
                    triggerAmbientSound(world, player);
                }
                break;
                
            case HIGH:
                if (randomRoll < EventConfig.getAdjustedChance(EventConfig.EVENT_CHANCE_HIGH)) {
                    stalkingController.triggerBehindPlayer(world, player);
                } else if (randomRoll < EventConfig.getAdjustedChance(EventConfig.EVENT_CHANCE_HIGH) * 1.5f) {
                    stalkingController.triggerPeek(world, player, EventConfig.SPAWN_DISTANCE_CLOSE_MAX);
                }
                break;
                
            case VERY_HIGH:
                if (randomRoll < EventConfig.getAdjustedChance(EventConfig.EVENT_CHANCE_VERY_HIGH)) {
                    stalkingController.triggerBehindPlayer(world, player);
                } else if (randomRoll < EventConfig.getAdjustedChance(EventConfig.EVENT_CHANCE_VERY_HIGH) * 1.6f) {
                    stalkingController.triggerGroupSpawn(world, player, 2);
                }
                break;
                
            case MAXIMUM:
                if (randomRoll < EventConfig.getAdjustedChance(EventConfig.EVENT_CHANCE_MAXIMUM)) {
                    stalkingController.triggerGroupSpawn(world, player, 3);
                } else if (randomRoll < EventConfig.getAdjustedChance(EventConfig.EVENT_CHANCE_MAXIMUM) * 1.5f) {
                    stalkingController.triggerBehindPlayer(world, player);
                }
                break;
        }
    }
    
    /**
     * Check for progression milestones and trigger special events
     */
    private void checkProgressionMilestones(ServerWorld world, ServerPlayerEntity player, PlayerEventState state) {
        int gameDay = (int) (state.ticksPlayed / 24000);
        
        // Day 7: First major scare
        if (gameDay >= EventConfig.MILESTONE_DAY_7 && !state.milestonesReached.contains("day7")) {
            triggerMilestoneEvent(world, player, "day7", "I'VE BEEN WATCHING YOU");
            stalkingController.triggerBehindPlayer(world, player);
            state.milestonesReached.add("day7");
        }
        
        // Day 14: Intensity increase
        if (gameDay >= EventConfig.MILESTONE_DAY_14 && !state.milestonesReached.contains("day14")) {
            triggerMilestoneEvent(world, player, "day14", "YOU CAN'T HIDE FROM ME");
            stalkingController.triggerGroupSpawn(world, player, 2);
            state.milestonesReached.add("day14");
        }
        
        // Day 21: Major escalation
        if (gameDay >= EventConfig.MILESTONE_DAY_21 && !state.milestonesReached.contains("day21")) {
            triggerMilestoneEvent(world, player, "day21", "I AM ALWAYS HERE");
            world.playSound(null, player.getBlockPos(), ModSounds.SCREAM, 
                SoundCategory.HOSTILE, 1.0f, 1.0f);
            state.milestonesReached.add("day21");
        }
        
        // Day 30: Pre-Overlook warning
        if (gameDay >= EventConfig.MILESTONE_DAY_30 && !state.milestonesReached.contains("day30")) {
            triggerMilestoneEvent(world, player, "day30", "THE END IS NEAR");
            state.milestonesReached.add("day30");
        }
        
        // Day 36+: Ready for The Overlook (triggered externally)
        if (gameDay >= EventConfig.OVERLOOK_MINIMUM_DAY && !OVERLOOK_TRIGGERED && !state.milestonesReached.contains("overlook_ready")) {
            player.sendMessage(Text.literal("Something is coming...").formatted(Formatting.DARK_RED, Formatting.BOLD), false);
            state.milestonesReached.add("overlook_ready");
            
            // Auto-trigger if enabled
            if (EventConfig.OVERLOOK_AUTO_TRIGGER && gameDay >= EventConfig.OVERLOOK_AUTO_TRIGGER_DAY) {
                triggerOverlook(world, player);
            }
        }
    }
    
    /**
     * Trigger ambient horror sound near player
     */
    private void triggerAmbientSound(ServerWorld world, ServerPlayerEntity player) {
        List<net.minecraft.sound.SoundEvent> ambientSounds = Arrays.asList(
            ModSounds.WHISPER_1,
            ModSounds.WHISPER_2,
            ModSounds.STATIC_NOISE,
            ModSounds.BREATHING,
            ModSounds.FOOTSTEPS_HORROR,
            ModSounds.DOOR_CREAK,
            ModSounds.LAUGH_DISTANT
        );
        
        net.minecraft.sound.SoundEvent sound = ambientSounds.get(world.random.nextInt(ambientSounds.size()));
        float volume = 0.3f + world.random.nextFloat() * 0.3f; // 0.3-0.6
        float pitch = 0.8f + world.random.nextFloat() * 0.4f; // 0.8-1.2
        
        world.playSound(null, player.getBlockPos(), sound, SoundCategory.AMBIENT, volume, pitch);
    }
    
    /**
     * Trigger special milestone event
     */
    private void triggerMilestoneEvent(ServerWorld world, ServerPlayerEntity player, String milestone, String message) {
        player.sendMessage(Text.literal(message).formatted(Formatting.DARK_RED, Formatting.OBFUSCATED), false);
        world.playSound(null, player.getBlockPos(), ModSounds.EVENT_WATCHING, 
            SoundCategory.HOSTILE, 0.8f, 0.9f);
    }
    
    /**
     * Halt all normal event scheduling (called by TheOverlook)
     */
    public static void haltNormalScheduling() {
        normalSchedulingHalted = true;
    }
    
    /**
     * Check if event has occurred
     */
    public static boolean hasEventOccurred(int eventId) {
        return triggeredEvents.contains(eventId);
    }
    
    /**
     * Mark event as occurred
     */
    public static void markEventOccurred(int eventId) {
        triggeredEvents.add(eventId);
    }
    
    /**
     * Force run all events except specified one (called by TheOverlook)
     */
    public static void forceRunAllExcept(int excludedEventId) {
        // Mark all events as triggered except the excluded one
        for (int i = 1; i <= 42; i++) {
            if (i != excludedEventId) {
                triggeredEvents.add(i);
            }
        }
    }
    
    /**
     * Process chat message for forbidden commands
     */
    public static void processChatMessage(ServerPlayerEntity player, String message) {
        // Check for forbidden commands
        if (TheOverlook.checkForbiddenCommand(message)) {
            TheOverlook.trigger(player, player.getServer());
        }
    }
    
    /**
     * Handle entity death for Overlook detection
     */
    public static void onEntityDeath(ServerPlayerEntity killer, net.minecraft.entity.LivingEntity victim) {
        TheOverlook.onEntityDeath(killer, victim);
    }
    
    /**
     * Reset player state (for respawn/new world)
     */
    public void resetPlayerState(UUID playerUuid) {
        playerStates.remove(playerUuid);
    }
    
    /**
     * Get or create player event state
     */
    private PlayerEventState getOrCreateState(ServerPlayerEntity player) {
        return playerStates.computeIfAbsent(player.getUuid(), uuid -> new PlayerEventState());
    }
    
    /**
     * Player-specific event state
     */
    private static class PlayerEventState {
        long ticksPlayed = 0;
        long lastMessageTick = 0;
        long lastSoundEventTick = 0;
        long lastPeekEventTick = 0;
        StalkingController.StalkingIntensity currentIntensity = StalkingController.StalkingIntensity.NONE;
        Set<String> milestonesReached = new HashSet<>();
    }
}
