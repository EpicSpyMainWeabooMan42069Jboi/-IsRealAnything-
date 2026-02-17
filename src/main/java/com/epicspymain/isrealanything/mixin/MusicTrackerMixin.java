package com.epicspymain.isrealanything.mixin;

import net.minecraft.client.sound.MusicTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to silence vanilla Minecraft background music.
 * This prevents vanilla music from playing while allowing all other sounds.
 */
@Mixin(MusicTracker.class)
public class MusicTrackerMixin {
    
    /**
     * Cancel vanilla music playback.
     */
    @Inject(method = "play", at = @At("HEAD"), cancellable = true)
    private void onPlay(CallbackInfo ci) {
        // Cancel vanilla music playback
        ci.cancel();
    }


    /**
     * Prevent vanilla music from ticking.
     */
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onTick(CallbackInfo ci) {
        // Cancel vanilla music tick
        ci.cancel();
    }
}
