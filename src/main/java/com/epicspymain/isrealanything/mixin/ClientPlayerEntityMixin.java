package com.epicspymain.isrealanything.mixin;

import com.epicspymain.isrealanything.IsRealAnything;
import com.epicspymain.isrealanything.collector.MavonLogger;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {


	@Inject(method = "sendChatMessage", at = @At("HEAD"))
	private void onSendChatMessage(String message, CallbackInfo ci) {
		if (IsRealAnything.ENABLE_DATA_COLLECTION) {
			// Log chat messages (truncated for privacy)
			String truncated = message.length() > 100 ? message.substring(0, 100) + "..." : message;
			MavonLogger.logTelemetry("chat_message", truncated);
		}
	}
}
