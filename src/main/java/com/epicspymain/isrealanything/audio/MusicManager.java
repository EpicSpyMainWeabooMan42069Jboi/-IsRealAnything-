package com.epicspymain.isrealanything.audio;

import com.epicspymain.isrealanything.IsRealAnything;
import com.epicspymain.isrealanything.sound.ModSounds;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MusicManager {
    private static final Random RANDOM = new Random();
    private static final List<SoundEvent> CUSTOM_MUSIC_TRACKS = new ArrayList<>();

    // Event 1 special track
    private static final SoundEvent EVENT1_TRACK = ModSounds.F_O_REV_E_R;

    private static boolean event1Played = false;
    private static boolean musicSystemInitialized = false;
    private static int tickCounter = 0;
    private static int musicCooldown = 0;

    // Music timing constants (in ticks)
    private static final int MUSIC_INTERVAL_MIN = 6000;  // 5 minutes
    private static final int MUSIC_INTERVAL_MAX = 12000; // 10 minutes
    private static final int DAY_2_START_TICK = 48000;   // 2 full Minecraft days (24000 ticks each)

    private static PositionedSoundInstance currentMusic = null;

    /**
     * Initialize the music system.
     * Call this from client initialization.
     */
    public static void initialize() {
        if (musicSystemInitialized) {
            return;
        }

        IsRealAnything.LOGGER.info("Initializing custom music system...");

        // Register custom music tracks (excluding Event 1 track and special sounds)
        CUSTOM_MUSIC_TRACKS.add(ModSounds.CRAFTMINETHINNG);
        CUSTOM_MUSIC_TRACKS.add(ModSounds.EMPTYNESS);
        CUSTOM_MUSIC_TRACKS.add(ModSounds.GLITCH);
        CUSTOM_MUSIC_TRACKS.add(ModSounds.INEEDYOU);
        CUSTOM_MUSIC_TRACKS.add(ModSounds.INSANITY);
        CUSTOM_MUSIC_TRACKS.add(ModSounds.ITHURTS);
        CUSTOM_MUSIC_TRACKS.add(ModSounds.MISC);
        CUSTOM_MUSIC_TRACKS.add(ModSounds.MISC2);
        CUSTOM_MUSIC_TRACKS.add(ModSounds.NOSTALGIA);
        CUSTOM_MUSIC_TRACKS.add(ModSounds.ANYONE);
        CUSTOM_MUSIC_TRACKS.add(ModSounds.REALITY);
        CUSTOM_MUSIC_TRACKS.add(ModSounds.PLACINGBLOCKSANDSHIT);
        CUSTOM_MUSIC_TRACKS.add(ModSounds.YOUR_MEMORY);
        CUSTOM_MUSIC_TRACKS.add(ModSounds.ILOVE);
        CUSTOM_MUSIC_TRACKS.add(ModSounds.EMPTY_DENIAL);
        CUSTOM_MUSIC_TRACKS.add(ModSounds.FOREVER);

        // Register tick event for music management
        ClientTickEvents.END_CLIENT_TICK.register(MusicManager::onClientTick);

        musicSystemInitialized = true;
        IsRealAnything.LOGGER.info("Custom music system initialized with {} tracks", CUSTOM_MUSIC_TRACKS.size());
    }

    /**
     * Client tick handler for music management.
     */
    private static void onClientTick(MinecraftClient client) {
        if (client.world == null || client.isPaused()) {
            return;
        }

        tickCounter++;

        // Check for Event 1 (Day 2 start) - play F.O.rev_E-R.ogg once
        if (!event1Played && tickCounter >= DAY_2_START_TICK) {
            playEvent1Music(client);
            event1Played = true;
            return;
        }

        // Silence vanilla music
        silenceVanillaMusic(client);

        // Handle custom music playback
        if (musicCooldown > 0) {
            musicCooldown--;
            return;
        }

        // Check if current music is still playing
        if (currentMusic != null && client.getSoundManager().isPlaying(currentMusic)) {
            return;
        }

        // Play next random track
        playRandomMusic(client);

        // Set cooldown for next track
        musicCooldown = MUSIC_INTERVAL_MIN + RANDOM.nextInt(MUSIC_INTERVAL_MAX - MUSIC_INTERVAL_MIN);
    }

    /**
     * Play Event 1 music (F.O.rev_E-R.ogg) - plays once on day 2 start.
     */
    private static void playEvent1Music(MinecraftClient client) {
        IsRealAnything.LOGGER.info("Event 1 triggered - Playing F.O.rev_E-R.ogg");

        stopCurrentMusic(client);

        currentMusic = PositionedSoundInstance.master(EVENT1_TRACK, 1.0f);
        client.getSoundManager().play(currentMusic);

        // Set long cooldown after event music
        musicCooldown = MUSIC_INTERVAL_MAX;
    }

    /**
     * Play a random custom music track (excluding Event 1 track).
     */
    private static void playRandomMusic(MinecraftClient client) {
        if (CUSTOM_MUSIC_TRACKS.isEmpty()) {
            return;
        }

        stopCurrentMusic(client);

        SoundEvent randomTrack = CUSTOM_MUSIC_TRACKS.get(RANDOM.nextInt(CUSTOM_MUSIC_TRACKS.size()));

        currentMusic = PositionedSoundInstance.master(randomTrack, 0.75f);
        client.getSoundManager().play(currentMusic);

        IsRealAnything.LOGGER.debug("Playing custom music: {}", randomTrack.id());
        IsRealAnything.LOGGER.info("Added custom music track: {}", track.id());
        IsRealAnything.LOGGER.info("Removed custom music track: {}", track.id());
    }

    /**
     * Silence vanilla Minecraft background music without affecting other sounds.
     */
    private static void silenceVanillaMusic(MinecraftClient client) {
        // Stop vanilla music tracker
        if (client.getMusicTracker() != null) {
            client.getMusicTracker().stop();
        }

        // Stop any vanilla music sounds
        client.getSoundManager().stopSounds(null, SoundCategory.MUSIC);
    }

    /**
     * Stop currently playing custom music.
     */
    private static void stopCurrentMusic(MinecraftClient client) {
        if (currentMusic != null) {
            client.getSoundManager().stop(currentMusic);
            currentMusic = null;
        }
    }

    /**
     * Add a custom music track to the playlist at runtime.
     */
    public static void addCustomTrack(SoundEvent track) {
        if (!CUSTOM_MUSIC_TRACKS.contains(track) && !track.equals(EVENT1_TRACK)) {
            CUSTOM_MUSIC_TRACKS.add(track);
            IsRealAnything.LOGGER.info("Added custom music track: {}", track.getId());
        }
    }

    /**
     * Remove a track from the playlist (for event-specific music changes).
     */
    public static void removeCustomTrack(SoundEvent track) {
        if (CUSTOM_MUSIC_TRACKS.remove(track)) {
            IsRealAnything.LOGGER.info("Removed custom music track: {}", track.getId());
        }
    }

    /**
     * Check if Event 1 music has been played.
     */
    public static boolean hasEvent1Played() {
        return event1Played;
    }

    /**
     * Reset Event 1 flag (for testing purposes only).
     */
    public static void resetEvent1() {
        event1Played = false;
        IsRealAnything.LOGGER.warn("Event 1 flag reset - F.O.rev_E-R.ogg will play again on day 2");
    }

    /**
     * Get current tick count.
     */
    public static int getTickCounter() {
        return tickCounter;
    }

    /**
     * Shutdown the music system.
     */
    public static void shutdown(MinecraftClient client) {
        stopCurrentMusic(client);
        IsRealAnything.LOGGER.info("Music system shutdown");
    }
}