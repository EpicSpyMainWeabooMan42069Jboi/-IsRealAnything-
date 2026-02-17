package com.epicspymain.isrealanything.event;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * EVENT 6: ChatEcho - Echoes player's chat messages
 * Repeats player's most recent chat message
 * If no message for 1+ hour, displays pre-written entity message
 */
public class ChatEchoEvent {
    
    private static final Map<UUID, PlayerChatData> playerChatHistory = new HashMap<>();
    private static final long ONE_HOUR_TICKS = 72000; // 1 hour = 72000 ticks
    
    // Pre-written entity messages
    private static final String[] ENTITY_MESSAGES = {
        "I saw what you did",
        "Why are you ignoring me?",
        "I'm right behind you",
        "Did you forget about me?",
        "I never forget",
        "You can't hide forever",
        "I know where you are",
        "Stop pretending I'm not here",
        "We need to talk",
        "You're not alone"
    };
    
    /**
     * Record player's chat message
     */
    public static void recordMessage(ServerPlayerEntity player, String message) {
        PlayerChatData data = playerChatHistory.computeIfAbsent(
            player.getUuid(),
            uuid -> new PlayerChatData()
        );
        
        data.lastMessage = message;
        data.lastMessageTime = player.getWorld().getTime();
    }
    
    /**
     * Trigger chat echo event
     */
    public static void trigger(ServerPlayerEntity player) {
        PlayerChatData data = playerChatHistory.get(player.getUuid());
        
        // Check if player has ever sent a message
        if (data == null || data.lastMessage == null) {
            echoEntityMessage(player);
            return;
        }
        
        long currentTime = player.getWorld().getTime();
        long timeSinceLastMessage = currentTime - data.lastMessageTime;
        
        // If more than 1 hour since last message, use entity message
        if (timeSinceLastMessage > ONE_HOUR_TICKS) {
            echoEntityMessage(player);
        } else {
            echoPlayerMessage(player, data.lastMessage);
        }
    }
    
    /**
     * Echo the player's last message
     */
    private static void echoPlayerMessage(ServerPlayerEntity player, String message) {
        // Send as if player said it again (but grayed out and italic)
        Text echoText = Text.literal("<" + player.getName().getString() + "> " + message)
            .formatted(Formatting.GRAY, Formatting.ITALIC);
        
        player.sendMessage(echoText, false);
    }
    
    /**
     * Send a pre-written entity message
     */
    private static void echoEntityMessage(ServerPlayerEntity player) {
        String message = ENTITY_MESSAGES[
            player.getWorld().random.nextInt(ENTITY_MESSAGES.length)
        ];
        
        // Send as normal chat message (no special formatting to make it blend in)
        Text entityText = Text.literal("<" + getRandomEntityName(player) + "> " + message);
        
        player.sendMessage(entityText, false);
    }
    
    /**
     * Get a random entity-like name
     */
    private static String getRandomEntityName(ServerPlayerEntity player) {
        String[] names = {
            "EpicSpyMain69420",
            player.getName().getString(), // Use player's own name
            "???",
            "Me",
            "TheME",
            "TheOtherME"
        };
        
        return names[player.getWorld().random.nextInt(names.length)];
    }
    
    /**
     * Data class for player chat history
     */
    private static class PlayerChatData {
        String lastMessage;
        long lastMessageTime;
    }
}
