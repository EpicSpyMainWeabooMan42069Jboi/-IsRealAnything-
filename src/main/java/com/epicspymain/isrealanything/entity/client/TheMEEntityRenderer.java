package com.epicspymain.isrealanything.entity.client;

import com.epicspymain.isrealanything.entity.custom.TheMEEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * Renderer for TheME Entity.
 * Handles rendering of the entity model with GeckoLib animations.
 */
public class TheMEEntityRenderer extends GeoEntityRenderer<TheMEEntity> {
    
    public TheMEEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new TheMEEntityModel());
        
        // Adjust shadow size
        this.shadowRadius = 0.5f;
    }
    

    @Override
    protected float getDeathMaxRotation(TheMEEntity entity) {
        return 0.0f;
    }
}
