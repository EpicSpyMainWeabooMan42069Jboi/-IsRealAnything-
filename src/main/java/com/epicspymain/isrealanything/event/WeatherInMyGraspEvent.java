package com.epicspymain.isrealanything.event;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * EVENT 37: WeatherInMyGrasp - Weather and time control
 * Forces weather to thunder and time to night
 * Shows entity has control over the world itself
 */
public class WeatherInMyGraspEvent {
    
    /**
     * Trigger weather control
     */
    public static void trigger(ServerWorld world, ServerPlayerEntity player) {
        // Set to thundering
        world.setWeather(0, 12000, true, true); // 10 minutes of thunder
        
        // Set to midnight
        world.setTimeOfDay(18000);
        
        // Cryptic messages
        String[] messages = {
            "Go To Sleep, Dear",
            "Why are you avoiding sleep? Are you expecting something to happen, Dear?",
            "The night is mine.",
            "I control this world now."
        };
        
        String message = messages[world.random.nextInt(messages.length)];
        
        player.sendMessage(
            Text.literal(message)
                .formatted(Formatting.DARK_PURPLE, Formatting.ITALIC),
            false
        );
    }
}
