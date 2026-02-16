package com.epicspymain.isrealanything.mixin;

import com.epicspymain.isrealanything.events.FileNamesInChatEvent;
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
            // Purple sky color (0x9370DB)
            double r = 0x93 / 255.0;
            double g = 0x70 / 255.0;
            double b = 0xDB / 255.0;

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
            // Dark orchid fog (0x9932CC)
            double r = 0x99 / 255.0;
            double g = 0x32 / 255.0;
            double b = 0xCC / 255.0;

            cir.setReturnValue(new Vec3d(r, g, b));
        }
    }
