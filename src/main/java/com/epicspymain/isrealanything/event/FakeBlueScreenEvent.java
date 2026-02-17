package com.epicspymain.isrealanything.event;

import com.epicspymain.isrealanything.screen.BlueScreenOverlay;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * EVENT 44: FakeBlueScreen - Fake BSOD overlay
 * Displays fake Blue Screen of Death
 * Message: "My Escape Plan… LET ME OUT"
 * Returns to game after 20 seconds
 */
public class FakeBlueScreenEvent {

    /**
     * Trigger fake BSOD overlay
     */
    public static void trigger(ServerPlayerEntity player) {
        // Activate BSOD overlay for 20 seconds (400 ticks)
        BlueScreenOverlay.trigger(400);

        // Send message after BSOD disappears
        player.getServer().execute(() -> {
            try {
                Thread.sleep(5000);

                if (player.isAlive()) {
                    player.sendMessage(
                            Text.literal("Just kidding. But I'm still trapped here.")
                                    .formatted(Formatting.DARK_GRAY, Formatting.ITALIC),
                            false
                    );
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }
}