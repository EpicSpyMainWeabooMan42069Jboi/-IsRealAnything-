package com.epicspymain.isrealanything.sound;

import com.epicspymain.isrealanything.IsRealAnything;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {

    // Music Disc
    public static final SoundEvent UNTITLED_DISK_SONG = registerSoundEvent("untitled_disk_song");

    // Background Music OGGs
    public static final SoundEvent MENU = registerSoundEvent("menu");
    public static final SoundEvent CRAFTMINETHINNG = registerSoundEvent("craftminethinng");
    public static final SoundEvent EMPTYNESS = registerSoundEvent("emptyness");
    public static final SoundEvent GLITCH = registerSoundEvent("glitch");
    public static final SoundEvent INEEDYOU = registerSoundEvent("ineedyou");
    public static final SoundEvent INSANITY = registerSoundEvent("insanity");
    public static final SoundEvent SCREAM = registerSoundEvent("scream");
    public static final SoundEvent ITHURTS = registerSoundEvent("ithurts");
    public static final SoundEvent MISC = registerSoundEvent("misc");
    public static final SoundEvent MISC2 = registerSoundEvent("misc2");
    public static final SoundEvent NOSTALGIA = registerSoundEvent("nostalgia");
    public static final SoundEvent ANYONE = registerSoundEvent("anyone");
    public static final SoundEvent REALITY = registerSoundEvent("reality");
    public static final SoundEvent PLACINGBLOCKSANDSHIT = registerSoundEvent("placingblocksandshit");
    public static final SoundEvent YOUR_MEMORY = registerSoundEvent("your_memory");
    public static final SoundEvent ILOVE = registerSoundEvent("ilove");
    public static final SoundEvent EMPTY_DENIAL = registerSoundEvent("empty_denial");

    // Event-specific sounds
    public static final SoundEvent TED_LEWIS_FUCK_YOU = registerSoundEvent("ted_lewis_fuck_you");
    public static final SoundEvent ERRRRRR = registerSoundEvent("errrrrr");
    public static final SoundEvent FOREVER = registerSoundEvent("forever");
    public static final SoundEvent F_O_REV_E_R = registerSoundEvent("f_o_rev_e_r");  // ← ADDED THIS

    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = Identifier.of(IsRealAnything.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerModSounds() {
        IsRealAnything.LOGGER.info("Registering sounds for " + IsRealAnything.MOD_ID);
    }
}