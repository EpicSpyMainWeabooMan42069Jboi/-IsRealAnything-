package com.epicspymain.isrealanything.mixin;

import com.epicspymain.isrealanything.events.FileNamesInChatEvent;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin: BackgroundRenderer - Purple sky/fog for FileNamesInChat event
 */
@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {

    @Inject(
            method = "applyFog",
            at = @At("TAIL")
    )
    private static void applyPurpleFog(
            Camera camera,
            BackgroundRenderer.FogType fogType,
            float viewDistance,
            boolean thickFog,
            float tickDelta,
            CallbackInfo ci
    ) {
        if (FileNamesInChatEvent.isSkyColorChanged()) {
            // Purple fog color (hex 9932CC)
            RenderSystem.clearColor(
                    0x99 / 255.0f,  // R
                    0x32 / 255.0f,  // G
                    0xCC / 255.0f,  // B
                    1.0f            // A
            );
        }
    }
}