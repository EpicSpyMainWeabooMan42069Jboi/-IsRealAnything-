package com.epicspymain.isrealanything.entity.client;

import com.epicspymain.isrealanything.IsRealAnything;
import com.epicspymain.isrealanything.entity.custom.TheOtherMEEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.base.GeoRenderState;


public class TheOtherMEEntityModel extends GeoModel<TheOtherMEEntity> {

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return Identifier.of(IsRealAnything.MOD_ID, "geo/the_other_me.geo.json");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return Identifier.of(IsRealAnything.MOD_ID, "textures/entity/TheMEOTHER/the_other_me.png");
    }

    @Override
    public Identifier getAnimationResource(GeoRenderState renderState) {
        return Identifier.of(IsRealAnything.MOD_ID, "animations/the_other_me.animation.json");
    }

}