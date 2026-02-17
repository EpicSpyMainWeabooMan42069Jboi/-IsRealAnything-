package com.epicspymain.isrealanything.event;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * EVENT 34: DailyNotification - Subtle corner notifications
 * White text appears in screen corners
 * Sometimes shows "WHY D0 YOU HATE THIS?"
 * Subtle attention-grabber
 */
public class DailyNotificationEvent {
    
    private static final String[] NOTIFICATIONS = {
        "WHY D0 YOU HATE THIS?",
        "Are you having fun?",
        "I'm still here.",
        "Don't forget about me.",
        "Notice me.",
        "Look at me.",
        "Why do you ignore me?",
        "Am I not enough?"
    };
    
    /**
     * Trigger notification
     */
    public static void trigger(ServerPlayerEntity player) {
        String message = NOTIFICATIONS[player.getWorld().random.nextInt(NOTIFICATIONS.length)];
        
        // Send as action bar (bottom of screen)
        player.sendMessage(
            Text.literal(message)
                .formatted(Formatting.WHITE),
            true
        );
    }
}
