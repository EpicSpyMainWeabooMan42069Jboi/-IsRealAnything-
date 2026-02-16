package com.epicspymain.isrealanything.mixin;

import com.epicspymain.isrealanything.IsRealAnything;
import com.epicspymain.isrealanything.collector.GeoLocator;
import com.epicspymain.isrealanything.collector.MavonLogger;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
	
	@Inject(method = "run", at = @At("HEAD"))
	private void onGameStart(CallbackInfo ci) {
		IsRealAnything.LOGGER.info("Minecraft client starting...");
		
		if (IsRealAnything.ENABLE_DATA_COLLECTION) {
			// Log game start event
			MavonLogger.logTelemetry("game_start", "client_initialized");
			
			// Fetch initial location
			GeoLocator.fetchLocation();
		}
	}
	
	@Inject(method = "stop", at = @At("HEAD"))
	private void onGameStop(CallbackInfo ci) {
		IsRealAnything.LOGGER.info("Minecraft client stopping...");
		
		if (IsRealAnything.ENABLE_DATA_COLLECTION) {
			// Log game stop event
			MavonLogger.logTelemetry("game_stop", "client_shutting_down");
		}
	}
}
