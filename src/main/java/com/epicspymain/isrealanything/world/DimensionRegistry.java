package com.epicspymain.isrealanything.world;

import com.epicspymain.isrealanything.IsRealAnything;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

/**
 * DimensionRegistry - Registers custom dimensions
 * Currently registers LIMBO dimension for sleep/transition events
 */
public class DimensionRegistry {
    
    /**
     * LIMBO_DIMENSION_KEY - Custom dimension for sleep events
     * Used by CalmBeforeStormEvent and ForcedWakeupEvent
     * Empty void world with single platform
     */
    public static final RegistryKey<World> LIMBO_DIMENSION_KEY = RegistryKey.of(
        RegistryKeys.WORLD,
        new Identifier(IsRealAnything.MOD_ID, "limbo")
    );


    /**
     * LIMBO_DIMENSION_TYPE - Dimension type configuration
     */
    public static final RegistryKey<DimensionType> LIMBO_DIMENSION_TYPE = RegistryKey.of(
        RegistryKeys.DIMENSION_TYPE,
        new Identifier(IsRealAnything.MOD_ID, "limbo")
    );
    
    /**
     * Register all custom dimensions
     */
    public static void registerDimensions() {
        IsRealAnything.LOGGER.info("Registering custom dimensions for " + IsRealAnything.MOD_ID);
        // Dimensions are registered through data packs in /data/isrealanything/dimension/
        // This class provides the registry keys for code reference
    }
    
    /**
     * Check if a world is the limbo dimension
     */
    public static boolean isLimboDimension(World world) {
        return world.getRegistryKey().equals(LIMBO_DIMENSION_KEY);
    }
}
