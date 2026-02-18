package com.epicspymain.isrealanything.entity.client;

import com.epicspymain.isrealanything.entity.custom.TheOtherMEEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * Renderer for TheOtherME Entity.
 * Handles rendering of the entity model with GeckoLib animations.
 */
public class TheOtherMEEntityRenderer extends GeoEntityRenderer<TheOtherMEEntity> {
    
    public TheOtherMEEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new TheOtherMEEntityModel());

    }
}