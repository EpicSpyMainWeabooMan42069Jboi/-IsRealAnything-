package com.epicspymain.isrealanything.audio;

import com.epicspymain.isrealanything.IsRealAnything;
import com.epicspymain.isrealanything.sound.ModSounds;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MusicManager {
    private static final Random RANDOM = new Random();
    private static final List<SoundEvent> CUSTOM_MUSIC_TRACKS = new ArrayList<>();
    private static final SoundEvent EVENT1_TRACK = ModSounds.F_O_REV_E_R;

    private static boolean event1Played = false;
    private static boolean musicSystemInitialized = false;
    private static int tickCounter = 0;
    private static int musicCooldown = 0;

    private static final int MUSIC_INTERVAL_MIN = 6000;
    private static final int MUSIC_INTERVAL_MAX = 12000;
    private static final int DAY_2_START_TICK = 48000;

    private static PositionedSoundInstance currentMusic = null;

    public static void initialize() {
        if (musicSystemInitialized) return;

        IsRealAnything.LOGGER.info("Initializing custom music system...");

        CUSTOM_MUSIC_TRACKS.add(ModSounds.MENU);
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
        CUSTOM_MUSIC_TRACKS.add(ModSounds.LAUGH_DISTANT);
        CUSTOM_MUSIC_TRACKS.add(ModSounds.STATIC_NOISE);
        CUSTOM_MUSIC_TRACKS.add(ModSounds.BREATHING);
        CUSTOM_MUSIC_TRACKS.add(ModSounds.FOOTSTEPS_HORROR);
        CUSTOM_MUSIC_TRACKS.add(ModSounds.DOOR_CREAK);

        ClientTickEvents.END_CLIENT_TICK.register(MusicManager::onClientTick);

        musicSystemInitialized = true;
        IsRealAnything.LOGGER.info("Custom music system initialized with {} tracks", CUSTOM_MUSIC_TRACKS.size());
    }

    private static void onClientTick(MinecraftClient client) {
        if (client.world == null || client.isPaused()) return;

        tickCounter++;

        if (!event1Played && tickCounter >= DAY_2_START_TICK) {
            playEvent1Music(client);
            event1Played = true;
            return;
        }

        silenceVanillaMusic(client);

        if (musicCooldown > 0) {
            musicCooldown--;
            return;
        }

        if (currentMusic != null && client.getSoundManager().isPlaying(currentMusic)) return;

        playRandomMusic(client);
        musicCooldown = MUSIC_INTERVAL_MIN + RANDOM.nextInt(MUSIC_INTERVAL_MAX - MUSIC_INTERVAL_MIN);
    }

    private static void playEvent1Music(MinecraftClient client) {
        IsRealAnything.LOGGER.info("Event 1 triggered - Playing F.O.rev_E-R.ogg");
        stopCurrentMusic(client);
        currentMusic = PositionedSoundInstance.master(EVENT1_TRACK, 1.0f);
        client.getSoundManager().play(currentMusic);
        musicCooldown = MUSIC_INTERVAL_MAX;
    }

    private static void playRandomMusic(MinecraftClient client) {
        if (CUSTOM_MUSIC_TRACKS.isEmpty()) return;
        stopCurrentMusic(client);
        SoundEvent randomTrack = CUSTOM_MUSIC_TRACKS.get(RANDOM.nextInt(CUSTOM_MUSIC_TRACKS.size()));
        currentMusic = PositionedSoundInstance.master(randomTrack, 0.75f);
        client.getSoundManager().play(currentMusic);
        IsRealAnything.LOGGER.debug("Playing custom music: {}", randomTrack.id());
    }

    private static void silenceVanillaMusic(MinecraftClient client) {
        if (client.getMusicTracker() != null) client.getMusicTracker().stop();
        client.getSoundManager().stopSounds(null, SoundCategory.MUSIC);
    }

    private static void stopCurrentMusic(MinecraftClient client) {
        if (currentMusic != null) {
            client.getSoundManager().stop(currentMusic);
            currentMusic = null;
        }
    }

    public static void addCustomTrack(SoundEvent track) {
        if (!CUSTOM_MUSIC_TRACKS.contains(track) && !track.equals(EVENT1_TRACK)) {
            CUSTOM_MUSIC_TRACKS.add(track);
            IsRealAnything.LOGGER.info("Added custom music track: {}", track.id());
        }
    }

    public static void removeCustomTrack(SoundEvent track) {
        if (CUSTOM_MUSIC_TRACKS.remove(track)) {
            IsRealAnything.LOGGER.info("Removed custom music track: {}", track.id());
        }
    }

    public static boolean hasEvent1Played() { return event1Played; }

    public static void resetEvent1() {
        event1Played = false;
        IsRealAnything.LOGGER.warn("Event 1 flag reset - F.O.rev_E-R.ogg will play again on day 2");
    }

    public static int getTickCounter() { return tickCounter; }

    public static void shutdown(MinecraftClient client) {
        stopCurrentMusic(client);
        IsRealAnything.LOGGER.info("Music system shutdown");
    }
}