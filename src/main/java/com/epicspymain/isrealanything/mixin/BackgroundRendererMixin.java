package com.epicspymain.isrealanything.mixin;

import com.epicspymain.isrealanything.event.FileNamesInChatEvent;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.render.BackgroundRenderer")
public class BackgroundRendererMixin {

    @Inject(method = "applyFog", at = @At("TAIL"), require = 0)
    private static void applyPurpleFog(
            Camera camera,
            Object fogType,
            float viewDistance,
            boolean thickFog,
            float tickDelta,
            CallbackInfo ci
    ) {
        if (FileNamesInChatEvent.isSkyColorChanged()) {
            RenderSystem.clearColor(
                    0x2D / 255.0f,
                    0x00 / 255.0f,
                    0x57 / 255.0f,
                    1.0f
            );
        }
    }
}