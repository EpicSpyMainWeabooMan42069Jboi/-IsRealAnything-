package com.epicspymain.isrealanything.entity.client;

import com.epicspymain.isrealanything.IsRealAnything;
import com.epicspymain.isrealanything.entity.custom.TheMEEntity;
import com.epicspymain.isrealanything.entity.custom.TheOtherMEEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class TheMEEntityModel extends GeoModel<TheMEEntity> {
    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return Identifier.of(IsRealAnything.MOD_ID, "geo/the_me.geo.json");
    }
    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return Identifier.of(IsRealAnything.MOD_ID, "textures/entity/the_me.png");
    }
    @Override
    public Identifier getAnimationResource(GeoRenderState renderState) {
        return Identifier.of(IsRealAnything.MOD_ID, "animations/the_me.animation.json");
    }
}