package com.epicspymain.isrealanything.item;

import com.epicspymain.isrealanything.IsRealAnything;
import com.epicspymain.isrealanything.sound.ModSounds;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

/**
 * Registers all custom items for IsRealAnything mod.
 * Includes the custom music disc "Untitled Disk Song" and other items.
 */
public class ModItems {
    
    // Custom Music Disc - "Untitled Disk Song"
    public static final Item UNTITLED_DISK_SONG_MUSIC_DISC = registerItem(
        "untitled_disk_song_music_disc",
        new Item(new Item.Settings()
            .maxCount(1)
            .rarity(Rarity.RARE)
            .jukeboxPlayable(RegistryKey.of(RegistryKeys.JUKEBOX_SONG, 
                Identifier.of(IsRealAnything.MOD_ID, "untitled_disk_song")))
        )
    );
    
    /**
     * Registers an item with the given name.
     * 
     * @param name The item's registry name
     * @param item The item instance
     * @return The registered item
     */
    private static Item registerItem(String name, Item item) {
        return Registry.register(
            Registries.ITEM,
            Identifier.of(IsRealAnything.MOD_ID, name),
            item
        );
    }
    
    /**
     * Initializes and registers all mod items.
     * Call this from the main mod initializer.
     */
    public static void registerModItems() {
        IsRealAnything.LOGGER.info("Registering mod items for " + IsRealAnything.MOD_ID);
        
        // Items are registered via static initialization
        // This method serves as an entry point to ensure the class is loaded
    }
}
