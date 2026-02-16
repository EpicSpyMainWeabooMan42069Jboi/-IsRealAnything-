package com.epicspymain.isrealanything.item;

import com.epicspymain.isrealanything.IsRealAnything;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * Registers custom creative mode item groups for IsRealAnything mod.
 */
public class ModItemGroups {
    
    // Main item group for IsRealAnything
    public static final RegistryKey<ItemGroup> ISREALANYTHING_GROUP = RegistryKey.of(
        RegistryKeys.ITEM_GROUP,
        Identifier.of(IsRealAnything.MOD_ID, "isrealanything")
    );
    
    // Item group instance
    public static final ItemGroup ISREALANYTHING_ITEM_GROUP = FabricItemGroup.builder()
        .icon(() -> new ItemStack(ModItems.UNTITLED_DISK_SONG_MUSIC_DISC))
        .displayName(Text.translatable("itemgroup.isrealanything"))
        .entries((displayContext, entries) -> {
            // Add all mod items to the creative tab
            entries.add(ModItems.UNTITLED_DISK_SONG_MUSIC_DISC);
            
            // Add more items here as they are created
        })
        .build();
    
    /**
     * Registers all mod item groups.
     * Call this from the main mod initializer.
     */
    public static void registerItemGroups() {
        IsRealAnything.LOGGER.info("Registering item groups for " + IsRealAnything.MOD_ID);
        
        Registry.register(
            Registries.ITEM_GROUP,
            ISREALANYTHING_GROUP,
            ISREALANYTHING_ITEM_GROUP
        );
    }
}
