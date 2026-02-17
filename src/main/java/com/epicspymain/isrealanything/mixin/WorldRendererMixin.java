package com.epicspymain.isrealanything.mixin;

import com.epicspymain.isrealanything.event.FileNamesInChatEvent;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin 6: WorldRenderer - Sky and fog color manipulation
 * Enables persistent purple sky effect from FileNamesInChat event
 */
@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    
    @Shadow
    private ClientWorld world;
    
    /**
     * Override sky color to purple when event active
     */
    @Inject(
            method = "getSkyColor",
            at = @At("RETURN"),
            cancellable = true
    )
    private void getPurpleSkyColor(
            Vec3d cameraPos,
            float tickDelta,
            CallbackInfoReturnable<Vec3d> cir
    ) {
        if (FileNamesInChatEvent.isSkyColorChanged()) {
            // Purple sky color  (0x4B0082 - Indigo)

            double r = 0x4B / 255.0;
            double g = 0x00 / 255.0;
            double b = 0x82 / 255.0;
            cir.setReturnValue(new Vec3d(r, g, b));
        }
    }
    
    /**
     * Override fog color to dark purple
     */
    @Inject(
            method = "getFogColor",
            at = @At("RETURN"),
            cancellable = true
    )
    private void getPurpleFogColor(
            Vec3d cameraPos,
            float tickDelta,
            CallbackInfoReturnable<Vec3d> cir
    ) {
        if (FileNamesInChatEvent.isSkyColorChanged()) {
            // DEEPER purple fog (0x2D0057 - Almost black purple)
            double r = 0x2D / 255.0;
            double g = 0x00 / 255.0;
            double b = 0x57 / 255.0;

            cir.setReturnValue(new Vec3d(r, g, b));
        }
    }
