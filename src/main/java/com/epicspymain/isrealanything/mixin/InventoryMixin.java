package com.epicspymain.isrealanything.mixin;

import com.epicspymain.isrealanything.event.InventoryShuffleEvent;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(HandledScreen.class)
public class InventoryMixin {

    @Inject(method = "close", at = @At("HEAD"))
    private void onInventoryClose(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        int delayMs = 3000 + new java.util.Random().nextInt(5000);

        new Thread(() -> {
            try {
                Thread.sleep(delayMs);
                client.execute(() ->
                        InventoryShuffleEvent.triggerClientSide(client.player)
                );
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }