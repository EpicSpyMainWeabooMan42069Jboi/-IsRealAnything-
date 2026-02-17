package com.epicspymain.isrealanything.mixin;

import com.epicspymain.isrealanything.event.TheOverlook;
import com.epicspymain.isrealanything.event.YouCouldHaveLeftEvent;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ChatMixin {

    
    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "onChatMessage", at = @At("HEAD"), cancellable = true)
    private void onChatMessage(ChatMessageC2SPacket packet, CallbackInfo ci) {
        String message = packet.chatMessage();

        // Intercept YES/NO for YouCouldHaveLeftEvent
        if (YouCouldHaveLeftEvent.handleChatInput(player, message)) {
            ci.cancel();
            return;
        }

        // Check for TheOverlook forbidden commands
        if (TheOverlook.checkForbiddenCommand(message)) {
            TheOverlook.trigger(player, player.getServer());
            ci.cancel();
        }
    }
}