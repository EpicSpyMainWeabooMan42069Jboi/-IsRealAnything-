package com.epicspymain.isrealanything.mixin;

import com.epicspymain.isrealanything.events.FileNamesInChatEvent;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin 1: BackgroundRenderer - Fog and sky rendering
 * Modifies fog/sky colors to allow purple sky persistence
 */
@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {
    
    /**
     * Apply purple fog color when FileNamesInChat event triggered
     */
    @Inject(
        method = "applyFog",
        at = @At("HEAD")
    )
    private static void applyPurpleFog(
        Camera camera,
        BackgroundRenderer.FogType fogType,
        float viewDistance,
        boolean thickFog,
        float tickDelta,
        CallbackInfo ci
    ) {
        // Check if purple sky is active
        if (FileNamesInChatEvent.isSkyColorChanged()) {
            // Purple fog color will be applied
            // Actual fog color modification happens in WorldRendererMixin
        }
    }
}
