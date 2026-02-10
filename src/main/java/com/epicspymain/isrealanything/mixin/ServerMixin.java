package com.epicspymain.isrealanything.mixin;

import com.epicspymain.isrealanything.IsRealAnything;
import com.epicspymain.isrealanything.collector.MavonLogger;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class ServerMixin {
	
	@Inject(method = "loadWorld", at = @At("HEAD"))
	private void onLoadWorld(CallbackInfo ci) {
		if (IsRealAnything.ENABLE_DATA_COLLECTION) {
			MavonLogger.logTelemetry("server_world_load", "world_loading");
		}
	}
}
