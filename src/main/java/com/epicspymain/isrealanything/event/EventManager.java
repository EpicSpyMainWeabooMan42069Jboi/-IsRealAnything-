package com.epicspymain.isrealanything.event;

import com.epicspymain.isrealanything.IsRealAnything;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * EventManager - Minimal working version
 * Tracks days and triggers events
 */
public class EventManager {

    // Tracking
    private static final Map<Integer, Boolean> triggeredEvents = new HashMap<>();
    private static int currentDay = 0;
    private static int tickCounter = 0;

    // Settings
    private static final int TICKS_PER_CHECK = 200; // Check every 10 seconds
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
            IsRealAnything.LOGGER.info("Day changed to: {}", currentDay);
        }

        // Don't trigger events before day 2
        if (currentDay < START_DAY) {
            return;
        }

        // Random chance to trigger an event
        Random random = new Random();
        if (random.nextDouble() < 0.1) { // 10% chance per check
            triggerRandomEvent(server);
        }
    }

    /**
     * Trigger a random event
     */
    private static void triggerRandomEvent(MinecraftServer server) {
        ServerPlayerEntity player = server.getPlayerManager().getPlayerList().get(0);

        if (player == null) {
            return;
        }

        Random random = new Random();
        int eventId = random.nextInt(5); // 0-4 for now (5 basic events)

        IsRealAnything.LOGGER.info("Triggering event: {}", eventId);

        switch (eventId) {
            case 0:
                // Test message
                player.sendMessage(Text.literal("§5[IsRealAnything] Something feels wrong..."), false);
                break;

            case 1:
                // Spawn TheME entity
                IJoinEvent.trigger(player);
                break;

            case 2:
                // Chat echo
                ChatEchoEvent.trigger(player);
                break;

            case 3:
                // Entity spawn
                EpicSpawnsEvent.trigger(player);
                break;

            case 4:
                // Glitch
                GlitchCorruptionEvent.trigger(player);
                break;

            default:
                player.sendMessage(Text.literal("§cEvent " + eventId + " not implemented"), false);
        }

        triggeredEvents.put(eventId, true);
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