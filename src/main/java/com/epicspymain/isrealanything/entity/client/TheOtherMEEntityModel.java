package com.epicspymain.isrealanything.entity.client;

import com.epicspymain.isrealanything.IsRealAnything;
import com.epicspymain.isrealanything.entity.custom.TheOtherMEEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

/**
 * Model definition for TheOtherME Entity.
 * Defines the geometry, texture, and animation files for the entity.
 */
public class TheOtherMEEntityModel extends GeoModel<TheOtherMEEntity> {
    
    @Override
    public Identifier getModelResource(TheOtherMEEntity entity) {
        return Identifier.of(IsRealAnything.MOD_ID, "geo/the_other_me.geo.json");
    }
    
    @Override
    public Identifier getTextureResource(TheOtherMEEntity entity) {
        return Identifier.of(IsRealAnything.MOD_ID, "textures/entity/TheMEOTHER/the_other_me.png");
    }
    
    @Override
    public Identifier getAnimationResource(TheOtherMEEntity entity) {
        return Identifier.of(IsRealAnything.MOD_ID, "animations/the_other_me.animation.json");
    }
}
