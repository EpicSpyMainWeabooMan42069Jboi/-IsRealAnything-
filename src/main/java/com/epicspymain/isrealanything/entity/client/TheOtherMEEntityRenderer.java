package com.epicspymain.isrealanything.entity.client;

import com.epicspymain.isrealanything.entity.custom.TheOtherMEEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.base.GeoRenderState;


public class TheMEEntityRenderer extends GeoEntityRenderer<TheMEEntity, GeoRenderState.Impl> {
    public TheMEEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new TheMEEntityModel());
    }
}