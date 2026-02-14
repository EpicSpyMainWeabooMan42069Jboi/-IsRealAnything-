package com.epicspymain.isrealanything.audio;

import com.epicspymain.isrealanything.IsRealAnything;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MusicType;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Manages custom background music replacement for IsRealAnything.
 * - Silences vanilla Minecraft background music
 * - Plays custom OGG files randomly
 * - Special handling for Event 1 (IJoin) with F.O.rev_E-R.ogg
 */
public class MusicManager {
    private static final Random RANDOM = new Random();
    private static final List<Identifier> CUSTOM_MUSIC_TRACKS = new ArrayList<>();
    private static final Identifier EVENT1_TRACK = Identifier.of("isrealanything", "music.event1");
    
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
        
        // Register custom music tracks (excluding Event 1 track)
        CUSTOM_MUSIC_TRACKS.add(Identifier.of("isrealanything", "music.custom_track_1"));
        CUSTOM_MUSIC_TRACKS.add(Identifier.of("isrealanything", "music.custom_track_2"));
        CUSTOM_MUSIC_TRACKS.add(Identifier.of("isrealanything", "music.custom_track_3"));
        CUSTOM_MUSIC_TRACKS.add(Identifier.of("isrealanything", "music.custom_track_4"));
        
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
        
        SoundEvent soundEvent = SoundEvent.of(EVENT1_TRACK);
        currentMusic = PositionedSoundInstance.master(soundEvent, 1.0f);
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
        
        Identifier randomTrack = CUSTOM_MUSIC_TRACKS.get(RANDOM.nextInt(CUSTOM_MUSIC_TRACKS.size()));
        SoundEvent soundEvent = SoundEvent.of(randomTrack);
        
        currentMusic = PositionedSoundInstance.master(soundEvent, 0.75f);
        client.getSoundManager().play(currentMusic);
        
        IsRealAnything.LOGGER.debug("Playing custom music: {}", randomTrack);
    }
    
    /**
     * Silence vanilla Minecraft background music without affecting other sounds.
     */
    private static void silenceVanillaMusic(MinecraftClient client) {
        // Stop vanilla music tracker
        if (client.getMusicTracker() != null && client.getMusicTracker().isPlayingType(MusicType.MENU)) {
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
     * Add a custom music track to the playlist.
     */
    public static void addCustomTrack(Identifier trackId) {
        if (!CUSTOM_MUSIC_TRACKS.contains(trackId) && !trackId.equals(EVENT1_TRACK)) {
            CUSTOM_MUSIC_TRACKS.add(trackId);
            IsRealAnything.LOGGER.info("Added custom music track: {}", trackId);
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
