package com.epicspymain.isrealanything.event;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * EVENT 7: FakeDisconnectPopup - Shows fake disconnect message
 * Displays "Lost connection to server" but game continues normally
 */
public class FakeDisconnectPopupEvent {
    
    /**
     * Trigger fake disconnect message
     */
    public static void trigger(ServerPlayerEntity player) {
        // Send disconnect-looking message to player
        player.sendMessage(
            Text.literal("").append(
                Text.literal("Disconnected from Server").formatted(Formatting.RED, Formatting.BOLD)
            ).append(
                Text.literal("\n\nLost connection to the server").formatted(Formatting.GRAY)
            ).append(
                Text.literal("\n\nInternal Exception: java.io.IOException: Connection reset by peer")
                    .formatted(Formatting.DARK_GRAY)
            ),
            false
        );
        
        // Also send to action bar for more authenticity
        player.sendMessage(
            Text.literal("Connection Lost").formatted(Formatting.RED),
            true // Overlay message
        );
    }
}
