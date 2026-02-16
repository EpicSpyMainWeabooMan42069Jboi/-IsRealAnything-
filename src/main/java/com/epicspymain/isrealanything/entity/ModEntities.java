import com.epicspymain.isrealanything.entity.ModEntities;
import com.epicspymain.isrealanything.IsRealAnything;
import com.epicspymain.isrealanything.screen;
import com.epicspymain.isrealanything.entity.custom.TheMEEntity;
import com.epicspymain.isrealanything.entity.custom.TheOtherMEEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * Registers all custom entities for IsRealAnything mod.
 * Includes horror-themed animated entities using GeckoLib.
 */
public class ModEntities {
    
    // TheME Entity - Primary horror entity
    public static final EntityType<TheMEEntity> THE_ME = Registry.register(
        Registries.ENTITY_TYPE,
        Identifier.of(IsRealAnything.MOD_ID, "the_me"),
        FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, TheMEEntity::new)
            .dimensions(EntityDimensions.fixed(0.6f, 1.95f))
            .build()
    );
    
    // TheOtherME Entity - Secondary horror entity
    public static final EntityType<TheOtherMEEntity> THE_OTHER_ME = Registry.register(
        Registries.ENTITY_TYPE,
        Identifier.of(IsRealAnything.MOD_ID, "the_other_me"),
        FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, TheOtherMEEntity::new)
            .dimensions(EntityDimensions.fixed(0.6f, 1.95f))
            .build()
    );
    
    /**
     * Initializes and registers all mod entities.
     * Call this from the main mod initializer.
     */
    public static void registerModEntities() {
        IsRealAnything.LOGGER.info("Registering entities for " + IsRealAnything.MOD_ID);
        
        // Entities are registered via static initialization
        // This method serves as an entry point to ensure the class is loaded


;


    }
}
