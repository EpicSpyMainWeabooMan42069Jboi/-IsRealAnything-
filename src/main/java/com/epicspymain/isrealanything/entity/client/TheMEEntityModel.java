package com.epicspymain.isrealanything.entity.client;

import com.epicspymain.isrealanything.IsRealAnything;
import com.epicspymain.isrealanything.entity.custom.TheMEEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

/**
 * Model definition for TheME Entity.
 * Defines the geometry, texture, and animation files for the entity.
 */
public class TheMEEntityModel extends GeoModel<TheMEEntity> {
    
    @Override
    public Identifier getModelResource(TheMEEntity entity) {
        return Identifier.of(IsRealAnything.MOD_ID, "geo/the_me.geo.json");
    }
    
    @Override
    public Identifier getTextureResource(TheMEEntity entity) {
        return Identifier.of(IsRealAnything.MOD_ID, "textures/entity/the_me.png");
    }
    
    @Override
    public Identifier getAnimationResource(TheMEEntity entity) {
        return Identifier.of(IsRealAnything.MOD_ID, "animations/the_me.animation.json");
    }
}
