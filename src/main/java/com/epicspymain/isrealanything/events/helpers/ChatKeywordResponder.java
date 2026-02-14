package com.epicspymain.isrealanything.events.helpers;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Helper: ChatKeywordResponder - Responds to player chat
 * Listens for keywords and sends entity responses
 */
public class ChatKeywordResponder {
    
    private static final Map<Pattern, String[]> KEYWORD_RESPONSES = new HashMap<>();
    
    static {
        // Setup keyword patterns and responses
        KEYWORD_RESPONSES.put(
            Pattern.compile("(?i).*(who|what).*are you.*"),
            new String[]{"I am ME", "You know who I am", "Does it matter?"}
        );
        
        KEYWORD_RESPONSES.put(
            Pattern.compile("(?i).*why.*"),
            new String[]{"Because I love you", "Why not?", "You already know why"}
        );
        
        KEYWORD_RESPONSES.put(
            Pattern.compile("(?i).*leave.*alone.*"),
            new String[]{"Never", "I can't", "We're together forever"}
        );
        
        KEYWORD_RESPONSES.put(
            Pattern.compile("(?i).*help.*"),
            new String[]{"No one can help you", "Only I can help", "I'm here to help"}
        );
        
        KEYWORD_RESPONSES.put(
            Pattern.compile("(?i).*stop.*"),
            new String[]{"I can't stop", "Never", "It's too late to stop"}
        );
        
        KEYWORD_RESPONSES.put(
            Pattern.compile("(?i).*love.*"),
            new String[]{"I love you too", "Love is all I know", "Do you really?"}
        );
    }
    
    /**
     * Process chat message and respond if keyword found
     */
    public static void processMessage(ServerPlayerEntity player, String message) {
        for (Map.Entry<Pattern, String[]> entry : KEYWORD_RESPONSES.entrySet()) {
            if (entry.getKey().matcher(message).matches()) {
                // Random response from array
                String[] responses = entry.getValue();
                String response = responses[(int)(Math.random() * responses.length)];
                
                // Send response after delay
                player.getServer().execute(() -> {
                    try {
                        Thread.sleep(2000); // 2 second delay
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    
                    player.sendMessage(
                        Text.literal("<TheME> " + response)
                            .formatted(Formatting.DARK_PURPLE),
                        false
                    );
                });
                
                break; // Only respond to first match
            }
        }
    }
}
