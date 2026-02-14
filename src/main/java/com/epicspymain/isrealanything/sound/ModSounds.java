package com.epicspymain.isrealanything.sound;

import com.epicspymain.isrealanything.IsRealAnything;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

/**
 * Registers all custom sound events for IsRealAnything mod.
 * Includes music disc sounds, ambient sounds, and event-triggered sounds.
 */
public class ModSounds {
    
    // Music Disc Sound
    public static final SoundEvent UNTITLED_DISK_SONG = registerSoundEvent("untitled_disk_song");
    
    // Custom Horror/Event Sounds
    public static final SoundEvent TED_LEWIS_FUCK_YOU = registerSoundEvent("ted_lewis_fuck_you");
    public static final SoundEvent ERRRRRR = registerSoundEvent("errrrrr");
    public static final SoundEvent FOREVER = registerSoundEvent("forever");
    
    // Additional Ambient/Horror Sounds
    public static final SoundEvent WHISPER_1 = registerSoundEvent("whisper_1");
    public static final SoundEvent WHISPER_2 = registerSoundEvent("whisper_2");
    public static final SoundEvent STATIC_NOISE = registerSoundEvent("static_noise");
    public static final SoundEvent HEARTBEAT = registerSoundEvent("heartbeat");
    public static final SoundEvent BREATHING = registerSoundEvent("breathing");
    public static final SoundEvent DOOR_CREAK = registerSoundEvent("door_creak");
    public static final SoundEvent FOOTSTEPS_HORROR = registerSoundEvent("footsteps_horror");
    public static final SoundEvent LAUGH_DISTANT = registerSoundEvent("laugh_distant");
    public static final SoundEvent SCREAM = registerSoundEvent("scream");
    public static final SoundEvent JUMPSCARE = registerSoundEvent("jumpscare");
    
    // Event-Specific Sounds
    public static final SoundEvent EVENT_IJOIN = registerSoundEvent("event_ijoin");
    public static final SoundEvent EVENT_WATCHING = registerSoundEvent("event_watching");
    public static final SoundEvent EVENT_GLITCH = registerSoundEvent("event_glitch");
    
    /**
     * Registers a sound event with the given name.
     * 
     * @param name The sound event's registry name
     * @return The registered SoundEvent
     */
    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = Identifier.of(IsRealAnything.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }
    
    /**
     * Initializes and registers all mod sounds.
     * Call this from the main mod initializer.
     */
    public static void registerModSounds() {
        IsRealAnything.LOGGER.info("Registering sounds for " + IsRealAnything.MOD_ID);
        
        // Sounds are registered via static initialization
        // This method serves as an entry point to ensure the class is loaded
    }
}
