package com.epicspymain.isrealanything.entity;

import com.epicspymain.isrealanything.IsRealAnything;
import com.epicspymain.isrealanything.entity.custom.TheMEEntity;
import com.epicspymain.isrealanything.entity.custom.TheOtherMEEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {

    // ✅ Consistent with rest of codebase
    public static final EntityType<TheMEEntity> THEME_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(IsRealAnything.MOD_ID, "the_me"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, TheMEEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 1.95f))
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(IsRealAnything.MOD_ID, "the_me")))
    );

    public static final EntityType<TheOtherMEEntity> THEOTHERME_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(IsRealAnything.MOD_ID, "the_other_me"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, TheOtherMEEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 1.95f))
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(IsRealAnything.MOD_ID, "the_other_me")))
    );

    public static void registerModEntities() {
        IsRealAnything.LOGGER.info("Registering entities for " + IsRealAnything.MOD_ID);
    }
}